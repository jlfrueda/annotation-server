package org.clinbioinfosspa.mmp.server.entities;

public record Variant(String sequenceId, long position, String reference, String alternate) {
    @Override
    public String toString() {
        return sequenceId + ':' + position + ":" + reference + ":" + alternate;
    }
}
