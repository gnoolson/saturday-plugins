package gnoolson.saturday.plugin.db.lua;

import gnoolson.luaj_utils.LuaArgUtils;
import gnoolson.saturday.plugin.db.Connection;
import gnoolson.saturday.plugin.db.SQL;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.List;
import java.util.Map;

public class ConnectionLuaTable extends LuaTable {

    private final Connection connection;

    /*
     *
     *
     * */
    public ConnectionLuaTable(Connection connection) {
        this.connection = connection;
        this.set("commit", new CommitFunction());
        this.set("rollback", new RollbackFunction());
        this.set("close", new CloseFunction());
        this.set("query", new QueryFunction());
        this.set("update", new UpdateFunction());
    }

    /*
     *
     *
     * */
    public static class CommitFunction extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue self) {
            ConnectionLuaTable connectionLuaTable = LuaArgUtils.getSelfFromFunctionArgs(self);
            connectionLuaTable.connection.commit();
            return LuaValue.NIL;
        }
    }

    public static class RollbackFunction extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue self) {
            ConnectionLuaTable connectionLuaTable = LuaArgUtils.getSelfFromFunctionArgs(self);
            connectionLuaTable.connection.rollback();
            return LuaValue.NIL;
        }
    }

    public static class CloseFunction extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue self) {
            ConnectionLuaTable connectionLuaTable = LuaArgUtils.getSelfFromFunctionArgs(self);
            connectionLuaTable.connection.close();
            return LuaValue.NIL;
        }
    }

    public static class QueryFunction extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue self, LuaValue queryStringLuaValue) {
            ConnectionLuaTable connectionLuaTable = LuaArgUtils.getSelfFromFunctionArgs(self);
            String queryString = LuaArgUtils.getStringFromFunctionArgs(queryStringLuaValue, 2, "queryString");
            List<Map<String, String>> resultSet = connectionLuaTable.connection.query(SQL.of(queryString));
            return new ResultSetLuaTable(resultSet);
        }
    }

    public static class UpdateFunction extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue self, LuaValue updateStringLuaValue) {
            ConnectionLuaTable connectionLuaTable = LuaArgUtils.getSelfFromFunctionArgs(self);
            String updateString = LuaArgUtils.getStringFromFunctionArgs(updateStringLuaValue, 2, "updateString");
            int number = connectionLuaTable.connection.update(SQL.of(updateString));
            return LuaInteger.valueOf(number);
        }
    }

}
