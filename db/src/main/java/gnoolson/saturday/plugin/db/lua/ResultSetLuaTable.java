package gnoolson.saturday.plugin.db.lua;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.List;
import java.util.Map;

public class ResultSetLuaTable extends LuaTable {

    public ResultSetLuaTable(List<Map<String, String>> resultSet) {
        int index = 1;

        for (Map<String, String> map : resultSet) {
            LuaTable row = new LuaTable();

            for (Map.Entry<String, String> stringObjectEntry : map.entrySet()) {
                String key = stringObjectEntry.getKey();
                String value = stringObjectEntry.getValue();
                row.set(key, value == null ? LuaValue.NIL : LuaValue.valueOf(value));
            }

            this.set(index, row);
            index++;
        }
    }

}
