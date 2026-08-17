package gnoolson.saturday.plugin.locker;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor
public class LockHandle {

    private final gnoolson.locker.Locker.LockHandle lockHandle;

    /*
     *
     *
     * */
    public void close() {
        lockHandle.close();
    }

}
