package gnoolson.saturday.plugin.event_bus.lua;

import gnoolson.luaj_utils.LuaArgUtils;
import gnoolson.luaj_utils.LuaTypeConverter;
import gnoolson.saturday.plugin.event_bus.EventBus;
import gnoolson.saturday.plugin.event_bus.EventInfo;
import gnoolson.saturday_plugin_api.Functionality;
import gnoolson.saturday_plugin_api.LuaModule;
import lombok.RequiredArgsConstructor;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventBusLuaModule extends LuaModule {

    private EventBus eventBus;
    private final String scriptId;

    /*
     *
     *
     * */
    public EventBusLuaModule(String scriptId) {
        this.scriptId = scriptId;
        set("emit", new EmitFunction(this));
        set("on", new OnFunction(this));
        set("off", new OffFunction(this));
        set("info", new InfoFunction(this));
    }

    @Override
    public void updateFunctionality(Functionality functionality) {
        this.eventBus = (EventBus) functionality;
    }

    @Override
    public void release() {

    }

    @Override
    public String name() {
        return "event_bus";
    }

    @RequiredArgsConstructor
    public static class InfoFunction extends ZeroArgFunction {

        private final EventBusLuaModule eventBusLuaModule;

        @Override
        public LuaValue call() {
            LuaValue result = new LuaTable();
            for (EventInfo eventInfo : eventBusLuaModule.eventBus.getEventInfoList()) {
                result.set(eventInfo.getEventName(), new LuaTable() {{
                    int index = 1;
                    for (String scriptId : eventInfo.getScriptIds()) {
                        set(index, LuaValue.valueOf(scriptId));
                        index++;
                    }
                }});
            }
            return result;
        }
    }

    @RequiredArgsConstructor
    public static class EmitFunction extends TwoArgFunction {

        private final EventBusLuaModule eventBusLuaModule;

        @Override
        public LuaValue call(LuaValue eventNameLuaValue, LuaValue messageLuaValue) {
            String eventName = LuaArgUtils.getStringFromFunctionArgs(eventNameLuaValue, 1, "eventName");
            LuaTable messageLuaTable = LuaArgUtils.getLuaTableFromFunctionArgs(messageLuaValue, 2, "message");

            int result = eventBusLuaModule.eventBus.emit(eventBusLuaModule.scriptId, eventName, extractMessage(messageLuaTable));
            return LuaValue.valueOf(result);
        }

        private Map<String, Object> extractMessage(LuaTable argsLuaTable) {
            Object object = LuaTypeConverter.toObject(argsLuaTable);

            if (object instanceof Map) {
                return (Map<String, Object>) object;
            } else if (object instanceof List) {
                List list = (List) object;
                if (list.isEmpty()) { // empty list is an empty table
                    return new HashMap<>(0);
                }
            }
            throw new RuntimeException("The message must be a table (not as an array).");
        }
    }

    @RequiredArgsConstructor
    public static class OnFunction extends OneArgFunction {

        private final EventBusLuaModule eventBusLuaModule;

        @Override
        public LuaValue call(LuaValue eventNameLuaValue) {
            String eventName = LuaArgUtils.getStringFromFunctionArgs(eventNameLuaValue, 1, "eventName");

            boolean result = eventBusLuaModule.eventBus.on(eventBusLuaModule.scriptId, eventName);
            return LuaValue.valueOf(result);
        }
    }

    @RequiredArgsConstructor
    public static class OffFunction extends OneArgFunction {

        private final EventBusLuaModule eventBusLuaModule;

        @Override
        public LuaValue call(LuaValue eventNameLuaValue) {
            String eventName = LuaArgUtils.getStringFromFunctionArgs(eventNameLuaValue, 1, "eventName");

            boolean result = eventBusLuaModule.eventBus.off(eventBusLuaModule.scriptId, eventName);
            return LuaValue.valueOf(result);
        }
    }

}
