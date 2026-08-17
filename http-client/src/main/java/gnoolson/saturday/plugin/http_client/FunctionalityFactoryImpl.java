package gnoolson.saturday.plugin.http_client;

import gnoolson.saturday_plugin_api.Functionality;
import gnoolson.saturday_plugin_api.FunctionalityFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FunctionalityFactoryImpl implements FunctionalityFactory {

    private final HttpClient httpClient;

    /*
     *
     *
     * */
    @Override
    public Functionality getInstance() {
        return httpClient;
    }

}
