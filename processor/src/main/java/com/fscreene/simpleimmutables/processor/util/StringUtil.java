package com.fscreene.simpleimmutables.processor.util;

import com.google.common.base.Preconditions;

public class StringUtil {
    public static String lowerFirstLetter(String input) {
        Preconditions.checkArgument(input.length() > 2);
        return input.substring(0, 1).toLowerCase() + input.substring(1);
    }
}
