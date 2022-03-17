package org.clinbioinfosspa.mmp.server.common;

public class Nucleotide {
    private static final String VALID_NUCLEOTIDES = "ACGT";

    public static boolean compatible(char n1, char n2) {
        n1 = Character.toUpperCase(n1);
        n2 = Character.toUpperCase(n2);
        return n1 == n2 || -1 == VALID_NUCLEOTIDES.indexOf(n1) || -1 == VALID_NUCLEOTIDES.indexOf(n2);
    }

    public static boolean incompatible(char n1, char n2) {
        return !compatible(n1, n2);
    }
}
