package gnoolson.saturday.plugin.locker;

import java.util.ArrayList;
import java.util.List;

public class LockCaretaker {

    private final List<LockHandle> locks = new ArrayList<>(1);

    /*
     *
     *
     * */
    public void closeOpenedLocks() {
        for (LockHandle lock : locks) {
            lock.close();
        }
    }

    public void add(LockHandle lockHandle) {
        locks.add(lockHandle);
    }

}
