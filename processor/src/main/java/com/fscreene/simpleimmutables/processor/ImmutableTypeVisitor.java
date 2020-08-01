package com.fscreene.simpleimmutables.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.type.*;

public class ImmutableTypeVisitor implements TypeVisitor<TypeName, TypeName> {
    @Override
    public TypeName visit(TypeMirror typeMirror, TypeName typeName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeName visitPrimitive(PrimitiveType primitiveType, TypeName typeName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeName visitNull(NullType nullType, TypeName typeName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeName visitArray(ArrayType arrayType, TypeName typeName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeName visitDeclared(DeclaredType declaredType, TypeName typeName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeName visitError(ErrorType errorType, TypeName typeName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeName visitTypeVariable(TypeVariable typeVariable, TypeName typeName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeName visitWildcard(WildcardType wildcardType, TypeName typeName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeName visitExecutable(ExecutableType executableType, TypeName typeName) {
        return ClassName.get(executableType.getReturnType());
    }

    @Override
    public TypeName visitNoType(NoType noType, TypeName typeName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeName visitUnknown(TypeMirror typeMirror, TypeName typeName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeName visitUnion(UnionType unionType, TypeName typeName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeName visitIntersection(IntersectionType intersectionType, TypeName typeName) {
        throw new UnsupportedOperationException();
    }
}
