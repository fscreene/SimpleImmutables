package com.fscreene.simpleimmutables.processor;

import com.fscreene.simpleimmutables.processor.annotations.SafeField;
import com.fscreene.simpleimmutables.processor.annotations.SimpleImmutable;

@SimpleImmutable
public interface Smol {
    @SafeField
    String getName();
    int getSize();
    boolean getTruth();
}
