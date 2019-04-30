package com.microsoft.tooling.msservices.serviceexplorer.azure.arm;

import com.microsoft.azuretools.core.mvp.model.AzureMvpModel;
import com.microsoft.azuretools.core.mvp.ui.base.MvpPresenter;
import java.io.IOException;

public class ResourceManagementNodePresenter<V extends ResourceManagementNodeView> extends MvpPresenter<V> {

    public void onModuleRefresh(String sid, String rgName) throws IOException {
        final ResourceManagementNodeView view = getMvpView();
        if (view != null) {
            view.renderChildren(AzureMvpModel.getInstance().getDeploymentByRgName(sid, rgName));
        }
    }
}
