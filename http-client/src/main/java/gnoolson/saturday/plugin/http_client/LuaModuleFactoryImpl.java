package gnoolson.saturday.plugin.http_client;

import gnoolson.saturday.plugin.http_client.lua.HttpClientLuaModule;
import gnoolson.saturday_plugin_api.LuaModule;
import gnoolson.saturday_plugin_api.LuaModuleFactory;

public class LuaModuleFactoryImpl implements LuaModuleFactory {

    @Override
    public LuaModule getInstance() {
        return new HttpClientLuaModule();
    }

}
