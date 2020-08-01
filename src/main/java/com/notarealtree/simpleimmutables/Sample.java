package com.notarealtree.simpleimmutables;

import com.notarealtree.simpleimmutables.processor.SimpleImmutable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@SimpleImmutable
public interface Sample {
    String getStringValue();
    int getIntValue();
    List<String> getStringList();
    List<Sample> getSampleList();
    Set<String> getStringSet();
    Set<Sample> getSampleSet();
    Map<String, String> getStringStringMap();
    Map<String, Sample> getStringSampleMap();
    Optional<String> getStringOptional();
    Optional<Sample> getSampleOptional();
}
