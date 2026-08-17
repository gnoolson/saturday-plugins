package gnoolson.saturday.plugin.http_client;

import gnoolson.saturday_plugin_api.SaturdayPlugin;
import lombok.Getter;

public class HttpClientPlugin extends SaturdayPlugin {

    @Getter
    private HttpClient httpClient;

    /*
     *
     *
     * */
    @Override
    public void start() {
        this.httpClient = new HttpClientImpl();
    }

}