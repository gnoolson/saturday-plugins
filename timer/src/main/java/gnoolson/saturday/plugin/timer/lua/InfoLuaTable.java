package gnoolson.saturday.plugin.timer.lua;

import gnoolson.saturday.plugin.timer.Info;
import org.luaj.vm2.LuaTable;

public class InfoLuaTable extends LuaTable {

    public InfoLuaTable(Info info) {
        set("id", info.getId().toString());
        set("name", info.getName());
        set("delay", info.getDelay());
    }

}
