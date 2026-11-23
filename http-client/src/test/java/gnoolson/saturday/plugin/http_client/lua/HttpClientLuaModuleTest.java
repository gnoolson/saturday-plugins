package gnoolson.saturday.plugin.http_client.lua;

import gnoolson.saturday.plugin.http_client.HttpClientImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;


class HttpClientLuaModuleTest {

//    @Test
    public void real_request() {
        HttpClientLuaModule httpClientLuaModule = new HttpClientLuaModule();
        httpClientLuaModule.updateFunctionality(new HttpClientImpl());
        HttpClientLuaModule.SendRequestFunction sendRequestFunction = new HttpClientLuaModule.SendRequestFunction(httpClientLuaModule);

        LuaTable request = new LuaTable();
        request.set("url", "https://api.open-meteo.com/v1/forecast?latitude=50.4501&longitude=30.5234&current=temperature_2m,relative_humidity_2m,wind_speed_10m");
        request.set("method", "GET");
        request.set("headers", new LuaTable() {{
            set("Accept", "application/json; charset=UTF-8");
        }});
        request.set("connectionTimeout", 10);

        LuaValue response = sendRequestFunction.call(request);

        Assertions.assertEquals(LuaNumber.valueOf(200), response.get("code"));
    }

    //@Test
    public void real_request_without_headers() {
        HttpClientLuaModule httpClientLuaModule = new HttpClientLuaModule();
        httpClientLuaModule.updateFunctionality(new HttpClientImpl());
        HttpClientLuaModule.SendRequestFunction sendRequestFunction = new HttpClientLuaModule.SendRequestFunction(httpClientLuaModule);

        LuaTable request = new LuaTable();
        request.set("url", "https://api.open-meteo.com/v1/forecast?latitude=50.4501&longitude=30.5234&current=temperature_2m,relative_humidity_2m,wind_speed_10m");
        request.set("method", "GET");
        request.set("headers", new LuaTable());
        request.set("connectionTimeout", 10);

        LuaValue response = sendRequestFunction.call(request);

        Assertions.assertEquals(LuaNumber.valueOf(200), response.get("code"));
    }

}