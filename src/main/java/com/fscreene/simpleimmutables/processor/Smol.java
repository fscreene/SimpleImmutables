package com.fscreene.simpleimmutables.processor;

import com.fscreene.simpleimmutables.processor.SimpleImmutable;

@SimpleImmutable
public interface Smol {
    String getName();
    int getSize();
    boolean getTruth();
}
