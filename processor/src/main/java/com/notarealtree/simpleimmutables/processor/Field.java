package com.notarealtree.simpleimmutables.processor;

import com.squareup.javapoet.TypeName;

public class Field {
    private final TypeName type;
    private final String name;

    public Field(TypeName type, String name) {
        this.type = type;
        this.name = name;
    }

    public TypeName getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
