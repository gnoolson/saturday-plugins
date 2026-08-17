package gnoolson.saturday.plugin.http_client;

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
        HttpClientPlugin httpClientPlugin = (HttpClientPlugin) saturdayPlugin;
        functionalityFactory = new FunctionalityFactoryImpl(httpClientPlugin.getHttpClient());
    }

}
