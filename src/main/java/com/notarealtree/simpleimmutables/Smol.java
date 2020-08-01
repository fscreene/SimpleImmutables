package com.notarealtree.simpleimmutables;

import com.notarealtree.simpleimmutables.processor.SimpleImmutable;

@SimpleImmutable
public interface Smol {
    String getName();
    int getSize();
    boolean getTruth();
}
