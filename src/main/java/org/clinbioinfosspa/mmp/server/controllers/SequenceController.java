package org.clinbioinfosspa.mmp.server.controllers;

import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.clinbioinfosspa.mmp.server.entities.Variant;
import org.clinbioinfosspa.mmp.server.models.Assembly;
import org.clinbioinfosspa.mmp.server.services.SequenceService;
import org.clinbioinfosspa.mmp.server.services.VariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

@Log
@CrossOrigin(origins = "*", methods = {RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST, RequestMethod.DELETE})
@RestController
@RequestMapping(path="/api/v1")
public class SequenceController {

    @Autowired
    private SequenceService sequenceService;

    @Autowired
    private VariantService variantService;

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

    @PostMapping("/files")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        var builder = new StringBuilder();
        try (var stream = file.getInputStream(); var reader = new BufferedReader(new InputStreamReader(stream))) {
            for (var line = reader.readLine(); null != line; line = reader.readLine()) {
                line = StringUtils.trimToEmpty(line);
                if (StringUtils.isNotBlank(line)) {
                    var fields = Arrays.stream(StringUtils.splitByWholeSeparatorPreserveAllTokens(line, ":")).map(s -> StringUtils.trimToEmpty(s)).map(s -> "-".equals(s) ? "" : s).toArray(String[]::new);
                    if (4 != fields.length) {
                        builder.append(line).append("\n");
                    } else {
                        var sequenceId = fields[0];
                        var position = Long.parseLong(fields[1]) - 1;
                        var reference = fields[2];
                        var alternate = fields[3];
                        var orig = new Variant(sequenceId, position, reference, alternate);
                        var variant = variantService.normalize(orig);
                        var refSnp = variantService.getRefSnp(variant);





                    }
                }
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception exc) {
        return ResponseEntity.internalServerError().build();
    }
}
