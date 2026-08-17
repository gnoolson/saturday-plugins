package gnoolson.saturday.plugin.locker.lua;

import gnoolson.luaj_utils.LuaArgUtils;
import gnoolson.saturday.plugin.locker.LockCaretaker;
import gnoolson.saturday.plugin.locker.LockHandle;
import gnoolson.saturday.plugin.locker.Locker;
import gnoolson.saturday_plugin_api.Functionality;
import gnoolson.saturday_plugin_api.LuaModule;
import lombok.RequiredArgsConstructor;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class LockerModule extends LuaModule {

    private final LockCaretaker lockCaretaker = new LockCaretaker();
    private Locker locker;

    /*
     *
     *
     * */
    public LockerModule() {
        set("lockIds", new LockIdsFunction(this));
        set("lockGlobal", new LockGlobalFunction(this));
    }

    @Override
    public void updateFunctionality(Functionality functionality) {
        this.locker = (Locker) functionality;
    }

    @Override
    public void release() {
        lockCaretaker.closeOpenedLocks();
    }

    @Override
    public String name() {
        return "locker";
    }

    /*
     *
     *
     * */
    @RequiredArgsConstructor
    public static class LockIdsFunction extends VarArgFunction {

        private final LockerModule lockerModule;

        @Override
        public Varargs invoke(Varargs args) {
            String[] ids = new String[args.narg()];
            for (int i = 0, j = 1; i < args.narg(); i++, j++) {
                LuaValue keyLuaValue = LuaArgUtils.getLuaValueFromFunctionVarargs(args, j, "arg_" + j);
                String key = LuaArgUtils.getStringFromFunctionArgs(keyLuaValue, j, "arg_" + j);
                ids[i] = key;
            }

            LockHandle lockHandle = lockerModule.locker.lockIds(ids);
            lockerModule.lockCaretaker.add(lockHandle);
            return new LockHandleLuaTable(lockHandle);
        }
    }

    @RequiredArgsConstructor
    public static class LockGlobalFunction extends ZeroArgFunction {

        private final LockerModule lockerModule;

        @Override
        public LuaValue call() {
            LockHandle lockHandle = lockerModule.locker.lockGlobal();
            lockerModule.lockCaretaker.add(lockHandle);
            return new LockHandleLuaTable(lockHandle);
        }
    }

}
