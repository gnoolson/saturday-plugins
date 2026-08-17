package gnoolson.saturday.plugin.serial;

import gnoolson.saturday_plugin_api.SaturdayPlugin;
import lombok.Getter;

@Getter
public class SerialPlugin extends SaturdayPlugin {

    private Serial serial;

    /*
     *
     *
     * */
    @Override
    public void start() {
        this.serial = new SerialImpl(this.scriptLauncherGateway);
    }

    @Override
    public void stop() {
        this.serial.shutdown();
        this.serial = null;
    }

}
