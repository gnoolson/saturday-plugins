package gnoolson.saturday.plugin.event_bus;

import gnoolson.saturday_plugin_api.FunctionalityFactory;
import gnoolson.saturday_plugin_api.FunctionalityFactoryProvider;
import gnoolson.saturday_plugin_api.SaturdayPlugin;
import org.pf4j.Extension;

@Extension
public class FunctionalityFactoryProviderImpl implements FunctionalityFactoryProvider {

    private FunctionalityFactory functionalityFactory;

    /*
     *
     *
     * */
    @Override
    public FunctionalityFactory getFactory() {
        return functionalityFactory;
    }

    @Override
    public void setup(SaturdayPlugin saturdayPlugin) {
        EventBusPlugin eventBusPlugin = (EventBusPlugin) saturdayPlugin;
        this.functionalityFactory = new FunctionalityFactoryImpl(eventBusPlugin.getEventBus());
    }

}
