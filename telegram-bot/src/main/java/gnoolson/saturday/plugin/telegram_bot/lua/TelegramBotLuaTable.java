package gnoolson.saturday.plugin.telegram_bot.lua;

import gnoolson.luaj_utils.LuaArgUtils;
import gnoolson.saturday.plugin.telegram_bot.*;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TelegramBotLuaTable extends LuaTable {

    private final TelegramBot telegramBot;

    /*
     *
     *
     * */
    public TelegramBotLuaTable(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
        this.set("getUpdate", new GetUpdateFunction());
        this.set("send", new SendFunction());
        this.set("setCommandsIfAbsent", new SetCommandsIfAbsentFunction());
        this.set("clearCommands", new ClearCommandsFunction());
        this.set("id", telegramBot.getId());
        this.set("username", telegramBot.getUsername());
    }

    /*
     *
     *
     * */
    public static class ClearCommandsFunction extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue self) {
            TelegramBotLuaTable telegramBotLuaTable = LuaArgUtils.getSelfFromFunctionArgs(self);
            telegramBotLuaTable.telegramBot.clearCommands();
            return LuaValue.NIL;
        }
    }

    public static class SetCommandsIfAbsentFunction extends TwoArgFunction {

        public LuaValue call(LuaValue self, LuaValue commandsLuaValue) {
            TelegramBotLuaTable telegramBotLuaTable = LuaArgUtils.getSelfFromFunctionArgs(self);
            LuaTable commandsLuaTable = LuaArgUtils.getLuaTableFromFunctionArgs(commandsLuaValue, 2, "commands");

            List<Command> commands = this.convertLuaTableToCommands(commandsLuaTable);
            telegramBotLuaTable.telegramBot.setCommandsIfAbsent(commands);
            return LuaValue.NIL;
        }

        private List<Command> convertLuaTableToCommands(LuaTable commandsLuaTable) {
            LuaValue[] keys = commandsLuaTable.keys();
            List<Command> commands = new ArrayList<>(keys.length);

            for (LuaValue key : keys) {
                LuaValue luaValue = commandsLuaTable.get(key);

                LuaTable commandLuaTable = LuaArgUtils.getLuaTableFromLuaValue(luaValue, "command");
                String command = LuaArgUtils.getStringFromLuaTable(commandLuaTable, "command", "command");
                String description = LuaArgUtils.getStringFromLuaTable(commandLuaTable, "command", "description");

                commands.add(new Command(command, description));
            }

            return commands;
        }
    }

    public static class GetUpdateFunction extends OneArgFunction {
        public LuaValue call(LuaValue self) {
            TelegramBotLuaTable telegramBotLuaTable = LuaArgUtils.getSelfFromFunctionArgs(self);
            Optional<IncomingUpdate> updateOpt = telegramBotLuaTable.telegramBot.getUpdate();
            return (LuaValue) (updateOpt.isPresent() ? new UpdateLuaTable((IncomingUpdate) updateOpt.get()) : LuaValue.NIL);
        }
    }

    public static class SendFunction extends TwoArgFunction {

        public LuaValue call(LuaValue self, LuaValue dataLuaValue) {
            TelegramBotLuaTable telegramBotLuaTable = LuaArgUtils.getSelfFromFunctionArgs(self);
            OutgoingMessage outgoingMessage = convert(LuaArgUtils.getLuaTableFromFunctionArgs(dataLuaValue, 2, "data"));
            telegramBotLuaTable.telegramBot.send(outgoingMessage);
            return LuaValue.NIL;
        }

        private OutgoingMessage convert(LuaTable data) {
            String chatId = LuaArgUtils.getStringFromLuaTable(data, "data", "chatId");
            String text = LuaArgUtils.getStringFromLuaTable(data, "data", "text");

            if (data.get("markup").isnil()) {
                return new OutgoingMessage(chatId, text, new Markup());
            }

            LuaTable markupMatrixLuaTable = LuaArgUtils.getLuaTableFromLuaTable(data, "data", "markup");
            List<List<Button>> keyboard = new ArrayList<>();

            for (int i = 1; i <= markupMatrixLuaTable.length(); i++) {
                LuaValue rowLuaValue = markupMatrixLuaTable.get(i);
                LuaTable rowLuaTable = LuaArgUtils.getLuaTableFromLuaValue(rowLuaValue, "row");

                List<Button> rows = new ArrayList<>();

                for (int j = 1; j <= rowLuaTable.length(); j++) {
                    LuaValue buttonLuaValue = rowLuaTable.get(j);
                    LuaTable buttonLuaTable = LuaArgUtils.getLuaTableFromLuaValue(buttonLuaValue, "button");

                    String buttonText = LuaArgUtils.getStringFromLuaTable(buttonLuaTable, "button", "text");
                    String buttonCallbackData = LuaArgUtils.getStringFromLuaTable(buttonLuaTable, "button", "callbackData");

                    rows.add(new Button(buttonText, buttonCallbackData));
                }

                keyboard.add(rows);
            }

            return new OutgoingMessage(chatId, text, new Markup(keyboard));
        }
    }

}
