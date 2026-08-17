package gnoolson.saturday.plugin.telegram_bot;

import java.util.List;
import java.util.Optional;

public interface TelegramBot {

    String getId();

    String getUsername();

    long getCreatedTs();

    Optional<IncomingUpdate> getUpdate();

    void setCommandsIfAbsent(List<Command> commands);

    void stop();

    void send(OutgoingMessage outgoingMessage);

    void clearCommands();

}
