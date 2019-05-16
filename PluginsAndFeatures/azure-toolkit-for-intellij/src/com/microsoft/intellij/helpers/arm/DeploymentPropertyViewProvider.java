package com.microsoft.intellij.helpers.arm;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class DeploymentPropertyViewProvider implements FileEditorProvider {

    public static final String TYPE = "DEPLOYMENT_PROPERTY_VIEW";

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        return virtualFile.getFileType().getName().equals(TYPE);
    }

    @NotNull
    @Override
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        DeploymentPropertyView deploymentPropertyView = new DeploymentPropertyView();
        return deploymentPropertyView;
    }

    @NotNull
    @Override
    public String getEditorTypeId() {
        return TYPE;
    }

    @NotNull
    @Override
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
    }
}
