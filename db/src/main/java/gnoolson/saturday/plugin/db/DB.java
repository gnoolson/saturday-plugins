package gnoolson.saturday.plugin.db;

import gnoolson.saturday_plugin_api.Functionality;

import java.util.List;

public interface DB extends Functionality {

    DataSource getOrCreate(String id, String path);

    void destroy(String id);

    void shutdown();

    List<DataSourceInfo> getInfo();

}
