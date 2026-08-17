package gnoolson.saturday.plugin.timer;

import gnoolson.saturday_plugin_api.SaturdayPlugin;
import lombok.Getter;

@Getter
public class TimerPlugin extends SaturdayPlugin {

    private Timer timer;

    /*
     *
     *
     * */
    @Override
    public void start() {
        this.timer = new TimerImpl(scriptLauncherGateway);
    }

    @Override
    public void stop() {
        this.timer.stop();
        this.timer = null;
    }

}
