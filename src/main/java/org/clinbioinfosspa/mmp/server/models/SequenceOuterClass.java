// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: sequence.proto

package org.clinbioinfosspa.mmp.server.models;

public final class SequenceOuterClass {
  private SequenceOuterClass() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_clinbioinfosspa_mmp_Sequence_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_clinbioinfosspa_mmp_Sequence_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\016sequence.proto\022\023clinbioinfosspa.mmp\"G\n" +
      "\010Sequence\022\n\n\002id\030\001 \002(\t\022\017\n\007genbank\030\002 \002(\t\022\016" +
      "\n\006refseq\030\003 \001(\t\022\016\n\006length\030\004 \002(\003B)\n%org.cl" +
      "inbioinfosspa.mmp.server.modelsP\001"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_clinbioinfosspa_mmp_Sequence_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_clinbioinfosspa_mmp_Sequence_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_clinbioinfosspa_mmp_Sequence_descriptor,
        new java.lang.String[] { "Id", "Genbank", "Refseq", "Length", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}