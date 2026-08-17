package gnoolson.saturday.plugin.json.lua;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gnoolson.luaj_utils.LuaArgUtils;
import gnoolson.luaj_utils.LuaTypeConverter;
import gnoolson.saturday_plugin_api.Functionality;
import gnoolson.saturday_plugin_api.LuaModule;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

public class JSONLuaModule extends LuaModule {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    public JSONLuaModule() {
        set("toString", new ToStringFunction());
        set("toTable", new ToTableFunction());
    }

    @Override
    public void updateFunctionality(Functionality functionality) {
    }

    @Override
    public void release() {
    }

    @Override
    public String name() {
        return "json";
    }

    /*
     *
     *
     * */
    public static class ToStringFunction extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue dataLuaValue, LuaValue prettyLuaValue) {
            Object data = LuaArgUtils.getObjectFromFunctionArgs(dataLuaValue, 1, "data");

            boolean pretty;
            if (prettyLuaValue.isnil())
                pretty = false;
            else
                pretty = LuaArgUtils.getBooleanFromFunctionArgs(prettyLuaValue, 2, "pretty");

            try {
                if (pretty) {
                    return LuaString.valueOf(JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(data));
                } else {
                    return LuaString.valueOf(JSON_MAPPER.writeValueAsString(data));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static class ToTableFunction extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue jsonStringLuaValue) {
            String jsonString = LuaArgUtils.getStringFromFunctionArgs(jsonStringLuaValue, 1, "jsonString");

            try {
                JsonNode obj = JSON_MAPPER.readTree(jsonString);
                return LuaTypeConverter.toLua(obj);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
