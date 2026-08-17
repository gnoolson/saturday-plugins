package gnoolson.saturday.plugin.now.lua;

import gnoolson.saturday_plugin_api.Functionality;
import gnoolson.saturday_plugin_api.LuaModule;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.Calendar;

public class NowLuaModule extends LuaModule {

    public NowLuaModule() {
        set("getDate", new GetDateFunction());
        set("getTime", new GetTimeFunction());
    }

    @Override
    public void updateFunctionality(Functionality functionality) {
    }

    @Override
    public void release() {
    }

    @Override
    public String name() {
        return "now";
    }

    /*
     *
     *
     * */
    public static class GetDateFunction extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            Calendar d = Calendar.getInstance();
            LuaTable tbl = LuaValue.tableOf();
            tbl.set("year", LuaValue.valueOf(d.get(Calendar.YEAR)));
            tbl.set("month", LuaValue.valueOf(d.get(Calendar.MONTH) + 1));
            tbl.set("day", LuaValue.valueOf(d.get(Calendar.DAY_OF_MONTH)));
            tbl.set("hour", LuaValue.valueOf(d.get(Calendar.HOUR_OF_DAY)));
            tbl.set("min", LuaValue.valueOf(d.get(Calendar.MINUTE)));
            tbl.set("sec", LuaValue.valueOf(d.get(Calendar.SECOND)));
            tbl.set("wday", LuaValue.valueOf(d.get(Calendar.DAY_OF_WEEK)));
            tbl.set("yday", LuaValue.valueOf(d.get(0x6)));
            return tbl;
        }
    }

    public static class GetTimeFunction extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            return LuaNumber.valueOf(System.currentTimeMillis());
        }
    }

}
