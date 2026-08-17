package gnoolson.saturday.plugin.json.lua;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

class ToStringFunctionTest {

    @Test
    void general() {
        JSONLuaModule.ToStringFunction toStringFunction = new JSONLuaModule.ToStringFunction();

        LuaTable table = new LuaTable();
        table.set("key1", "value1");
        table.set("key2", LuaValue.valueOf(false));
        table.set("key3", LuaValue.valueOf(111));


        LuaString result = (LuaString) toStringFunction.call(table, LuaBoolean.valueOf(true));

        String expected = "{\n" +
                "  \"key1\" : \"value1\",\n" +
                "  \"key3\" : 111,\n" +
                "  \"key2\" : false\n" +
                "}";

        Assertions.assertEquals(expected, result.checkjstring());
    }

}