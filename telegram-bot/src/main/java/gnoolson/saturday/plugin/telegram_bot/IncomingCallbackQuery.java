package gnoolson.saturday.plugin.telegram_bot;

import lombok.Getter;

public class IncomingCallbackQuery extends IncomingUpdate {

    @Getter
    private final String data;

    /*
     *
     *
     * */
    public IncomingCallbackQuery(long created, Long chatId, long userId, String username, String firstName, String lastName, String data) {
        super(created, chatId, userId, username, firstName, lastName);
        this.data = data;
    }

}
