package com.fscreene.simpleimmutables.processor.model;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

public class BuilderContainer {
    private final MethodSpec buildMethod;
    private final MethodSpec fromMethod;
    private final TypeSpec builderType;

    public BuilderContainer(MethodSpec builderMethod, MethodSpec fromMethod, TypeSpec builderType) {
        this.buildMethod = builderMethod;
        this.fromMethod = fromMethod;
        this.builderType = builderType;
    }

    public MethodSpec getBuildMethod() {
        return buildMethod;
    }

    public MethodSpec getFromMethod() {
        return fromMethod;
    }

    public TypeSpec getBuilderType() {
        return builderType;
    }
}
