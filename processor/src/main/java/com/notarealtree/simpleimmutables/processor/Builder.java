package com.notarealtree.simpleimmutables.processor;

import com.squareup.javapoet.*;

import javax.annotation.processing.Generated;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Optional;
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

    private static void addBuilderToClass(TypeName typeName, TypeSpec.Builder classBuilder, String immutableClassName, String immutableBuilderClassName, List<Field> fields) {
        ClassName builderName = ClassName.get("", immutableBuilderClassName);

        MethodSpec.Builder builderStart = MethodSpec.methodBuilder("builder");
        builderStart.addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        builderStart.returns(builderName);
        builderStart.addStatement("return new $T()", builderName);
        classBuilder.addMethod(builderStart.build());

        MethodSpec.Builder builderFrom = MethodSpec.methodBuilder("from");
        builderFrom.addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        builderFrom.returns(builderName);
        builderFrom.addParameter(typeName, "existing");        builderFrom.addCode("return new $T(", builderName);
        Optional<String> reducer = fields.stream().map(field -> "existing.get" + field.getName().substring(0,1).toUpperCase() + field.getName().substring(1) + "()").reduce((i1, i2) -> i1 + ",\n" + i2);
        builderFrom.addCode(reducer.get());
        builderFrom.addStatement(")");
        classBuilder.addMethod(builderFrom.build());

        TypeSpec.Builder builder = TypeSpec.classBuilder(immutableBuilderClassName);
        builder.addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
        builder.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).build());
        fields.forEach(field -> {
            builder.addField(field.getType(), field.getName(), Modifier.PRIVATE);
        });

        // Parameterized Constructor
        MethodSpec.Builder parameterizedConstructorBuilder =
                MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);
        fields.forEach(field -> {
            parameterizedConstructorBuilder.addParameter(field.getType(), field.getName());
            parameterizedConstructorBuilder.addStatement("this.$L = $L", field.getName(), field.getName());
        });
        builder.addMethod(parameterizedConstructorBuilder.build());

        fields.forEach(field -> {
            MethodSpec.Builder withBuilder = MethodSpec.methodBuilder(
                    "with" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1));
            withBuilder.addParameter(field.getType(), field.getName());
            withBuilder.addStatement("this.$L = $L", field.getName(), field.getName());
            withBuilder.addStatement("return this");
            withBuilder.addModifiers(Modifier.PUBLIC);
            withBuilder.returns(builderName);
            builder.addMethod(withBuilder.build());
        });

        MethodSpec.Builder buildMethod = MethodSpec.methodBuilder("build");
        buildMethod.returns(ClassName.get("", immutableClassName));
        buildMethod.addModifiers(Modifier.PUBLIC);
        buildMethod.addCode("return new $L(\n", immutableClassName);
        Optional<String> reduce = fields.stream().map(field -> "    this." + field.getName()).reduce((i1, i2) -> i1 + ",\n" + i2);
        buildMethod.addCode(reduce.get());
        buildMethod.addStatement(")");

        builder.addMethod(buildMethod.build());

        classBuilder.addType(builder.build());
    }

}
