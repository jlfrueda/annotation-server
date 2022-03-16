package org.clinbioinfosspa.mmp.server.common;

import org.apache.commons.lang3.StringUtils;
import org.clinbioinfosspa.mmp.server.models.Assembly;

import java.util.HashMap;
import java.util.Map;

public class SequenceTranslator {
    private final Assembly assembly;
    private final Map<String, String> map = new HashMap<>();

    public SequenceTranslator(Assembly assembly) {
        this.assembly = assembly;
        int numSequences = assembly.getSequenceCount();
        for (int idx = 0; idx < numSequences; ++idx) {
            var sequence = assembly.getSequence(idx);
            var sequenceId = sequence.getSequence().getRefseq();
            addName(sequence.getName(), sequenceId);
            addName(sequence.getUcscName(), sequenceId);
            addName(sequence.getSequence().getGenbank(), sequenceId);
            addName(sequence.getSequence().getRefseq(), sequenceId);
        }
    }

    public String translate(String name) {
        return map.getOrDefault(name, name);
    }

    private void addName(String name, String id) {
        if (StringUtils.isNoneBlank(name, id)) {
            map.put(name, id);
        }
    }
}
