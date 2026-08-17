package gnoolson.saturday.plugin.db;

import gnoolson.saturday_plugin_api.SaturdayPlugin;
import lombok.Getter;

public class DBPlugin extends SaturdayPlugin {

    @Getter
    private DB db;

    /*
     *
     *
     * */
    @Override
    public void start() {
        db = new DBImpl();
    }

    @Override
    public void stop() {
        db.shutdown();
        db = null;
    }

}
