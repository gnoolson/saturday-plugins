package gnoolson.saturday.plugin.http_client.lua;

import gnoolson.luaj_utils.LuaArgUtils;
import gnoolson.luaj_utils.LuaTableUtils;
import gnoolson.luaj_utils.LuaTypeConverter;
import gnoolson.saturday.plugin.http_client.*;
import gnoolson.saturday_plugin_api.Functionality;
import gnoolson.saturday_plugin_api.LuaModule;
import lombok.RequiredArgsConstructor;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import java.util.HashMap;
import java.util.Map;

public class HttpClientLuaModule extends LuaModule {

    private HttpClient httpClient;

    /*
     *
     *
     * */
    public HttpClientLuaModule() {
        set("send", new SendRequestFunction(this));
    }

    @Override
    public void updateFunctionality(Functionality functionality) {
        this.httpClient = (HttpClient) functionality;
    }

    @Override
    public void release() {

    }

    @Override
    public String name() {
        return "http_client";
    }

    /*
     *
     *
     * */
    @RequiredArgsConstructor
    public static class SendRequestFunction extends OneArgFunction {

        private final HttpClientLuaModule httpClientLuaModule;

        @Override
        public LuaValue call(LuaValue requestLuaValue) {
            LuaTable requestLuaTable = LuaArgUtils.getLuaTableFromFunctionArgs(requestLuaValue, 1, "request");

            URL url = URL.of(LuaArgUtils.getStringFromLuaTable(requestLuaTable, "request", "url"));
            Method method = Method.valueOf(LuaArgUtils.getStringFromLuaTable(requestLuaTable, "request", "method"));
            int connectionTimeout = LuaArgUtils.getIntFromLuaTable(requestLuaTable, "request", "connectionTimeout");

            LuaTable headersLuaTable = LuaArgUtils.getLuaTableFromLuaTable(requestLuaTable, "request", "headers");

            Map<String, String> headersMap;
            if(LuaTableUtils.isEmpty(headersLuaTable)){
                headersMap = new HashMap<>(0);
            } else {
                Object obj = LuaTypeConverter.toObject(headersLuaTable);
                if(obj instanceof Map) {
                    headersMap = (Map)obj;
                } else {
                    throw new IllegalArgumentException("\"headers\" value must be a key-value map");
                }
            }

            Request request = new Request(
                    url,
                    method,
                    headersMap,
                    convertLuaValueToBody(requestLuaTable.get("body")),
                    connectionTimeout
            );

            Response response = httpClientLuaModule.httpClient.send(request);
            return new ResponseLuaTable(response);
        }

        /*
         *
         *
         * */
        private Body convertLuaValueToBody(LuaValue luaValue) {
            if (luaValue.isnil()) {
                return Body.empty();
            }

            String bodyStr = luaValue.checkjstring();
            return bodyStr.isEmpty() ? Body.empty() : Body.of(bodyStr);
        }
    }

}
