package gnoolson.saturday.plugin.locker;

import gnoolson.saturday_plugin_api.Functionality;

public interface Locker extends Functionality {

    LockHandle lockIds(String... ids);

    LockHandle lockGlobal();

    void close();

}
