package com.microsoft.intellij.language.arm;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

public class ARMLanguage extends Language {

    public static final ARMLanguage INSTANCE = new ARMLanguage();
    public static final String MIME_TYPE = "application/x-template";
    public static final String MIME_TYPE2 = "application/template";
    public static final String ID = "arm";

    public ARMLanguage() {
        super(ID, MIME_TYPE, MIME_TYPE2);
    }

}
