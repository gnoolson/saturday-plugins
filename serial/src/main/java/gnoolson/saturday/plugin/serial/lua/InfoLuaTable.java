package gnoolson.saturday.plugin.serial.lua;

import gnoolson.saturday.plugin.serial.Info;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;

public class InfoLuaTable extends LuaTable {

    public InfoLuaTable(Info info) {
        set("deviceId", info.getDeviceId());
        set("port", info.getPort());
        set("systemPortName", info.getSystemPortName());
        set("portDescription", info.getPortDescription());
        set("baudRate", info.getBaudRate());
        set("numDataBits", info.getNumDataBits());
        set("numStopBits", info.getNumStopBits());
        set("parity", info.getParity());
        set("searchDeviceEventHandler", info.getSearchDeviceEventHandler());
        set("dataReceivedEventHandler", info.getDataReceivedEventHandler());
        set("deviceConnectedEventHandler", info.getDeviceConnectedEventHandler());
        set("deviceDisconnectedEventHandler", info.getDeviceDisconnectedEventHandler());
        set("connectionFailedEventHandler", info.getConnectionFailedEventHandler());
        set("readTime", info.getReadTime());
        set("terminator", new LuaUserdata(info.getTerminator()));
        set("inputBufferLength", info.getInputBufferLength());
        set("connected", LuaBoolean.valueOf(info.isConnected()));
        set("connectionTimestamp", LuaValue.valueOf(info.getConnectionTimestamp()));
    }

}
