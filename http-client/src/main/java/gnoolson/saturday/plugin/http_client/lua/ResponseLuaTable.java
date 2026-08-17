package gnoolson.saturday.plugin.http_client.lua;

import gnoolson.luaj_utils.LuaTypeConverter;
import gnoolson.saturday.plugin.http_client.Response;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;

public class ResponseLuaTable extends LuaTable {

    public ResponseLuaTable(Response response) {
        set("code", LuaNumber.valueOf(response.getCode()));
        set("body", LuaString.valueOf(response.getBody().getValue()));
        set("headers", LuaTypeConverter.toLua(response.getHeaders()));
    }

}
