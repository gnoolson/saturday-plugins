package gnoolson.saturday.plugin.db.lua;

import gnoolson.luaj_utils.LuaArgUtils;
import gnoolson.saturday.plugin.db.Connection;
import gnoolson.saturday.plugin.db.ConnectionCaretaker;
import gnoolson.saturday.plugin.db.DataSource;
import lombok.Getter;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public class DataSourceLuaTable extends LuaTable {

    @Getter
    private final DataSource dataSource;
    private final ConnectionCaretaker connectionCaretaker;

    /*
     *
     *
     * */
    public DataSourceLuaTable(DataSource dataSource, ConnectionCaretaker connectionCaretaker) {
        this.dataSource = dataSource;
        this.connectionCaretaker = connectionCaretaker;

        this.set("getConnection", new GetConnectionFunction());
        this.set("id", dataSource.getId());
    }

    /*
     *
     *
     * */
    public static class GetConnectionFunction extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue self, LuaValue tansactionLevelLuaValue) {
            DataSourceLuaTable dataSourceLuaTable = LuaArgUtils.getSelfFromFunctionArgs(self);

            Connection connection;
            if (tansactionLevelLuaValue.equals(LuaValue.NIL)) {
                connection = dataSourceLuaTable.dataSource.getConnection(DataSource.TransactionLevel.REPEATABLE_READ);
            } else {
                String transactionLevelStr = LuaArgUtils.getStringFromFunctionArgs(tansactionLevelLuaValue, 2, "transactionLevel");
                connection = dataSourceLuaTable.dataSource.getConnection(DataSource.TransactionLevel.valueOf(transactionLevelStr));
            }

            dataSourceLuaTable.connectionCaretaker.addConnection(connection);
            return new ConnectionLuaTable(connection);
        }
    }

}
