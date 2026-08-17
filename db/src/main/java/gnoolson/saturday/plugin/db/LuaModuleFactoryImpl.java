package gnoolson.saturday.plugin.db;

import gnoolson.saturday.plugin.db.lua.DBLuaModule;
import gnoolson.saturday_plugin_api.LuaModule;
import gnoolson.saturday_plugin_api.LuaModuleFactory;

public class LuaModuleFactoryImpl implements LuaModuleFactory {

    @Override
    public LuaModule getInstance() {
        return new DBLuaModule();
    }

}
