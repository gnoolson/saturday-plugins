package gnoolson.saturday.plugin.cache;

import gnoolson.saturday_plugin_api.Functionality;
import gnoolson.saturday_plugin_api.FunctionalityFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FunctionalityFactoryImpl implements FunctionalityFactory {

    private final Cache cache;

    /*
     *
     *
     * */
    @Override
    public Functionality getInstance() {
        return cache;
    }

}
