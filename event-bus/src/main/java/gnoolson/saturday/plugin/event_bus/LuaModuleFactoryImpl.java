package gnoolson.saturday.plugin.event_bus;

import gnoolson.saturday.plugin.event_bus.lua.EventBusLuaModule;
import gnoolson.saturday_plugin_api.LuaModule;
import gnoolson.saturday_plugin_api.LuaModuleFactory;

public class LuaModuleFactoryImpl implements LuaModuleFactory {

    @Override
    public LuaModule getInstance(String scriptId) {
        return new EventBusLuaModule(scriptId);
    }

}
