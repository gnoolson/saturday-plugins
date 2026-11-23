package gnoolson.saturday.plugin.event_bus;

import gnoolson.saturday_plugin_api.Functionality;

import java.util.List;
import java.util.Map;

public interface EventBus extends Functionality {

    boolean on(String scriptId, String eventName);

    boolean off(String scriptId, String eventName);

    int emit(String scriptId, String eventName, Map<String, Object> args);

    void shutdown();

    List<EventInfo> getEventInfoList();

}
