package gnoolson.saturday.plugin.event_bus;

import gnoolson.saturday_plugin_api.SaturdayPlugin;
import lombok.Getter;

@Getter
public class EventBusPlugin extends SaturdayPlugin {

    private EventBus eventBus;

    /*
     *
     *
     * */
    @Override
    public void start() {
        this.eventBus = new EventBusImpl(scriptLauncherGateway);
    }

    @Override
    public void stop() {
        this.eventBus.shutdown();
        this.eventBus = null;
    }

}
