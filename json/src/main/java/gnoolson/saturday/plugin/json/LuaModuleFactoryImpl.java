package gnoolson.saturday.plugin.json;

import gnoolson.saturday.plugin.json.lua.JSONLuaModule;
import gnoolson.saturday_plugin_api.LuaModule;
import gnoolson.saturday_plugin_api.LuaModuleFactory;

public class LuaModuleFactoryImpl implements LuaModuleFactory {

    private final JSONLuaModule jsonLuaModule = new JSONLuaModule();

    /*
     *
     *
     * */
    @Override
    public LuaModule getInstance() {
        return jsonLuaModule;
    }

}
