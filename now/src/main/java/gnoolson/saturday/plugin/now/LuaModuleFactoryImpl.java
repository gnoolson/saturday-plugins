package gnoolson.saturday.plugin.now;

import gnoolson.saturday.plugin.now.lua.NowLuaModule;
import gnoolson.saturday_plugin_api.LuaModule;
import gnoolson.saturday_plugin_api.LuaModuleFactory;

public class LuaModuleFactoryImpl implements LuaModuleFactory {

    @Override
    public LuaModule getInstance() {
        return new NowLuaModule();
    }

}
