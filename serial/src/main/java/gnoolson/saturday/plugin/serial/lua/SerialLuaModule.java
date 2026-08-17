package gnoolson.saturday.plugin.serial.lua;

import gnoolson.luaj_utils.LuaArgUtils;
import gnoolson.saturday.plugin.serial.*;
import gnoolson.saturday_plugin_api.Functionality;
import gnoolson.saturday_plugin_api.LuaModule;
import lombok.RequiredArgsConstructor;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.List;

public class SerialLuaModule extends LuaModule {

    private Serial serial;

    /*
     *
     *
     * */
    public SerialLuaModule() {
        set("SEARCH", Consts.SEARCH);
        set("DEVICE_CONNECTED", Consts.DEVICE_CONNECTED);
        set("DEVICE_DISCONNECTED", Consts.DEVICE_DISCONNECTED);
        set("CONNECTION_FAILED", Consts.CONNECTION_FAILED);
        set("DATA_RECEIVED", Consts.DATA_RECEIVED);
        set("SEARCH_DEVICE", Consts.SEARCH_DEVICE);

        set("resetInputBuffer", new ResetInputBufferFunction(this));
        set("popInputData", new PopInputDataFunction(this));
        set("writeData", new WriteDataFunction(this));
        set("registerDeviceIfAbsent", new RegisterDeviceIfAbsentFunction(this));
        set("unregisterDevice", new UnregisterDeviceFunction(this));
        set("searchDevice", new SearchDeviceFunction(this));
        set("isDeviceRegistered", new IsDeviceRegisteredFunction(this));
        set("isDeviceAvailable", new IsDeviceAvailableFunction(this));
        set("info", new InfoFunction(this));
    }

    @Override
    public void updateFunctionality(Functionality functionality) {
        this.serial = (Serial) functionality;
    }

    @Override
    public void release() {

    }

    @Override
    public String name() {
        return "serial";
    }

    /*
     *
     *
     * */
    @RequiredArgsConstructor
    public static class RegisterDeviceIfAbsentFunction extends TwoArgFunction {
        public static final String PARAMS = "params";

        public static final String BAUD_RATE = "baudRate";
        public static final String NUM_DATA_BITS = "numDataBits";
        public static final String NUM_STOP_BITS = "numStopBits";
        public static final String PARITY = "parity";

        public static final String READ_TIME = "readTime";
        public static final String INPUT_TERMINATOR = "inputTerminator";
        public static final String INPUT_BUFFER_LENGTH = "inputBufferLength";

        public static final String PORT = "port";

        public static final String DATA_RECEIVED_EVENT_HANDLER = "dataReceivedEventHandler";
        public static final String DEVICE_CONNECTED_EVENT_HANDLER = "deviceConnectedEventHandler";
        public static final String DEVICE_DISCONNECTED_EVENT_HANDLER = "deviceDisconnectedEventHandler";
        public static final String SEARCH_DEVICE_EVENT_HANDLER = "searchDeviceEventHandler";
        public static final String CONNECTION_FAILED_EVENT_HANDLER = "connectionFailedEventHandler";

        private final SerialLuaModule serialLuaModule;

