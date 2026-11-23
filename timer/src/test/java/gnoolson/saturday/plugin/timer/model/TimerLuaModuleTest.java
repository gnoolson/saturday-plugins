package gnoolson.saturday.plugin.timer.model;

import gnoolson.luaj_utils.LuaTypeConverter;
import gnoolson.saturday.plugin.timer.Timer;
import gnoolson.saturday.plugin.timer.TimerImpl;
import gnoolson.saturday.plugin.timer.lua.TimerLuaModule;
import gnoolson.saturday_plugin_api.ScriptLauncherGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class TimerLuaModuleTest {

    @Test
    public void create_task() throws InterruptedException {
        TimerLuaModule timerLuaModule = new TimerLuaModule();

        Map<String, Object> argsData = new HashMap<String, Object>() {{
            put("key", "value");
        }};

        ScriptLauncherGateway scriptLauncherGateway = Mockito.mock(ScriptLauncherGateway.class);

        Timer timer = new TimerImpl(scriptLauncherGateway);
        timerLuaModule.updateFunctionality(timer);

        TimerLuaModule.CreateTaskFunction createTaskFunction = new TimerLuaModule.CreateTaskFunction(timerLuaModule);
        String scriptId = UUID.randomUUID().toString();

        Varargs arg = createTaskFunction.invoke(LuaValue.varargsOf(new LuaValue[]{
                LuaValue.valueOf("test_task"),
                LuaValue.valueOf(100),
                LuaValue.valueOf(scriptId),
                LuaTypeConverter.toLua(argsData)
        }));

        UUID.fromString(arg.checkjstring(1));

        verify(scriptLauncherGateway, timeout(200).times(1)).execute(
                scriptId,
                argsData
        );
    }

    @Test
    public void create_task_without_arguments() throws InterruptedException {
        TimerLuaModule timerLuaModule = new TimerLuaModule();


        ScriptLauncherGateway scriptLauncherGateway = Mockito.mock(ScriptLauncherGateway.class);

        Timer timer = new TimerImpl(scriptLauncherGateway);
        timerLuaModule.updateFunctionality(timer);

        TimerLuaModule.CreateTaskFunction createTaskFunction = new TimerLuaModule.CreateTaskFunction(timerLuaModule);
        String scriptId = UUID.randomUUID().toString();

        Varargs arg = createTaskFunction.invoke(LuaValue.varargsOf(new LuaValue[]{
                LuaValue.valueOf("test_task"),
                LuaValue.valueOf(100),
                LuaValue.valueOf(scriptId),
                new LuaTable()
        }));

        UUID.fromString(arg.checkjstring(1));

        verify(scriptLauncherGateway, timeout(200).times(1)).execute(
                scriptId,
                new HashMap<>(0)
        );
    }

    @Test
    public void cancel_task() {
        TimerLuaModule timerLuaModule = new TimerLuaModule();

        Map<String, Object> argsData = new HashMap<String, Object>() {{
            put("key", "value");
        }};

        ScriptLauncherGateway scriptLauncherGateway = Mockito.mock(ScriptLauncherGateway.class);

        Timer timer = new TimerImpl(scriptLauncherGateway);
        timerLuaModule.updateFunctionality(timer);

        TimerLuaModule.CreateTaskFunction createTaskFunction = new TimerLuaModule.CreateTaskFunction(timerLuaModule);

        String scriptId = UUID.randomUUID().toString();

        Varargs arg = createTaskFunction.invoke(LuaValue.varargsOf(new LuaValue[]{
                LuaValue.valueOf("test_task"),
                LuaValue.valueOf(100),
                LuaValue.valueOf(scriptId),
                LuaTypeConverter.toLua(argsData)
        }));


        UUID.fromString(arg.checkjstring(1));

        TimerLuaModule.CancelTaskByIdFunction cancelTaskByIdFunction = new TimerLuaModule.CancelTaskByIdFunction(timerLuaModule);
        Assertions.assertTrue(cancelTaskByIdFunction.call(arg.checkjstring(1)).checkboolean());

        verify(scriptLauncherGateway, after(200).never()).execute(any(), any());
    }

}