package gnoolson.saturday.plugin.http_client;

import gnoolson.saturday_plugin_api.Functionality;

public interface HttpClient extends Functionality {

    Response send(Request request);

}
