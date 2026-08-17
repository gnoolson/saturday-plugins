package gnoolson.saturday.plugin.serial.lua;

import gnoolson.luaj_utils.LuaArgUtils;
import gnoolson.saturday.plugin.serial.OpenPort;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

public class OpenPortLuaTable extends LuaTable {

    private final OpenPort openPort;

    /*
     *
     *
     * */
    public OpenPortLuaTable(OpenPort openPort) {
        this.openPort = openPort;

        set("systemPortName", openPort.getSystemPortName());
        set("portDescription", openPort.getPortDescription());

        set("write", new WriteFunction());
        set("read", new ReadFunction());
    }

    /*
     *
     *
     * */
    public static class WriteFunction extends TwoArgFunction {

        @Override
        public LuaValue call(LuaValue selfLuaValue, LuaValue dataLuaValue) {
            OpenPortLuaTable openPortLuaTable = LuaArgUtils.getSelfFromFunctionArgs(selfLuaValue);
            byte[] data = LuaArgUtils.getByteArrayFromFunctionArgs(dataLuaValue, 2, "data");

            int written = openPortLuaTable.openPort.write(data);

            return LuaValue.valueOf(written);
        }
    }

    public static class ReadFunction extends ThreeArgFunction {

        @Override
        public LuaValue call(LuaValue selfLuaValue, LuaValue lengthLuaValue, LuaValue waitingTimeLuaValue) {
            OpenPortLuaTable openPortLuaTable = LuaArgUtils.getSelfFromFunctionArgs(selfLuaValue);
            int length = LuaArgUtils.getIntFromFunctionArgs(lengthLuaValue, 2, "length");
            int waitingTime = LuaArgUtils.getIntFromFunctionArgs(waitingTimeLuaValue, 3, "waitingTime");

            byte[] result = openPortLuaTable.openPort.read(length, waitingTime);

            return new LuaUserdata(result);
        }
    }

}
