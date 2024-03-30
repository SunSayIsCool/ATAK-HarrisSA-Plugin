
package com.atakmap.android.harrissaspr.plugin;

import com.atak.plugins.impl.AbstractPlugin;
import com.atak.plugins.impl.PluginContextProvider;
import com.atakmap.android.harrissaspr.HarrisSaSprMapComponent;
import gov.tak.api.plugin.IServiceController;
import gov.tak.api.plugin.IPlugin;


public class PluginLifecycle extends AbstractPlugin implements IPlugin {

    public PluginLifecycle(IServiceController serviceController) {
        super(serviceController, new PluginTemplateTool(serviceController.getService(PluginContextProvider.class).getPluginContext()), new HarrisSaSprMapComponent());
    }
}