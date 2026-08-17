package gnoolson.saturday.plugin.locker;

import gnoolson.locker.OptimisticLocalLocker;
import gnoolson.saturday_plugin_api.Output;

public class LockerImpl implements Locker {

    private final gnoolson.locker.Locker locker = new OptimisticLocalLocker();

    /*
     *
     *
     * */
    @Override
    public void writeResultToOutput(Output output) {
    }

    @Override
    public LockHandle lockIds(String... ids) {
        gnoolson.locker.Locker.LockHandle lockHandle = locker.lockIds(ids);
        return new LockHandle(lockHandle);
    }

    @Override
    public LockHandle lockGlobal() {
        gnoolson.locker.Locker.LockHandle lockHandle = locker.lockGlobal();
        return new LockHandle(lockHandle);
    }

    @Override
    public void close() {

    }

}
