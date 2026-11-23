package gnoolson.saturday.plugin.event_bus.lua;

import gnoolson.saturday.plugin.event_bus.EventBus;
import gnoolson.saturday.plugin.event_bus.EventBusPlugin;
import gnoolson.saturday_plugin_api.ScriptLauncherGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.mockito.Mockito;

import java.util.HashMap;

class EventBusLuaModuleTest {

    @Test
    public void general() throws InterruptedException {
        ScriptLauncherGateway mock = Mockito.mock(ScriptLauncherGateway.class);

        EventBusPlugin eventBusPlugin = new EventBusPlugin();
        eventBusPlugin.setScriptLauncherGateway(mock);
        eventBusPlugin.start();
        EventBus eventBus = eventBusPlugin.getEventBus();

        EventBusLuaModule eventBusLuaModule_1 = new EventBusLuaModule("SCRIPT_ID_1");
        eventBusLuaModule_1.updateFunctionality(eventBus);

        EventBusLuaModule eventBusLuaModule_2 = new EventBusLuaModule("SCRIPT_ID_2");
        eventBusLuaModule_2.updateFunctionality(eventBus);


        LuaValue eventName = LuaValue.valueOf("TEST");

        {
            EventBusLuaModule.EmitFunction emitFunction = new EventBusLuaModule.EmitFunction(eventBusLuaModule_1);
            LuaValue result = emitFunction.call(eventName, new LuaTable() {{
                set("key", "value");
            }});

            Thread.sleep(100);

            Mockito.verify(mock, Mockito.never()).execute(Mockito.any(), Mockito.any());

            Assertions.assertEquals(LuaValue.valueOf(0), result);
        }

        {
            EventBusLuaModule.OnFunction onFunction = new EventBusLuaModule.OnFunction(eventBusLuaModule_2);

            LuaValue result = onFunction.call(eventName);
            Assertions.assertEquals(LuaValue.valueOf(true), result);
        }

        {
            EventBusLuaModule.EmitFunction emitFunction = new EventBusLuaModule.EmitFunction(eventBusLuaModule_1);
            LuaValue result = emitFunction.call(eventName, new LuaTable() {{
                set("key", "value");
            }});

            Thread.sleep(100);

            Mockito.reset(mock);
            Mockito.verify(mock, Mockito.atMostOnce()).execute("___SCRIPT_ID___", new HashMap<String, Object>() {{
                put("key", "value");
            }});
            Assertions.assertEquals(LuaValue.valueOf(1), result);
        }

        {
            EventBusLuaModule.OffFunction offFunction = new EventBusLuaModule.OffFunction(eventBusLuaModule_2);

            LuaValue result = offFunction.call(eventName);
            Assertions.assertEquals(LuaValue.valueOf(true), result);
        }

        {
            EventBusLuaModule.EmitFunction emitFunction = new EventBusLuaModule.EmitFunction(eventBusLuaModule_1);
            LuaValue result = emitFunction.call(eventName, new LuaTable() {{
                set("key", "value");
            }});

            Thread.sleep(100);

            Mockito.reset(mock);
            Mockito.verify(mock, Mockito.never()).execute(Mockito.any(), Mockito.any());
            Assertions.assertEquals(LuaValue.valueOf(0), result);
        }

    }

}