package com.microsoft.intellij.language.arm.codeinsight;

import com.google.common.collect.Sets;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.json.psi.impl.JsonPropertyImpl;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.microsoft.intellij.language.arm.ARMLanguage;
import java.util.Arrays;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class ARMCompletionProvider extends CompletionProvider<CompletionParameters> {
    public static final ARMCompletionProvider INSTANCE = new ARMCompletionProvider();
    private static final Set<String> KEYWORDS = Sets.newConcurrentHashSet(Arrays.asList("$schema", "contentVersion",
        "parameters", "variables", "resources", "outputs"));
    private static final Set<String> RESOURCES = Sets.newConcurrentHashSet(Arrays.asList("type", "apiVersion", "name",
        "location", "sku", "properties"));
    private static final Set<String> PARAMETERS = Sets.newConcurrentHashSet();
    private static final Set<String> VARIABLES = Sets.newConcurrentHashSet();
    private static final Set<String> OUTPUTS = Sets.newConcurrentHashSet();
    private static final String[] SCOPES = new String[]{"parameters", "variables", "resources", "outputs"};

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context,
        @NotNull CompletionResultSet result) {
        long timeS = System.currentTimeMillis();
        try {
            if (parameters.getOriginalFile().getLanguage().getID().equals(ARMLanguage.ID)) {
                PsiElement position = parameters.getPosition();
                if (isProperty(position)) {
                    Scope scope = findScope(position);
                    if (scope == Scope.root) {
                        for (String keyword : KEYWORDS) {
                            result.addElement(LookupElementBuilder.create(keyword).bold());
                        }
                    } else if (scope == Scope.resources) {
                        for (String resource : RESOURCES) {
                            result.addElement(LookupElementBuilder.create(resource).bold());
                        }
                    } else if (scope == Scope.variables) {

                    }
                } else {

                }
            }
        } catch (Exception ignore) {
            System.out.println(ignore);
        }
        System.out.println("time cost: " + (System.currentTimeMillis() - timeS));
    }

    private boolean isProperty(PsiElement position) {
        PsiElement parentElement = position.getParent().getParent().getOriginalElement();
        if (parentElement instanceof JsonPropertyImpl) {
            if (((JsonPropertyImpl) parentElement).getValue() == null) {
                return true;
            }
            return !position.getText().equals(((JsonPropertyImpl) parentElement).getValue().getText());
        }
        return false;
    }

    private Scope findScope(PsiElement position) {
        try {
            while (position.getParent().getParent().getParent() != null) {
                position = position.getParent();
            }
            String positionName = ((JsonPropertyImpl) position).getName();
            return Scope.valueOf(positionName);
        } catch (Exception ignore) {
            return Scope.root;
        }
    }

    enum Scope {
        parameters, variables, resources, outputs, root
    }
}
