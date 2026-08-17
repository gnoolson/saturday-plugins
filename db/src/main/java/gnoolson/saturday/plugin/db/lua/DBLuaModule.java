package gnoolson.saturday.plugin.db.lua;

import gnoolson.luaj_utils.LuaArgUtils;
import gnoolson.saturday.plugin.db.ConnectionCaretaker;
import gnoolson.saturday.plugin.db.DB;
import gnoolson.saturday.plugin.db.DataSource;
import gnoolson.saturday.plugin.db.DataSourceInfo;
import gnoolson.saturday_plugin_api.Functionality;
import gnoolson.saturday_plugin_api.LuaModule;
import lombok.RequiredArgsConstructor;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.List;

public class DBLuaModule extends LuaModule {

    private final ConnectionCaretaker connectionCaretaker = new ConnectionCaretaker();
    private DB db;

    /*
     *
     *
     * */
    public DBLuaModule() {
        set("getOrCreateDataSource", new GetOrCreateDataSourceFunction(this));
        set("closeDataSource", new CloseDataSourceFunction(this));
        set("info", new InfoFunction(this));
    }

    @Override
    public void updateFunctionality(Functionality functionality) {
        this.db = (DB) functionality;
    }

    @Override
    public void release() {
        connectionCaretaker.closeOpenedConnection();
    }

    @Override
    public String name() {
        return "db";
    }

    /*
     *
     *
     * */
    @RequiredArgsConstructor
    public static class InfoFunction extends ZeroArgFunction {

        private final DBLuaModule dbLuaModule;

        @Override
        public LuaValue call() {
            List<DataSourceInfo> dataSourceInfoList = dbLuaModule.db.getInfo();
            LuaTable result = new LuaTable();

            int index = 1;
            for (DataSourceInfo dataSourceInfo : dataSourceInfoList) {
                result.set(index, new DataSourceInfoLuaTable(dataSourceInfo));
                index++;
            }

            return result;
        }
    }

    @RequiredArgsConstructor
    public static class GetOrCreateDataSourceFunction extends TwoArgFunction {

        private final DBLuaModule dbLuaModule;

        @Override
        public LuaValue call(LuaValue dataSourceIdLuaString, LuaValue pathLuaValue) {
            String dataSourceId = LuaArgUtils.getStringFromFunctionArgs(dataSourceIdLuaString, 1, "dataSourceId");
            String path = LuaArgUtils.getStringFromFunctionArgs(pathLuaValue, 2, "path");

            dataSourceIdLuaString.checkjstring();
            DataSource dataSource = dbLuaModule.db.getOrCreate(dataSourceId, path);

            return new DataSourceLuaTable(dataSource, dbLuaModule.connectionCaretaker);
        }
    }

    @RequiredArgsConstructor
    public static class CloseDataSourceFunction extends OneArgFunction {

        private final DBLuaModule dbLuaModule;

        @Override
        public LuaValue call(LuaValue dataSourceIdLuaString) {
            String dataSourceId = LuaArgUtils.getStringFromFunctionArgs(dataSourceIdLuaString, 1, "dataSourceId");

            dbLuaModule.db.destroy(dataSourceId);
            return LuaValue.NIL;
        }
    }

}
