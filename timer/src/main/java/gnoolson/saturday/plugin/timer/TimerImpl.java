package gnoolson.saturday.plugin.timer;

import gnoolson.saturday_plugin_api.Output;
import gnoolson.saturday_plugin_api.ScriptLauncherGateway;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class TimerImpl implements Timer {

    private static final Logger log = LoggerFactory.getLogger(TimerImpl.class);

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Map<UUID, ScheduledFutureInfo> schedules = new HashMap<>();
    private final ScriptLauncherGateway scriptLauncherGateway;

    /*
     *
     *
     * */
    @Override
    public synchronized UUID createTask(String name, long delay, String scriptId, Map<String, Object> args) {
        cancelTaskByName(name);
        UUID id = UUID.randomUUID();

        ScheduledFuture<?> schedule = scheduler.schedule(new Task(scriptId, args, id), delay, TimeUnit.MILLISECONDS);
        schedules.put(id, new ScheduledFutureInfo(schedule, name, delay));
        return id;
    }

    @Override
    public synchronized boolean cancelTaskById(UUID id) {
        ScheduledFutureInfo scheduledFutureInfo = schedules.get(id);
        if (scheduledFutureInfo == null)
            return false;

        scheduledFutureInfo.cancel();
        remove(id);

        return true;
    }

    @Override
    public synchronized void stop() {
        schedules.clear();
        scheduler.shutdown();
    }

    @Override
    public synchronized Collection<Info> info() {
        Collection<Info> result = new ArrayList<>();

        for (Map.Entry<UUID, ScheduledFutureInfo> entry : schedules.entrySet()) {
            UUID id = entry.getKey();
            ScheduledFutureInfo scheduledFutureInfo = entry.getValue();
            result.add(new Info(id, scheduledFutureInfo.name, scheduledFutureInfo.delay));
        }

        return result;
    }

    @Override
    public synchronized boolean cancelTaskByName(String name) {
        for (Map.Entry<UUID, ScheduledFutureInfo> entry : schedules.entrySet()) {
            UUID id = entry.getKey();
            ScheduledFutureInfo scheduledFutureInfo = entry.getValue();

            if (scheduledFutureInfo.name.equals(name)) {
                return cancelTaskById(id);
            }
        }
        return false;
    }

    @Override
    public UUID createTask(String name, long delay, Callback callback) {
        cancelTaskByName(name);
        UUID id = UUID.randomUUID();

        ScheduledFuture<?> schedule = scheduler.schedule(new CallbackTask(id, callback), delay, TimeUnit.MILLISECONDS);
        schedules.put(id, new ScheduledFutureInfo(schedule, name, delay));
        return id;
    }

    @Override
    public void writeResultToOutput(Output output) {

    }

    /*
     *
     *
     * */
    private synchronized void remove(UUID id) {
        schedules.remove(id);
    }

    @RequiredArgsConstructor
    private static class ScheduledFutureInfo {
        final ScheduledFuture<?> scheduledFuture;
        final String name;
        final long delay;

        void cancel() {
            scheduledFuture.cancel(false);
        }
    }

    @RequiredArgsConstructor
    private class Task implements Runnable {
        private final String scriptId;
        private final Map<String, Object> args;
        private final UUID id;

        @Override
        public void run() {
            try {
                remove(id);
                scriptLauncherGateway.execute(scriptId, args);
            } catch (Exception e) {
                log.warn("Script Launch Exception", e);
            }
        }
    }

    @RequiredArgsConstructor
    private class CallbackTask implements Runnable {
        private final UUID id;
        private final Callback callback;

        @Override
        public void run() {
            try {
                remove(id);
                callback.execute();
            } catch (Exception e) {
                log.warn("Execute Callback Exception", e);
            }
        }
    }

}
