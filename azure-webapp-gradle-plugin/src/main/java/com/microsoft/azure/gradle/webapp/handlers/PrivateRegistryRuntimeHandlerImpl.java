/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.gradle.webapp.handlers;

import com.microsoft.azure.gradle.webapp.AzureWebAppExtension;
import com.microsoft.azure.gradle.webapp.DeployTask;
import com.microsoft.azure.gradle.webapp.configuration.AppService;
import com.microsoft.azure.gradle.webapp.helpers.WebAppUtils;
import com.microsoft.azure.management.appservice.AppServicePlan;
import com.microsoft.azure.management.appservice.OperatingSystem;
import com.microsoft.azure.management.appservice.WebApp;
import com.microsoft.azure.management.appservice.WebApp.DefinitionStages.WithCreate;
import com.microsoft.azure.management.appservice.WebApp.Update;
import org.gradle.api.GradleException;

public class PrivateRegistryRuntimeHandlerImpl implements RuntimeHandler {
    private DeployTask task;
    private AzureWebAppExtension extension;

    public PrivateRegistryRuntimeHandlerImpl(final DeployTask task) {
        this.task = task;
        this.extension = task.getAzureWebAppExtension();
    }

    @Override
    public WithCreate defineAppWithRuntime() throws Exception {
        final AppService appService = extension.getAppService();
        WebAppUtils.assureDockerSettingsValid(appService);
        final AppServicePlan plan = WebAppUtils.createOrGetAppServicePlan(task, OperatingSystem.LINUX);
        return WebAppUtils.defineLinuxApp(task, plan)
                .withPrivateRegistryImage(appService.getImageName(), appService.getRegistryUrl())
                .withCredentials(appService.getUsername(), appService.getPassword());
    }

    @Override
    public Update updateAppRuntime(final WebApp app) throws GradleException {
        WebAppUtils.assureLinuxWebApp(app);
        final AppService appService = extension.getAppService();
        WebAppUtils.assureDockerSettingsValid(appService);
        return app.update()
                .withPrivateRegistryImage(appService.getImageName(), appService.getRegistryUrl())
                .withCredentials(appService.getUsername(), appService.getPassword());
    }
}
