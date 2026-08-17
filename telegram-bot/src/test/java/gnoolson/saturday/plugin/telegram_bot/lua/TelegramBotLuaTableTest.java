package gnoolson.saturday.plugin.telegram_bot.lua;

import gnoolson.saturday.plugin.telegram_bot.Button;
import gnoolson.saturday.plugin.telegram_bot.Markup;
import gnoolson.saturday.plugin.telegram_bot.OutgoingMessage;
import gnoolson.saturday.plugin.telegram_bot.TelegramBot;
import org.junit.jupiter.api.Test;
import org.luaj.vm2.LuaTable;
import org.mockito.Mockito;

import java.util.Arrays;

class TelegramBotLuaTableTest {

    @Test
    public void send() {
        TelegramBot telegramBot = Mockito.mock(TelegramBot.class);
        Mockito.when(telegramBot.getId()).thenReturn("BOT_ID");
        Mockito.when(telegramBot.getUsername()).thenReturn("BOT_USERNAME");

        TelegramBotLuaTable telegramBotLuaTable = new TelegramBotLuaTable(telegramBot);

        TelegramBotLuaTable.SendFunction sendFunction = new TelegramBotLuaTable.SendFunction();

        LuaTable dataLuaTable = new LuaTable();
        dataLuaTable.set("text", "_TEXT_");
        dataLuaTable.set("chatId", "CHAT_ID");
        LuaTable markupLuaTable = new LuaTable();
        LuaTable firstRowLuaTable = new LuaTable();
        LuaTable secondRowLuaTable = new LuaTable();

        LuaTable button11 = new LuaTable();
        button11.set("text", "Text Button1 Row1");
        button11.set("callbackData", "/callback_name_1/userId");

        LuaTable button12 = new LuaTable();
        button12.set("text", "Text Button2 Row1");
        button12.set("callbackData", "/callback_name_2/userId");

        firstRowLuaTable.set(1, button11);
        firstRowLuaTable.set(2, button12);

        LuaTable button21 = new LuaTable();
        button21.set("text", "Text Button3 Row2");
        button21.set("callbackData", "/callback_name_3/userId");

        LuaTable button22 = new LuaTable();
        button22.set("text", "Text Button4 Row2");
        button22.set("callbackData", "/callback_name_4/userId");

        secondRowLuaTable.set(1, button21);
        secondRowLuaTable.set(2, button22);

        markupLuaTable.set(1, firstRowLuaTable);
        markupLuaTable.set(2, secondRowLuaTable);

        dataLuaTable.set("markup", markupLuaTable);
        sendFunction.call(telegramBotLuaTable, dataLuaTable);


        OutgoingMessage expected = new OutgoingMessage("CHAT_ID", "_TEXT_", new Markup(
                Arrays.asList(
                        Arrays.asList(new Button("Text Button1 Row1", "/callback_name_1/userId"), new Button("Text Button2 Row1", "/callback_name_2/userId")),
                        Arrays.asList(new Button("Text Button3 Row2", "/callback_name_3/userId"), new Button("Text Button4 Row2", "/callback_name_4/userId"))
                )
        ));
        Mockito.verify(telegramBot, Mockito.times(1)).send(expected);
    }

}