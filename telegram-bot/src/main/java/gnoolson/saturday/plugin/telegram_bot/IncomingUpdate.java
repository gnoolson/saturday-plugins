package gnoolson.saturday.plugin.telegram_bot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class IncomingUpdate {

    private final long created;
    private final long chatId;
    private final long userId;
    private final String username;
    private final String firstName;
    private final String lastName;

}
