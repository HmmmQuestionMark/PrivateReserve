package me.hqm.document;

public enum SupportedFormat {
    JSON("json"),
    MESSAGEPACK("mpk.sz");

    private final String fileExt;

    SupportedFormat(String fileExt) {
        this.fileExt = fileExt;
    }

    public String getExt() {
        return fileExt;
    }
}
