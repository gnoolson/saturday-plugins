package gnoolson.saturday.plugin.cache;

import gnoolson.saturday.plugin.cache.lua.CacheLuaModule;
import gnoolson.saturday_plugin_api.LuaModule;
import gnoolson.saturday_plugin_api.LuaModuleFactory;

public class LuaModuleFactoryImpl implements LuaModuleFactory {

    @Override
    public LuaModule getInstance() {
        return new CacheLuaModule();
    }

}
