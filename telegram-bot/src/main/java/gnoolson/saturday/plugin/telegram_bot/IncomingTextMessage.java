package gnoolson.saturday.plugin.telegram_bot;

import lombok.Getter;

public class IncomingTextMessage extends IncomingUpdate {

    @Getter
    private final String text;

    /*
     *
     *
     * */
    public IncomingTextMessage(long created, Long chatId, long userId, String username, String firstName, String lastName, String text) {
        super(created, chatId, userId, username, firstName, lastName);
        this.text = text;
    }

}
