package com.microsoft.tooling.msservices.serviceexplorer.azure.arm.deployments;

import com.microsoft.azure.management.resources.Deployment;
import com.microsoft.azuretools.core.mvp.ui.arm.DeploymentProperty;
import com.microsoft.azuretools.core.mvp.ui.base.MvpPresenter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeploymentPropertyViewPresenter<V extends DeploymentPropertyMvpView> extends MvpPresenter<V> {

    public void onLoadProperty(Deployment deployment) {
        List<String> parameters = new ArrayList<>();
        List<String> variables = new ArrayList<>();
        List<String> resources = new ArrayList<>();

        Map<String, Object> template = (Map<String, Object>) deployment.exportTemplate().template();

        Map<String, Map<String, String>> parametersTmp = (Map<String, Map<String, String>>) template.get("parameters");
        for (String parameter : parametersTmp.keySet()) {
            parameters.add(String.format("%s(%s)", parameter, parametersTmp.get(parameter).get("type")));
        }

        List<Map<String, String>> resourcesTmp = (List<Map<String, String>>) template.get("resources");
        for (Map<String, String> resource : resourcesTmp) {
            resources.add(String.format("%s(%s)", resource.get("name"), resource.get("type")));
        }

        getMvpView().onLoadProperty(new DeploymentProperty(deployment, parameters, variables, resources));
    }
}
