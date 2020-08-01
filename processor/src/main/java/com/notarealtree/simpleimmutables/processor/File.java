package com.notarealtree.simpleimmutables.processor;

public class File {
    private final String name;
    private final String content;

    public File(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }
}
