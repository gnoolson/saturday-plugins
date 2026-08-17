package gnoolson.saturday.plugin.cache.lua;

import gnoolson.luaj_utils.LuaArgUtils;
import gnoolson.saturday.plugin.cache.Cache;
import gnoolson.saturday_plugin_api.Functionality;
import gnoolson.saturday_plugin_api.LuaModule;
import lombok.RequiredArgsConstructor;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.Collection;
import java.util.Optional;

public class CacheLuaModule extends LuaModule {

    private Cache cache;

    /*
     *
     *
     * */
    public CacheLuaModule() {
        set("get", new GetFunction(this));
        set("getAndProlong", new GetAndProlongFunction(this));
        set("put", new PutFunction(this));
        set("remove", new RemoveFunction(this));
        set("getKeys", new GetKeysFunction(this));
    }

    @Override
    public void updateFunctionality(Functionality functionality) {
        this.cache = (Cache) functionality;
    }

    @Override
    public void release() {

    }

    @Override
    public String name() {
        return "cache";
    }

    /*
     *
     *
     * */
    @RequiredArgsConstructor
    public static class GetFunction extends OneArgFunction {

        private final CacheLuaModule cacheLuaModule;

        @Override
        public LuaValue call(LuaValue keyLuaValue) {
            String key = LuaArgUtils.getStringFromFunctionArgs(keyLuaValue, 1, "key");
            Optional<Object> dataOpt = cacheLuaModule.cache.get(key);
            return dataOpt.map(o -> (LuaValue) o).orElse(LuaValue.NIL);
        }
    }

    @RequiredArgsConstructor
    public static class GetAndProlongFunction extends OneArgFunction {

        private final CacheLuaModule cacheLuaModule;

        @Override
        public LuaValue call(LuaValue keyLuaValue) {
            String key = LuaArgUtils.getStringFromFunctionArgs(keyLuaValue, 1, "key");
            Optional<Object> dataOpt = cacheLuaModule.cache.getAndProlong(key);
            return dataOpt.map(o -> (LuaValue) o).orElse(LuaValue.NIL);
        }
    }

    @RequiredArgsConstructor
    public static class PutFunction extends VarArgFunction {

        private final CacheLuaModule cacheLuaModule;

        @Override
        public Varargs invoke(Varargs args) {
            String key = LuaArgUtils.getStringFromFunctionArgs(LuaArgUtils.getLuaValueFromFunctionVarargs(args, 1, "key"), 1, "key");
            LuaValue data = LuaArgUtils.getLuaValueFromFunctionVarargs(args, 2, "data");
            long storageTime = LuaArgUtils.getLongFromFunctionArgs(LuaArgUtils.getLuaValueFromFunctionVarargs(args, 3, "storageTime"), 3, "storageTime");

            cacheLuaModule.cache.put(key, data, storageTime);
            return LuaValue.NIL;
        }
    }

    @RequiredArgsConstructor
    public static class RemoveFunction extends OneArgFunction {

        private final CacheLuaModule cacheLuaModule;

        @Override
        public LuaValue call(LuaValue keyLuaValue) {
            String key = LuaArgUtils.getStringFromFunctionArgs(keyLuaValue, 1, "key");
            return LuaBoolean.valueOf(cacheLuaModule.cache.remove(key));
        }
    }

    @RequiredArgsConstructor
    public static class GetKeysFunction extends ZeroArgFunction {
        private final CacheLuaModule cacheLuaModule;

        @Override
        public LuaValue call() {
            LuaTable result = new LuaTable();

            Collection<String> keys = cacheLuaModule.cache.getKeys();

            int index = 1;
            for (String key : keys) {
                result.set(index++, key);
            }

            return result;
        }
    }

}
