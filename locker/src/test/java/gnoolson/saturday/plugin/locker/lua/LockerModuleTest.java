package gnoolson.saturday.plugin.locker.lua;

import gnoolson.saturday.plugin.locker.LockerImpl;
import org.junit.jupiter.api.Test;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

// NO ASSERTS. ONLY CALL CHAIN
class LockerModuleTest {

    @Test
    public void lock_ids__call_chain(){
        LockerModule lockerModule = new LockerModule();
        lockerModule.updateFunctionality(new LockerImpl());

        LockerModule.LockIdsFunction lockIdsFunction = new LockerModule.LockIdsFunction(lockerModule);

        Varargs ids = LuaValue.varargsOf(LuaValue.valueOf("id_1"), LuaValue.valueOf("id_2"));

        LockHandleLuaTable lockHandleLuaTable = (LockHandleLuaTable)lockIdsFunction.invoke(ids);

        LockHandleLuaTable.CloseFunction closeFunction = new LockHandleLuaTable.CloseFunction();
        closeFunction.call(lockHandleLuaTable);
    }

    @Test
    public void lock_global__call_chain(){
        LockerModule lockerModule = new LockerModule();
        lockerModule.updateFunctionality(new LockerImpl());

        LockerModule.LockGlobalFunction lockGlobalFunction = new LockerModule.LockGlobalFunction(lockerModule);

        LockHandleLuaTable lockHandleLuaTable = (LockHandleLuaTable)lockGlobalFunction.invoke();

        LockHandleLuaTable.CloseFunction closeFunction = new LockHandleLuaTable.CloseFunction();
        closeFunction.call(lockHandleLuaTable);
    }

}