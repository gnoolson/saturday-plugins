package gnoolson.saturday.plugin.serial;

import gnoolson.saturday.plugin.serial.lua.SerialLuaModule;
import gnoolson.saturday_plugin_api.LuaModule;
import gnoolson.saturday_plugin_api.LuaModuleFactory;

public class LuaModuleFactoryImpl implements LuaModuleFactory {

    @Override
    public LuaModule getInstance() {
        return new SerialLuaModule();
    }

}