        /*
         *
         *
         * */
        @Override
        public LuaValue call(LuaValue deviceIdLuaValue, LuaValue paramsLuaValue) {
            String deviceId = LuaArgUtils.getStringFromFunctionArgs(deviceIdLuaValue, 1, "deviceId");

            if (serialLuaModule.serial.isDeviceRegistered(deviceId))
                return LuaValue.valueOf(false);

            LuaTable paramsLuaTable = LuaArgUtils.getLuaTableFromFunctionArgs(paramsLuaValue, 2, PARAMS);

            Params params = new Params();

            String port = LuaArgUtils.getStringFromLuaTable(paramsLuaTable, PARAMS, PORT);
            params.setPort(port);

            if (!paramsLuaTable.get(BAUD_RATE).isnil())
                params.setBaudRate(LuaArgUtils.getIntFromLuaTable(paramsLuaTable, PARAMS, BAUD_RATE));

            if (!paramsLuaTable.get(NUM_DATA_BITS).isnil())
                params.setNumDataBits(LuaArgUtils.getIntFromLuaTable(paramsLuaTable, PARAMS, NUM_DATA_BITS));

            if (!paramsLuaTable.get(NUM_STOP_BITS).isnil())
                params.setNumStopBits(LuaArgUtils.getIntFromLuaTable(paramsLuaTable, PARAMS, NUM_STOP_BITS));

            if (!paramsLuaTable.get(PARITY).isnil())
                params.setParity(LuaArgUtils.getIntFromLuaTable(paramsLuaTable, PARAMS, PARITY));

            if (!paramsLuaTable.get(SEARCH_DEVICE_EVENT_HANDLER).isnil())
                params.setSearchDeviceEventHandler(LuaArgUtils.getStringFromLuaTable(paramsLuaTable, PARAMS, SEARCH_DEVICE_EVENT_HANDLER));

            if (!paramsLuaTable.get(DEVICE_CONNECTED_EVENT_HANDLER).isnil())
                params.setDeviceConnectedEventHandler(LuaArgUtils.getStringFromLuaTable(paramsLuaTable, PARAMS, DEVICE_CONNECTED_EVENT_HANDLER));

            if (!paramsLuaTable.get(DEVICE_DISCONNECTED_EVENT_HANDLER).isnil())
                params.setDeviceDisconnectedEventHandler(LuaArgUtils.getStringFromLuaTable(paramsLuaTable, PARAMS, DEVICE_DISCONNECTED_EVENT_HANDLER));

            if (!paramsLuaTable.get(CONNECTION_FAILED_EVENT_HANDLER).isnil())
                params.setConnectionFailedEventHandler(LuaArgUtils.getStringFromLuaTable(paramsLuaTable, PARAMS, CONNECTION_FAILED_EVENT_HANDLER));

            if (!paramsLuaTable.get(DATA_RECEIVED_EVENT_HANDLER).isnil())
                params.setDataReceivedEventHandler(LuaArgUtils.getStringFromLuaTable(paramsLuaTable, PARAMS, DATA_RECEIVED_EVENT_HANDLER));

            if (!paramsLuaTable.get(INPUT_TERMINATOR).isnil())
                params.setTerminator(LuaArgUtils.getByteArrayFromLuaTable(paramsLuaTable, PARAMS, INPUT_TERMINATOR));

            if (!paramsLuaTable.get(READ_TIME).isnil())
                params.setReadTime(LuaArgUtils.getIntFromLuaTable(paramsLuaTable, PARAMS, READ_TIME));

            if (!paramsLuaTable.get(INPUT_BUFFER_LENGTH).isnil())
                params.setInputBufferLength(LuaArgUtils.getIntFromLuaTable(paramsLuaTable, PARAMS, INPUT_BUFFER_LENGTH));

            boolean result = serialLuaModule.serial.registerDeviceIfAbsent(deviceId, params);

            return LuaValue.valueOf(result);
        }
    }

    @RequiredArgsConstructor
    public static class SearchDeviceFunction extends TwoArgFunction {
        private final SerialLuaModule serialLuaModule;

        @Override
        public LuaValue call(LuaValue deviceIdLuaValue, LuaValue functionLuaValue) {
            String deviceId = LuaArgUtils.getStringFromFunctionArgs(deviceIdLuaValue, 1, "deviceId");
            LuaFunction searchFunction = LuaArgUtils.getFunctionFromFunctionArgs(functionLuaValue, 2, "searchFunction");

            serialLuaModule.serial.searchDevice(deviceId, new SearchFunction() {
                @Override
                public boolean execute(OpenPort openPort) {
                    LuaValue flagLuaValue = searchFunction.call(new OpenPortLuaTable(openPort));
                    return LuaArgUtils.getBooleanFromLuaValue(flagLuaValue, "found");
                }
            });

            return LuaValue.NIL;
        }
    }

