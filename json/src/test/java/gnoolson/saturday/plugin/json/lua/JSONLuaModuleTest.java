package gnoolson.saturday.plugin.json.lua;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

class ToTableFunctionTest {

    @Test
    void general() {

        String json = "{\n" +
                "  \"key1\" : \"value1\",\n" +
                "  \"key2\" : 111,\n" +
                "  \"key3\" : false\n" +
                "}";

        JSONLuaModule.ToTableFunction toTableFunction = new JSONLuaModule.ToTableFunction();
        LuaTable result = (LuaTable) toTableFunction.call(LuaValue.valueOf(json));

        Assertions.assertEquals(result.get("key1").checkjstring(), "value1");
        Assertions.assertEquals(result.get("key2").checkint(), 111);
        Assertions.assertEquals(result.get("key3").checkboolean(), false);
    }

}