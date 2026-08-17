package gnoolson.saturday.plugin.db.lua;

import gnoolson.saturday.plugin.db.DataSourceInfo;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;

public class DataSourceInfoLuaTable extends LuaTable {

    public DataSourceInfoLuaTable(DataSourceInfo dataSourceInfo) {
        set("id", LuaInteger.valueOf(dataSourceInfo.getId()));
        set("driverPath", LuaString.valueOf(dataSourceInfo.getDriverPath()));
        set("driverClassName", LuaString.valueOf(dataSourceInfo.getDriverClassName()));
        set("jdbcUrl", LuaString.valueOf(dataSourceInfo.getJdbcUrl()));
        set("username", LuaString.valueOf(dataSourceInfo.getUsername()));
        set("activeConnections", LuaInteger.valueOf(dataSourceInfo.getActiveConnections()));
        set("idleConnections", LuaInteger.valueOf(dataSourceInfo.getIdleConnections()));
    }

}
