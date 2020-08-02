package com.fscreene.simpleimmutables.processor.model;

import com.squareup.javapoet.TypeName;

public class Field {
    private final TypeName type;
    private final String name;
    private final boolean safeField;

    public Field(TypeName type, String name, boolean safeField) {
        this.type = type;
        this.name = name;
        this.safeField = safeField;
    }

    public TypeName getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isSafeField() {
        return safeField;
    }
}
