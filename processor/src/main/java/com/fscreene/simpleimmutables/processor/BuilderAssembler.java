package com.fscreene.simpleimmutables.processor;

import com.fscreene.simpleimmutables.processor.model.BuilderContainer;
import com.fscreene.simpleimmutables.processor.model.Field;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.Optional;

public class BuilderAssembler {
    private final TypeName typeName;
    private final String immutableClassName;
    private final String immutableBuilderClassName;
    private final List<Field> fields;
    private final ClassName builderName;

    public BuilderAssembler(
            TypeName typeName,
            String immutableClassName,
            String immutableBuilderClassName,
            List<Field> fields) {
        this.typeName = typeName;
        this.immutableClassName = immutableClassName;
        this.immutableBuilderClassName = immutableBuilderClassName;
        this.fields = fields;
        this.builderName = ClassName.get("", immutableBuilderClassName);
    }

    public BuilderContainer assemble() {
        TypeSpec.Builder builder = TypeSpec.classBuilder(immutableBuilderClassName);
        builder.addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);

        fields.forEach(field -> builder.addField(field.getType(), field.getName(), Modifier.PRIVATE));

        builder.addMethod(createZeroArgsConstructor());
        builder.addMethod(createParameterizedConstructor());

        fields.forEach(field -> builder.addMethod(createAssignmentMethodForField(field)));

        builder.addMethod(createBuildMethod());
        return new BuilderContainer(createBuilderMethod(), createFromMethod(), builder.build());
    }

    private MethodSpec createBuildMethod() {
        MethodSpec.Builder buildMethod = MethodSpec.methodBuilder("build");
        buildMethod.returns(ClassName.get("", immutableClassName));
        buildMethod.addModifiers(Modifier.PUBLIC);
        buildMethod.addCode("return new $L(\n", immutableClassName);
        Optional<String> reduce = fields.stream()
                .map(field -> "    this." + field.getName())
                .reduce((i1, i2) -> i1 + ",\n" + i2);
        buildMethod.addCode(reduce.get());
        buildMethod.addStatement(")");
        return buildMethod.build();
    }

    private MethodSpec createBuilderMethod() {
        MethodSpec.Builder builderMethod = MethodSpec.methodBuilder("builder");
        builderMethod.addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        builderMethod.returns(builderName);
        builderMethod.addStatement("return new $T()", builderName);
        return builderMethod.build();
    }

    private MethodSpec createFromMethod() {
        MethodSpec.Builder builderFrom = MethodSpec.methodBuilder("from");
        builderFrom.addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        builderFrom.returns(builderName);
        builderFrom.addParameter(typeName, "existing");
        builderFrom.addCode("return new $T(", builderName);
        Optional<String> reducer = fields.stream()
                .map(field -> "existing.get" + field.getName().substring(0,1).toUpperCase() + field.getName().substring(1) + "()")
                .reduce((i1, i2) -> i1 + ",\n" + i2);
        builderFrom.addCode(reducer.get());
        builderFrom.addStatement(")");
        return builderFrom.build();
    }

    private MethodSpec createZeroArgsConstructor() {
        return MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).build();
    }

    private MethodSpec createParameterizedConstructor() {
        MethodSpec.Builder parameterizedConstructorBuilder =
                MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);
        fields.forEach(field -> {
            parameterizedConstructorBuilder.addParameter(field.getType(), field.getName());
            parameterizedConstructorBuilder.addStatement("this.$L = $L", field.getName(), field.getName());
        });
        return parameterizedConstructorBuilder.build();
    }

    private MethodSpec createAssignmentMethodForField(Field field) {
        MethodSpec.Builder withBuilder = MethodSpec.methodBuilder(
                "with" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1));
        withBuilder.addParameter(field.getType(), field.getName());
        withBuilder.addStatement("this.$L = $L", field.getName(), field.getName());
        withBuilder.addStatement("return this");
        withBuilder.addModifiers(Modifier.PUBLIC);
        withBuilder.returns(builderName);
        return withBuilder.build();
    }

}
