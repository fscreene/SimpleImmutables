package com.fscreene.simpleimmutables.processor;

import com.fscreene.simpleimmutables.processor.annotations.SimpleImmutable;
import com.google.auto.service.AutoService;
import com.fscreene.simpleimmutables.processor.model.File;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

@SupportedAnnotationTypes("com.fscreene.simpleimmutables.processor.annotations.SimpleImmutable")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(Processor.class)
public class SimpleImmutableProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Generating SimpleImmutable Classes");
        roundEnvironment.getElementsAnnotatedWith(SimpleImmutable.class).forEach(b -> {
            File file = ClassBuilder.buildImmutable(b);
            try {
                JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(file.getName());
                Writer writer = sourceFile.openWriter();
                writer.write(file.getContent());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return true;
    }
}
