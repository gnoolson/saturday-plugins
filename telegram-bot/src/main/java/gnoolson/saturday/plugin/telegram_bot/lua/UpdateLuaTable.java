package gnoolson.saturday.plugin.telegram_bot.lua;

import gnoolson.saturday.plugin.telegram_bot.IncomingCallbackQuery;
import gnoolson.saturday.plugin.telegram_bot.IncomingTextMessage;
import gnoolson.saturday.plugin.telegram_bot.IncomingUpdate;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class UpdateLuaTable extends LuaTable {

    public UpdateLuaTable(IncomingUpdate incomingUpdate) {
        this.set("created", incomingUpdate.getCreated());
        this.set("userId", incomingUpdate.getUserId());
        this.set("chatId", incomingUpdate.getChatId());

        this.set("username", incomingUpdate.getUsername() == null ? LuaValue.NIL : LuaValue.valueOf(incomingUpdate.getUsername()));
        this.set("firstName", incomingUpdate.getFirstName() == null ? LuaValue.NIL : LuaValue.valueOf(incomingUpdate.getFirstName()));
        this.set("lastName", incomingUpdate.getLastName() == null ? LuaValue.NIL : LuaValue.valueOf(incomingUpdate.getLastName()));

        if (incomingUpdate instanceof IncomingCallbackQuery) {
            IncomingCallbackQuery callbackQuery = (IncomingCallbackQuery) incomingUpdate;
            this.set("data", callbackQuery.getData());
        } else if (incomingUpdate instanceof IncomingTextMessage) {
            IncomingTextMessage incomingTextMessage = (IncomingTextMessage) incomingUpdate;
            this.set("text", incomingTextMessage.getText());
        } else {
            throw new IllegalArgumentException(String.format("\"%s\" is not supported", incomingUpdate.getClass().getCanonicalName()));
        }
    }

}
