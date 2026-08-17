package gnoolson.saturday.plugin.cache;

import gnoolson.saturday_plugin_api.SaturdayPlugin;
import lombok.Getter;

public class CachePlugin extends SaturdayPlugin {

    @Getter
    public Cache cache;

    /*
     *
     *
     * */
    @Override
    public void start() {
        this.cache = new CacheImpl(1_000);
    }

    @Override
    public void stop() {
        this.cache.clear();
        this.cache.shutdown();
        this.cache = null;
    }

}
