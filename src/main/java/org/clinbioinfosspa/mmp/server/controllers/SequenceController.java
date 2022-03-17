package org.clinbioinfosspa.mmp.server.controllers;

import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.clinbioinfosspa.mmp.server.common.SequenceTranslator;
import org.clinbioinfosspa.mmp.server.entities.Variant;
import org.clinbioinfosspa.mmp.server.models.Assembly;
import org.clinbioinfosspa.mmp.server.services.SequenceService;
import org.clinbioinfosspa.mmp.server.services.VariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;

@Log
@CrossOrigin(origins = "*", methods = {RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST, RequestMethod.DELETE})
@RestController
@RequestMapping(path = "/api/v1")
public class SequenceController {

    @Autowired
    private SequenceService sequenceService;

    @Autowired
    private VariantService variantService;

    @Value("${assemblies.cache.path}")
    private String asssemblyCachePath;

    private Assembly grch37;

    @PostConstruct
    public void setup() throws IOException {
        var path = Paths.get(asssemblyCachePath, "GCA_000001405.14");
        try (var stream = Files.newInputStream(path)) {
            this.grch37 = Assembly.parseFrom(stream);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Cannot load assemblies", e);
            assert false;
        }
    }


    @GetMapping("/assemblies/{assemblyId}")
    public ResponseEntity<Assembly> getAssembly(@PathVariable String assemblyId) {

        var path = String.format("/%s.proto", assemblyId);
        try (var stream = ClassLoader.class.getResourceAsStream(path)) {
            var assembly = Assembly.newBuilder().mergeFrom(stream).build();
            return ResponseEntity.ok(assembly);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/assemblies/{assemblyId}/variants/{sequenceId}/{position}/{reference}/{alternate}")
    public ResponseEntity<Variant> getVariant(@PathVariable String assemblyId, @PathVariable String sequenceId, @PathVariable long position, @PathVariable String reference, @PathVariable String alternate) {
        reference = StringUtils.trimToEmpty(reference);
        alternate = StringUtils.trimToEmpty(alternate);
        try {
            var variant = new Variant(sequenceId, position, reference, alternate);
            return ResponseEntity.ok(variantService.normalize(variant));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/sequences/{sequenceId}/{start}/{end}")
    public ResponseEntity<String> getReference(@PathVariable String sequenceId, @PathVariable long start, @PathVariable long end) {
        try {
            return ResponseEntity.ok(sequenceService.getReference(sequenceId, start, end));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "/dbsnp", produces = "text/plain")
    public ResponseEntity<String> handleFileUpload(@RequestBody String text) {
        var builder = new StringBuilder();
        var translator = new SequenceTranslator(grch37);
        text.lines().forEach(line -> {
            var fields = Arrays.stream(StringUtils.splitByWholeSeparatorPreserveAllTokens(line, ":")).map(s -> StringUtils.trimToEmpty(s)).map(s -> "-".equals(s) ? "" : s).toArray(String[]::new);
            if (4 != fields.length) {
                builder.append(line);
            } else {
                var sequenceName = fields[0];
                var position = Long.parseLong(fields[1]);
                var reference = fields[2];
                var alternate = fields[3];
                var orig = new Variant(sequenceName, position, reference, alternate);
                var sequenceId = translator.translate(sequenceName);
                var variant = new Variant(sequenceId, position - 1, reference, alternate);
                builder.append(orig).append("\t");
                if (sequenceService.checkReference(variant.sequenceId(), variant.position(), variant.reference())) {
                    variant = variantService.normalize(variant);
                    builder.append(variant);
                    try {
                        String refSnp = variantService.getRefSnp(variant);
                        builder.append("\t").append(refSnp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    builder.append("REFERENCE ERROR: ").append(variant.sequenceId()).append(":").append(variant.position()).append(" = ").append(sequenceService.getReference(sequenceId, variant.position(), variant.position() + variant.reference().length()));
                }
            }
            builder.append("\n");
        });
        return ResponseEntity.ok(builder.toString());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception exc) {
        log.log(Level.WARNING, exc.getMessage(), exc);
        return ResponseEntity.internalServerError().build();
    }
}
