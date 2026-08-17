package gnoolson.saturday.plugin.locker.lua;

import gnoolson.luaj_utils.LuaArgUtils;
import gnoolson.saturday.plugin.locker.LockHandle;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public class LockHandleLuaTable extends LuaTable {

    private final LockHandle LockHandle;

    /*
     *
     *
     * */
    public LockHandleLuaTable(LockHandle LockHandle) {
        this.LockHandle = LockHandle;
        set("close", new CloseFunction());
    }

    /*
     *
     *
     * */
    public static class CloseFunction extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue self) {
            LockHandleLuaTable lockHandleLuaTable = LuaArgUtils.getSelfFromFunctionArgs(self);
            lockHandleLuaTable.LockHandle.close();
            return LuaValue.NIL;
        }
    }

}
