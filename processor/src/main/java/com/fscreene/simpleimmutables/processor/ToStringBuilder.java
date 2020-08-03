package com.fscreene.simpleimmutables.processor;

import com.fscreene.simpleimmutables.processor.model.Field;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ToStringBuilder {
    private final List<Field> fields;
    private final String className;

    public ToStringBuilder(List<Field> fields, String className) {
        this.fields = fields;
        this.className = className;
    }

    public Optional<MethodSpec> build() {
        List<Field> safeFields = fields.stream().filter(Field::isSafeField).collect(Collectors.toList());
        if (safeFields.isEmpty()) {
            return Optional.empty();
        }

        MethodSpec.Builder toStringBuilder = MethodSpec.methodBuilder("toString")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .returns(TypeName.get(String.class))
                .addAnnotation(Override.class);

        toStringBuilder.addCode("return \"" + className +"{\" +\n ");
        Optional<String> reduce = fields.stream()
                .map(field -> "\"" + field.getName() + "='\" + " + field.getName() + " + \"'\" +")
                .reduce((i1, i2) -> i1 + "\",\" +\n " + i2);
        toStringBuilder.addCode(reduce.get() +"\n");
        toStringBuilder.addStatement("\"}\"");

        return Optional.of(toStringBuilder.build());
    }
}
