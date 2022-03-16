package org.clinbioinfosspa.mmp.server.services;

import org.clinbioinfosspa.mmp.server.entities.Variant;
import org.clinbioinfosspa.mmp.server.repositories.RepositoryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class VariantService {

    @Autowired
    private SequenceService sequenceService;

    private final RepositoryFactory repositoryFactory = new RepositoryFactory();

    public VariantService() throws IOException {
    }

    public Variant normalize(Variant variant) {
        return grow(shrink(variant));
    }

    private Variant shrink(Variant variant) {
        var reference = variant.reference();
        var alternate = variant.alternate();
        int nref = reference.length();
        int nalt = alternate.length();

        while (nref > 0 && nalt > 0 && reference.charAt(nref - 1) == alternate.charAt(nalt - 1)) {
            --nref;
            --nalt;
        }
        int offset = 0;
        while (nref > 0 && nalt > 0 && reference.charAt(offset) == alternate.charAt(offset)) {
            ++offset;
            --nref;
            --nalt;
        }
        if (0 == offset && nref == reference.length() && nalt == alternate.length()) {
            return variant;
        } else {
            return new Variant(variant.sequenceId(), variant.position() + offset, reference.substring(offset, offset + nref), alternate.substring(offset, offset + nalt));
        }
    }

    private Variant grow(Variant variant) {
        var sequenceId = variant.sequenceId();
        var position = variant.position();
        var reference = variant.reference();
        var alternate = variant.alternate();
        if (reference.isEmpty() == alternate.isEmpty()) {
            return variant;
        } else {
            int nref = reference.length();
            int nalt = alternate.length();
            var bud = 0 == nalt ? reference : alternate;
            int nbud = Math.max(nref, nalt);
            int sigma = (int)nbud - (int)(position % (long)nbud);
            long s = position;
            for (; s > 0 && sequenceService.getReference(sequenceId, s - 1) == bud.charAt((int)((s + sigma - 1) % nbud)); --s);
            long e = position + nref;
            for (; /* e <= reference.length && */ sequenceService.getReference(sequenceId, e) == bud.charAt((int)((e + sigma) % nbud)); ++e);
            if (position == s && position + nref == e) {
                return variant;
            } else {
                int extension = (int)(e - s - nref);
                var newRef = new StringBuilder(nref + extension);
                var newAlt = new StringBuilder(nalt + extension);
                for (int i = 0; i < nref + extension; ++i) {
                    newRef.append(bud.charAt((int)((s + i + sigma) % nbud)));
                }
                for (int i = 0; i < nalt + extension; ++i) {
                    newAlt.append(bud.charAt((int)((s + i + sigma) % nbud)));
                }
                return new Variant(sequenceId, s, newRef.toString(), newAlt.toString());
            }
        }
    }

    public String getRefSnp(Variant variant) {
    }
}
