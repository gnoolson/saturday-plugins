package gnoolson.saturday.plugin.cache;

import gnoolson.saturday_plugin_api.Functionality;

import java.util.Collection;
import java.util.Optional;

public interface Cache extends Functionality {

    Optional<Object> get(String key);

    Optional<Object> getAndProlong(String key);

    void put(String key, Object data, long storageTime);

    boolean remove(String key);

    void clear();

    void shutdown();

    Collection<String> getKeys();

}
