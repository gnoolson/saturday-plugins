package gnoolson.saturday.plugin.db;

import gnoolson.saturday_plugin_api.Functionality;
import gnoolson.saturday_plugin_api.FunctionalityFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FunctionalityFactoryImpl implements FunctionalityFactory {

    private final DB db;

    /*
     *
     *
     * */
    @Override
    public Functionality getInstance() {
        return db;
    }

}
