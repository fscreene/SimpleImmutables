package com.fscreene.simpleimmutables.processor.visitors;

import javax.lang.model.element.*;

public class AnnotationVisitor implements ElementVisitor<String, Object> {
    @Override
    public String visit(Element element, Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String visitPackage(PackageElement packageElement, Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String visitType(TypeElement typeElement, Object o) {
        return typeElement.getQualifiedName().toString();
    }

    @Override
    public String visitVariable(VariableElement variableElement, Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String visitExecutable(ExecutableElement executableElement, Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String visitTypeParameter(TypeParameterElement typeParameterElement, Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String visitUnknown(Element element, Object o) {
        throw new UnsupportedOperationException();
    }
}
