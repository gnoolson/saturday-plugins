package gnoolson.saturday.plugin.db;

import gnoolson.luaj_utils.LuaTypeConverter;
import gnoolson.saturday.plugin.db.lua.ConnectionLuaTable;
import gnoolson.saturday.plugin.db.lua.DBLuaModule;
import gnoolson.saturday.plugin.db.lua.DataSourceLuaTable;
import gnoolson.saturday.plugin.db.lua.ResultSetLuaTable;
import gnoolson.saturday_plugin_api.FunctionalityFactory;
import gnoolson.saturday_plugin_api.FunctionalityFactoryProvider;
import gnoolson.saturday_plugin_api.LuaModuleFactory;
import gnoolson.saturday_plugin_api.LuaModuleFactoryProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.luaj.vm2.LuaString;

import java.util.List;

public class GeneralTest {

    @Test
    public void data_source_connection_params_from_path___close_resources() {
        DBLuaModule dbLuaModule = getDBLuaModule();
        DataSourceLuaTable dataSourceLuaTable = getDataSourceLuaTableFromPath(dbLuaModule);
        ConnectionLuaTable connectionLuaTable = getConnectionLuaTable(dataSourceLuaTable);

        List resultSet = (List) executeQuery(connectionLuaTable);
        Assertions.assertFalse(resultSet.isEmpty());

        Assertions.assertEquals(1, dataSourceLuaTable.getDataSource().getActiveConnections());
        closeConnection(connectionLuaTable);
        Assertions.assertEquals(0, dataSourceLuaTable.getDataSource().getActiveConnections());

        destroyDataSource(dbLuaModule, dataSourceLuaTable);
    }

    /*
     *
     *
     * */
    private DBLuaModule getDBLuaModule() {
        DBPlugin dbPlugin = new DBPlugin();
        dbPlugin.start();

        LuaModuleFactoryProvider luaModuleFactoryProvider = new LuaModuleFactoryProviderImpl();
        LuaModuleFactory luaModuleFactory = luaModuleFactoryProvider.getFactory();
        DBLuaModule dbLuaModule = (DBLuaModule) luaModuleFactory.getInstance();

        FunctionalityFactoryProvider functionalityFactoryProvider = new FunctionalityFactoryProviderImpl();
        functionalityFactoryProvider.setup(dbPlugin);
        FunctionalityFactory functionalityFactory = functionalityFactoryProvider.getFactory();

        dbLuaModule.updateFunctionality(functionalityFactory.getInstance());
        return dbLuaModule;
    }

    private DataSourceLuaTable getDataSourceLuaTableFromPath(DBLuaModule dbLuaModule) {
        DBLuaModule.GetOrCreateDataSourceFunction getOrCreateDataSourceFunction = new DBLuaModule.GetOrCreateDataSourceFunction(dbLuaModule);

        LuaString path = LuaString.valueOf("classpath:test_configuration.properties");
        return (DataSourceLuaTable) getOrCreateDataSourceFunction.call(LuaString.valueOf("mariadb"), path);
    }

    private ConnectionLuaTable getConnectionLuaTable(DataSourceLuaTable dataSourceLuaTable) {
        DataSourceLuaTable.GetConnectionFunction getConnectionFunction = new DataSourceLuaTable.GetConnectionFunction();
        return (ConnectionLuaTable) getConnectionFunction.call(dataSourceLuaTable);
    }

    private Object executeQuery(ConnectionLuaTable connectionLuaTable) {
        ConnectionLuaTable.QueryFunction queryFunction = new ConnectionLuaTable.QueryFunction();
        ResultSetLuaTable resultSetLuaTable = (ResultSetLuaTable) queryFunction.call(connectionLuaTable, LuaString.valueOf("SHOW DATABASES"));

        return LuaTypeConverter.toObject(resultSetLuaTable);
    }

    private void closeConnection(ConnectionLuaTable connectionLuaTable) {
        ConnectionLuaTable.CloseFunction closeFunction = new ConnectionLuaTable.CloseFunction();
        closeFunction.call(connectionLuaTable);
    }

    private void destroyDataSource(DBLuaModule dbLuaModule, DataSourceLuaTable dataSourceLuaTable) {
        DBLuaModule.CloseDataSourceFunction closeDataSourceFunction = new DBLuaModule.CloseDataSourceFunction(dbLuaModule);
        closeDataSourceFunction.call(dataSourceLuaTable.get("id"));
    }

}
