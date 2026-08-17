package gnoolson.saturday.plugin.locker;

import gnoolson.saturday_plugin_api.SaturdayPlugin;
import lombok.Getter;

public class LockerPlugin extends SaturdayPlugin {

    @Getter
    public Locker locker;

    /*
     *
     *
     * */
    @Override
    public void start() {
        this.locker = new LockerImpl();
    }

    @Override
    public void stop() {
        this.locker.close();
        this.locker = null;
    }

}