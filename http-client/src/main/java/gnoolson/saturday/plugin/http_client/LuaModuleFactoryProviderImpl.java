package gnoolson.saturday.plugin.http_client;

import gnoolson.saturday_plugin_api.LuaModuleFactory;
import gnoolson.saturday_plugin_api.LuaModuleFactoryProvider;
import gnoolson.saturday_plugin_api.SaturdayPlugin;
import org.pf4j.Extension;

@Extension
public class LuaModuleFactoryProviderImpl implements LuaModuleFactoryProvider {

    private final LuaModuleFactory luaModuleFactory = new LuaModuleFactoryImpl();

    /*
     *
     *
     * */
    @Override
    public LuaModuleFactory getFactory() {
        return luaModuleFactory;
    }

    @Override
    public void setup(SaturdayPlugin saturdayPlugin) {
    }

}
