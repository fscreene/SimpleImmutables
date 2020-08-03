package com.fscreene.simpleimmutables.processor;

import com.fscreene.simpleimmutables.processor.annotations.SafeField;
import com.fscreene.simpleimmutables.processor.model.Field;
import com.fscreene.simpleimmutables.processor.util.StringUtil;
import com.fscreene.simpleimmutables.processor.visitors.AnnotationVisitor;
import com.fscreene.simpleimmutables.processor.visitors.ImmutableTypeVisitor;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.util.List;

public class FieldParser {
    private final Element method;

    public FieldParser(Element method) {
        this.method = method;
    }

    public Field parse() {
        List<? extends AnnotationMirror> annotationMirrors = method.getAnnotationMirrors();
        boolean isSafeField = annotationMirrors.stream().anyMatch(mirror -> {
            String accept = mirror.getAnnotationType().asElement().accept(new AnnotationVisitor(), null);
            return accept.equals(SafeField.class.getCanonicalName());
        });
        TypeMirror typeMirror = method.asType();
        typeMirror.getAnnotationMirrors();
        TypeName result = typeMirror.accept(new ImmutableTypeVisitor(), null);
        return new Field(result, parseFieldName(method.getSimpleName().toString()), isSafeField);
    }

    // This doesn't work if a method is called getInt() because the result will be int(). Need to fix.
    private static String parseFieldName(String fieldName) {
        String parsedName = fieldName;
        if (fieldName.startsWith("get")) {
            parsedName = fieldName.substring(3);
        }
        return StringUtil.lowerFirstLetter(parsedName);
    }
}
