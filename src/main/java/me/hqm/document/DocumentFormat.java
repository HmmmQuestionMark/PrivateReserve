package me.hqm.document;

public interface DocumentFormat<D extends DocumentCompatible> {
    String name();

    byte[] toRaw(D document);

    void write(D document);

    D fromRaw(byte[] raw);
}
