package com.fscreene.simpleimmutables.processor;

import com.fscreene.simpleimmutables.processor.model.BuilderContainer;
import com.fscreene.simpleimmutables.processor.model.Field;
import com.fscreene.simpleimmutables.processor.model.File;
import com.squareup.javapoet.*;

import javax.annotation.processing.Generated;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.stream.Collectors;


public class ClassBuilder {
    public static File buildImmutable(Element element) {
        TypeName typeName = ClassName.get(element.asType());
        String immutableClassName = "Immutable" + ClassName.bestGuess(typeName.toString()).simpleName();
        List<? extends Element> methods = element.getEnclosedElements();
        List<Field> fields = methods.stream()
                .map(method -> new FieldParser(method).parse())
                .collect(Collectors.toList());
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
        maybeAddToStringToClass(classBuilder, ClassName.bestGuess(typeName.toString()).simpleName(), fields);
        addBuilderToClass(typeName, classBuilder, immutableClassName, immutableBuilderClassName, fields);
        JavaFile javaFile = JavaFile
                .builder(ClassName.bestGuess(typeName.toString()).packageName(), classBuilder.build())
                .build();

        return javaFile.toString();
    }

    private static void maybeAddToStringToClass(TypeSpec.Builder classBuilder, String className, List<Field> fields) {
        new ToStringBuilder(fields, className).build().map(classBuilder::addMethod);
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
        fields.forEach(field ->
                classBuilder.addField(field.getType(), field.getName(), Modifier.PRIVATE, Modifier.FINAL));
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
                new ClassInternalBuilderBuilder(typeName, immutableClassName, immutableBuilderClassName, fields).assemble();
        classBuilder.addMethod(builderContainer.getBuildMethod());
        classBuilder.addMethod(builderContainer.getFromMethod());
        classBuilder.addType(builderContainer.getBuilderType());
    }

}
