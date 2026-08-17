package gnoolson.saturday.plugin.timer;

import gnoolson.saturday.plugin.timer.lua.TimerLuaModule;
import gnoolson.saturday_plugin_api.LuaModule;
import gnoolson.saturday_plugin_api.LuaModuleFactory;

public class LuaModuleFactoryImpl implements LuaModuleFactory {

    @Override
    public LuaModule getInstance() {
        return new TimerLuaModule();
    }

}
