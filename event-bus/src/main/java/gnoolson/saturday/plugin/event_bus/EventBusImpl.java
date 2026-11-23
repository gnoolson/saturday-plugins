package gnoolson.saturday.plugin.event_bus;

import gnoolson.saturday_plugin_api.Output;
import gnoolson.saturday_plugin_api.ScriptLauncherGateway;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
public class EventBusImpl implements EventBus {

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Map<String, Set<String>> subscriptions = new HashMap<>();
    private final ScriptLauncherGateway scriptLauncherGateway;

    /*
     *
     *
     * */
    @Override
    public synchronized boolean on(String scriptId, String eventName) {
        Set<String> scriptIds = subscriptions.computeIfAbsent(eventName, k -> new HashSet<>());
        return scriptIds.add(scriptId);
    }

    @Override
    public synchronized boolean off(String scriptId, String eventName) {
        Set<String> scriptIds = subscriptions.computeIfAbsent(eventName, k -> new HashSet<>());
        return scriptIds.remove(scriptId);
    }

    @Override
    public synchronized int emit(String scriptId, String eventName, Map<String, Object> args) {
        Set<String> scriptIds = subscriptions.get(eventName);
        if (scriptIds == null || scriptIds.isEmpty())
            return 0;

        for (String subscriptionScriptId : scriptIds) {
            if(scriptId.equals(subscriptionScriptId)) {
                throw new RuntimeException(String.format(
                        "Circular event emission detected: a listener cannot emit the same event it is subscribed to. ScriptId: %s, Event: %s",
                        scriptId,
                        eventName
                        ));
            }

            executor.execute(() -> {
                scriptLauncherGateway.execute(subscriptionScriptId, args);
            });
        }

        return scriptIds.size();
    }

    @Override
    public synchronized void shutdown() {
        executor.shutdown();
        subscriptions.clear();
    }

    @Override
    public List<EventInfo> getEventInfoList() {
        List<EventInfo> result = new ArrayList<>(subscriptions.size());

        synchronized (this) {
            for (Map.Entry<String, Set<String>> stringSetEntry : subscriptions.entrySet()) {
                result.add(new EventInfo(stringSetEntry.getKey(), new HashSet<>(stringSetEntry.getValue())));
            }
        }

        return result;
    }

    @Override
    public void writeResultToOutput(Output output) {

    }

}
