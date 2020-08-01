package com.notarealtree.simpleimmutables.processor;

import com.notarealtree.simpleimmutables.processor.model.BuilderContainer;
import com.squareup.javapoet.*;

import javax.annotation.processing.Generated;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.stream.Collectors;


public class Builder {
    public static File buildImmutable(Element element) {
        TypeName typeName = ClassName.get(element.asType());
        String immutableClassName = "Immutable" + ClassName.bestGuess(typeName.toString()).simpleName();
        List<? extends Element> methods = element.getEnclosedElements();
        List<Field> fields = methods.stream().map(method -> {
            TypeMirror typeMirror = method.asType();
            TypeName result = typeMirror.accept(new ImmutableTypeVisitor(), null);
            return new Field(result, parseFieldName(method.getSimpleName().toString()));
        }).collect(Collectors.toList());
        return new File(immutableClassName, buildImmutable(typeName, fields));
    }

    public static String buildImmutable(TypeName typeName, List<Field> fields) {
        String immutableClassName = "Immutable" + ClassName.bestGuess(typeName.toString()).simpleName();
        String immutableBuilderClassName = immutableClassName + "Builder";

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(immutableClassName);
        makePublicFinal(classBuilder);
        classBuilder.addAnnotation(getGeneratedAnnotation());

        addExtendsToClass(classBuilder, typeName);
        addFieldsToClass(classBuilder, fields);
        addConstructorToClass(classBuilder, fields);
        addGettersToClass(classBuilder, fields);
        addBuilderToClass(typeName, classBuilder, immutableClassName, immutableBuilderClassName, fields);
        JavaFile javaFile = JavaFile
                .builder(ClassName.bestGuess(typeName.toString()).packageName(), classBuilder.build())
                .build();

        return javaFile.toString();
    }

    private static void addExtendsToClass(TypeSpec.Builder classBuilder, TypeName typeName) {
        classBuilder.addSuperinterface(typeName);
    }

    private static void addGettersToClass(TypeSpec.Builder classBuilder, List<Field> fields) {
        fields.forEach(field -> {
            MethodSpec.Builder getterBuilder = MethodSpec.methodBuilder(
                    "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1));
            getterBuilder.addAnnotation(Override.class);
            getterBuilder.addModifiers(Modifier.PUBLIC);
            getterBuilder.returns(field.getType());
            getterBuilder.addStatement("return this.$L", field.getName());
            classBuilder.addMethod(getterBuilder.build());
        });
    }

    private static void addConstructorToClass(TypeSpec.Builder classBuilder, List<Field> fields) {
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder();
        constructorBuilder.addModifiers(Modifier.PUBLIC);
        fields.forEach(field -> {
            constructorBuilder.addParameter(field.getType(), field.getName());
            constructorBuilder.addStatement("this.$L = $L", field.getName(), field.getName());
        });
        classBuilder.addMethod(constructorBuilder.build());
    }

    private static void addFieldsToClass(TypeSpec.Builder classBuilder, List<Field> fields) {
        fields.forEach(field -> {
            classBuilder.addField(field.getType(), field.getName(), Modifier.PRIVATE, Modifier.FINAL);
        });
    }

    // This doesn't work if a method is called getInt() because the result will be int(). Need to fix.
    private static String parseFieldName(String fieldName) {
        if (fieldName.startsWith("get")) {
            return fieldName.substring(3,4).toLowerCase() + fieldName.substring(4);
        }
        return fieldName;
    }

    private static void makePublicFinal(TypeSpec.Builder builder) {
        builder.addModifiers(Modifier.PUBLIC, Modifier.FINAL);
    }

    private static AnnotationSpec getGeneratedAnnotation() {
        return AnnotationSpec
                .builder(Generated.class)
                .addMember("value", "\"SimpleImmutablesGenerator\"")
                .build();
    }

    private static void addBuilderToClass(
            TypeName typeName,
            TypeSpec.Builder classBuilder,
            String immutableClassName,
            String immutableBuilderClassName,
            List<Field> fields) {
        BuilderContainer builderContainer =
                new BuilderAssembler(typeName, immutableClassName, immutableBuilderClassName, fields).assemble();
        classBuilder.addMethod(builderContainer.getBuildMethod());
        classBuilder.addMethod(builderContainer.getFromMethod());
        classBuilder.addType(builderContainer.getBuilderType());
    }

}
