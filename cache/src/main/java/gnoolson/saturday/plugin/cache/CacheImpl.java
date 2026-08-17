package gnoolson.saturday.plugin.cache;

import gnoolson.saturday_plugin_api.Output;

import java.util.*;

public class CacheImpl implements Cache {

    private final Map<String, CachedData> cache = new HashMap<>();
    private final long cleaningInterval;
    private final Timer timer = new Timer();

    /*
     *
     *
     * */
    public CacheImpl(int cleaningInterval) {
        this.cleaningInterval = cleaningInterval;
        this.startCleaning();
    }

    @Override
    public void writeResultToOutput(Output output) {

    }

    @Override
    public synchronized Optional<Object> get(String key) {
        CachedData cachedData = cache.get(key);
        if (cachedData == null)
            return Optional.empty();

        if (cachedData.isExpired(System.currentTimeMillis()))
            return Optional.empty();

        return Optional.of(cachedData.get());
    }

    @Override
    public synchronized Optional<Object> getAndProlong(String key) {
        CachedData cachedData = cache.get(key);
        if (cachedData == null)
            return Optional.empty();

        if (cachedData.isExpired(System.currentTimeMillis()))
            return Optional.empty();

        return Optional.of(cachedData.getAndProlong(System.currentTimeMillis()));
    }

    @Override
    public synchronized void put(String key, Object data, long storageTime) {
        CachedData cachedData = new CachedData(data, storageTime, System.currentTimeMillis());
        cache.put(key, cachedData);
    }

    @Override
    public synchronized boolean remove(String key) {
        CachedData remove = cache.remove(key);
        return remove != null;
    }

    @Override
    public synchronized void clear() {
        cache.clear();
    }

    @Override
    public void shutdown() {
        timer.cancel();
        cache.clear();
    }

    @Override
    public synchronized Collection<String> getKeys() {
        return cache.keySet();
    }

    public synchronized void clearExpiredEntities() {
        long timestamp = System.currentTimeMillis();

        Iterator<Map.Entry<String, CachedData>> iterator = cache.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, CachedData> entry = iterator.next();

            if (entry.getValue().isExpired(timestamp)) {
                iterator.remove();
            }
        }
    }

    /*
     *
     *
     * */
    private void startCleaning() {
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                clearExpiredEntities();
            }
        }, 0, cleaningInterval);
    }

    private static class CachedData {
        private final Object data;
        private final long storageTime;
        private long expired;

        CachedData(Object data, long storageTime, long now) {
            this.data = data;
            this.storageTime = storageTime;
            this.expired = now + storageTime;
        }

        Object getAndProlong(long now) {
            this.expired = now + this.storageTime;
            return this.data;
        }

        Object get() {
            return this.data;
        }

        boolean isExpired(long now) {
            return now > expired;
        }
    }

}