    @RequiredArgsConstructor
    public static class UnregisterDeviceFunction extends OneArgFunction {
        private final SerialLuaModule serialLuaModule;

        @Override
        public LuaValue call(LuaValue deviceIdLuaValue) {
            String deviceId = LuaArgUtils.getStringFromFunctionArgs(deviceIdLuaValue, 1, "deviceId");

            boolean result = serialLuaModule.serial.unregisterDevice(deviceId);
            return LuaValue.valueOf(result);
        }
    }

    @RequiredArgsConstructor
    public static class WriteDataFunction extends ThreeArgFunction {
        private final SerialLuaModule serialLuaModule;

        @Override
        public LuaValue call(LuaValue deviceIdLuaValue, LuaValue bufferLuaValue, LuaValue bufferLengthLuaValue) {
            String deviceId = LuaArgUtils.getStringFromFunctionArgs(deviceIdLuaValue, 1, "deviceId");
            byte[] buffer = LuaArgUtils.getByteArrayFromFunctionArgs(bufferLuaValue, 2, "buffer");
            int length = LuaArgUtils.getIntFromFunctionArgs(bufferLengthLuaValue, 3, "bufferLength");

            int written = serialLuaModule.serial.writeData(deviceId, buffer, length);

            return LuaValue.valueOf(written);
        }
    }

    @RequiredArgsConstructor
    public static class PopInputDataFunction extends OneArgFunction {
        private final SerialLuaModule serialLuaModule;

        @Override
        public LuaValue call(LuaValue deviceIdLuaValue) {
            String deviceId = LuaArgUtils.getStringFromFunctionArgs(deviceIdLuaValue, 1, "deviceId");

            byte[] bytes = serialLuaModule.serial.popIncomingData(deviceId);
            return new LuaUserdata(bytes);
        }
    }

    @RequiredArgsConstructor
    public static class ResetInputBufferFunction extends OneArgFunction {
        private final SerialLuaModule serialLuaModule;

        @Override
        public LuaValue call(LuaValue deviceIdLuaValue) {
            String deviceId = LuaArgUtils.getStringFromFunctionArgs(deviceIdLuaValue, 1, "deviceId");

            serialLuaModule.serial.resetInputBuffer(deviceId);
            return LuaValue.NIL;
        }
    }

    @RequiredArgsConstructor
    public static class IsDeviceRegisteredFunction extends OneArgFunction {
        private final SerialLuaModule serialLuaModule;

        @Override
        public LuaValue call(LuaValue deviceIdLuaValue) {
            String deviceId = LuaArgUtils.getStringFromFunctionArgs(deviceIdLuaValue, 1, "deviceId");

            return LuaValue.valueOf(serialLuaModule.serial.isDeviceRegistered(deviceId));
        }
    }

    @RequiredArgsConstructor
    public static class IsDeviceAvailableFunction extends OneArgFunction {
        private final SerialLuaModule serialLuaModule;

        @Override
        public LuaValue call(LuaValue deviceIdLuaValue) {
            String deviceId = LuaArgUtils.getStringFromFunctionArgs(deviceIdLuaValue, 1, "deviceId");

            return LuaValue.valueOf(serialLuaModule.serial.isDeviceAvailable(deviceId));
        }
    }

    @RequiredArgsConstructor
    public static class InfoFunction extends ZeroArgFunction {
        private final SerialLuaModule serialLuaModule;

        @Override
        public LuaValue call() {
            List<Info> infoList = serialLuaModule.serial.getInfo();
            LuaTable result = new LuaTable();

            for (int i = 0; i < infoList.size(); i++) {
                result.set(i + 1, new InfoLuaTable(infoList.get(i)));
            }

            return result;
        }
    }

}
