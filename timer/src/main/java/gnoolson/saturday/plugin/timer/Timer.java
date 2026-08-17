package gnoolson.saturday.plugin.timer;

import gnoolson.saturday_plugin_api.Functionality;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface Timer extends Functionality {

    UUID createTask(String name, long delay, String scriptId, Map<String, Object> args);

    UUID createTask(String name, long delay, Callback callback);

    boolean cancelTaskById(UUID id);

    void stop();

    Collection<Info> info();

    boolean cancelTaskByName(String name);

}
