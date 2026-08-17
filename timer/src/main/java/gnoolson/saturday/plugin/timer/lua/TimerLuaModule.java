package gnoolson.saturday.plugin.timer.lua;

import gnoolson.luaj_utils.LuaArgUtils;
import gnoolson.luaj_utils.LuaTypeConverter;
import gnoolson.saturday.plugin.timer.Callback;
import gnoolson.saturday.plugin.timer.Info;
import gnoolson.saturday.plugin.timer.Timer;
import gnoolson.saturday_plugin_api.Functionality;
import gnoolson.saturday_plugin_api.LuaModule;
import lombok.RequiredArgsConstructor;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class TimerLuaModule extends LuaModule {

    private Timer timer;

    /*
     *
     *
     * */
    public TimerLuaModule() {
        set("createTask", new CreateTaskFunction(this));
        set("createCallbackTask", new CreateCallbackTaskFunction(this));

        set("cancelTaskById", new CancelTaskByIdFunction(this));
        set("cancelTaskByName", new CancelTaskByNameFunction(this));
        set("info", new InfoFunction(this));
    }

    @Override
    public String name() {
        return "timer";
    }

    @Override
    public void updateFunctionality(Functionality functionality) {
        this.timer = (Timer) functionality;
    }

    @Override
    public void release() {
    }

    /*
     *
     *
     * */
    @RequiredArgsConstructor
    public static class CreateTaskFunction extends VarArgFunction {

        private final TimerLuaModule timerLuaModule;

        @Override
        public Varargs invoke(Varargs param) {
            String name = LuaArgUtils.getStringFromFunctionArgs(LuaArgUtils.getLuaValueFromFunctionVarargs(param, 1, "taskName"), 1, "taskName");
            long delay = LuaArgUtils.getLongFromFunctionArgs(LuaArgUtils.getLuaValueFromFunctionVarargs(param, 2, "delay"), 2, "delay");
            String scriptId = LuaArgUtils.getStringFromFunctionArgs(LuaArgUtils.getLuaValueFromFunctionVarargs(param, 3, "scriptId"), 3, "scriptId");
            LuaTable argsLuaTable = LuaArgUtils.getLuaTableFromFunctionArgs(LuaArgUtils.getLuaValueFromFunctionVarargs(param, 4, "args"), 4, "args");

            Map<String, Object> args = (Map) LuaTypeConverter.toObject(argsLuaTable);

            UUID id = timerLuaModule.timer.createTask(
                    name,
                    delay,
                    scriptId,
                    args
            );

            return LuaString.valueOf(id.toString());
        }

    }

    @RequiredArgsConstructor
    public static class CreateCallbackTaskFunction extends ThreeArgFunction {

        private final TimerLuaModule timerLuaModule;

        @Override
        public LuaValue call(LuaValue nameLuaValue, LuaValue delayLuaValue, LuaValue callbackLuaValue) {
            String name = LuaArgUtils.getStringFromFunctionArgs(nameLuaValue, 1, "taskName");
            long delay = LuaArgUtils.getLongFromFunctionArgs(delayLuaValue, 2, "delay");
            LuaFunction callback = LuaArgUtils.getFunctionFromFunctionArgs(callbackLuaValue, 3, "callback");

            UUID id = timerLuaModule.timer.createTask(
                    name,
                    delay,
                    new Callback() {
                        @Override
                        public void execute() {
                            callback.call();
                        }
                    }
            );

            return LuaString.valueOf(id.toString());
        }

    }

    @RequiredArgsConstructor
    public static class CancelTaskByIdFunction extends OneArgFunction {

        private final TimerLuaModule timerLuaModule;

        @Override
        public LuaValue call(LuaValue idLuaValue) {
            String taskId = LuaArgUtils.getStringFromFunctionArgs(idLuaValue, 1, "taskId");
            boolean result = timerLuaModule.timer.cancelTaskById(UUID.fromString(taskId));
            return LuaBoolean.valueOf(result);
        }
    }

    @RequiredArgsConstructor
    public static class CancelTaskByNameFunction extends OneArgFunction {

        private final TimerLuaModule timerLuaModule;

        @Override
        public LuaValue call(LuaValue taskNameLuaValue) {
            String taskName = LuaArgUtils.getStringFromFunctionArgs(taskNameLuaValue, 1, "taskName");
            boolean result = timerLuaModule.timer.cancelTaskByName(taskName);
            return LuaBoolean.valueOf(result);
        }
    }

    @RequiredArgsConstructor
    public static class InfoFunction extends ZeroArgFunction {

        private final TimerLuaModule timerLuaModule;

        @Override
        public LuaValue call() {
            Collection<Info> infoList = timerLuaModule.timer.info();
            LuaTable result = new LuaTable();

            int index = 1;
            for (Info info : infoList) {
                result.set(index, new InfoLuaTable(info));
                index++;
            }

            return result;
        }
    }

}
