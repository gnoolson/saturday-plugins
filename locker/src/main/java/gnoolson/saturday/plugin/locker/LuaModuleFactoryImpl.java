package gnoolson.saturday.plugin.locker;

import gnoolson.saturday.plugin.locker.lua.LockerModule;
import gnoolson.saturday_plugin_api.LuaModule;
import gnoolson.saturday_plugin_api.LuaModuleFactory;

public class LuaModuleFactoryImpl implements LuaModuleFactory {

    @Override
    public LuaModule getInstance() {
        return new LockerModule();
    }

}