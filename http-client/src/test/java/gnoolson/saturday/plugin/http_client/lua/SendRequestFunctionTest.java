package gnoolson.saturday.plugin.http_client.lua;

import gnoolson.luaj_utils.LuaArgUtils;
import gnoolson.saturday.plugin.http_client.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class SendRequestFunctionTest {

    Response response = new Response(
            200,
            Body.of("<html></html"),
            new HashMap<String, List<String>>() {{
                put("header_1", new ArrayList<String>() {{
                    add("value_11");
                    add("value_12");
                }});
                put("header_2", new ArrayList<String>() {{
                    add("value_21");
                    add("value_22");
                }});
            }}
    );
    Request request = new Request(
            URL.of("https://google.com"),
            Method.valueOf("GET"),
            new HashMap<String, String>() {{
                put("content-type", "application/json");
                put("custom-header", "qwerty");
            }},
            Body.empty(),
            1
    );

    /*
     *
     *
     * */
    @Test
    public void send() {
        HttpClient httpClient = Mockito.mock(HttpClient.class);
        HttpClient httpClientSpy = Mockito.spy(httpClient);

        Mockito.when(httpClientSpy.send(Mockito.any())).thenReturn(response);

        HttpClientLuaModule httpClientLuaModule = new HttpClientLuaModule();
        httpClientLuaModule.updateFunctionality(httpClientSpy);
        HttpClientLuaModule.SendRequestFunction sendRequestFunction = new HttpClientLuaModule.SendRequestFunction(httpClientLuaModule);

        LuaTable requestLuatable = new LuaTable();
        {
            requestLuatable.set("url", "https://google.com");
            requestLuatable.set("method", "GET");

            LuaTable headersLuatable = new LuaTable();
            headersLuatable.set("content-type", "application/json");
            headersLuatable.set("custom-header", "qwerty");
            requestLuatable.set("headers", headersLuatable);

            requestLuatable.set("body", LuaValue.NIL);
            requestLuatable.set("connectionTimeout", 1);
        }

        LuaValue responseLuaValue = sendRequestFunction.call(requestLuatable);
        LuaTable responseLuaTable = LuaArgUtils.getLuaTableFromLuaValue(responseLuaValue, "response");

        Assertions.assertEquals(response.getCode(), LuaArgUtils.getIntFromLuaTable(responseLuaTable, "response", "code"));
        Assertions.assertEquals(response.getBody(), Body.of(LuaArgUtils.getStringFromLuaTable(responseLuaTable, "response", "body")));
        Assertions.assertEquals(response.getHeaders(), LuaArgUtils.getObjectFromLuaTable(responseLuaTable, "response", "headers"));

        Mockito.verify(httpClientSpy).send(request);
    }



}