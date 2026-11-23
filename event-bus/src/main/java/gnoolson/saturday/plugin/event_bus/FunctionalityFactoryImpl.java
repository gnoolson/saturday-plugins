package gnoolson.saturday.plugin.event_bus;

import gnoolson.saturday_plugin_api.Functionality;
import gnoolson.saturday_plugin_api.FunctionalityFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FunctionalityFactoryImpl implements FunctionalityFactory {

    private final EventBus eventBus;

    /*
     *
     *
     * */
    @Override
    public Functionality getInstance() {
        return eventBus;
    }

}
