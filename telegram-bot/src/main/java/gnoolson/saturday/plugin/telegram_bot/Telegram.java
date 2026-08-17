package gnoolson.saturday.plugin.telegram_bot;

import gnoolson.saturday_plugin_api.Functionality;

import java.util.List;

public interface Telegram extends Functionality {

    TelegramBot getOrCreateBot(String var1, String var2);

    void shutdown();

    void stop(String var1);

    List<BotInfo> info();

}
