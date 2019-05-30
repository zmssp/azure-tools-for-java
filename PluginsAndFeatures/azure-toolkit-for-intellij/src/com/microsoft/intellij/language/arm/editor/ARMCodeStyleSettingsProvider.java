package com.microsoft.intellij.language.arm.editor;

import com.intellij.json.formatter.JsonCodeStyleSettingsProvider;
import com.intellij.openapi.options.Configurable;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import org.jetbrains.annotations.Nullable;

public class ARMCodeStyleSettingsProvider extends CodeStyleSettingsProvider {

    private final JsonCodeStyleSettingsProvider provider;

    public ARMCodeStyleSettingsProvider() {
        provider = new JsonCodeStyleSettingsProvider();
    }

    public Configurable createSettingsPage(CodeStyleSettings settings, CodeStyleSettings originalSettings) {
        return provider.createSettingsPage(settings, originalSettings);
    }

    @Nullable
    public String getConfigurableDisplayName() {
        return provider.getConfigurableDisplayName();
    }

    @Nullable
    public CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings) {
        return provider.createCustomSettings(settings);
    }
}
