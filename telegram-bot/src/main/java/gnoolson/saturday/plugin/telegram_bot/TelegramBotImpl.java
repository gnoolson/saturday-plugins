package gnoolson.saturday.plugin.telegram_bot;

import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import gnoolson.saturday_plugin_api.ScriptLauncherGateway;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

@Log4j2
public class TelegramBotImpl implements TelegramBot {

    private static final int MAX_QUEUE_LENGTH = 64;
    private final ConcurrentLinkedQueue<IncomingUpdate> queue = new ConcurrentLinkedQueue<>();
    private final com.pengrad.telegrambot.TelegramBot pengradTgBot;
    private final String scriptId;
    private final ScriptLauncherGateway scriptLauncherGateway;
    private final boolean executeScript;
    @Getter
    private final String id;
    @Getter
    private final String username;
    @Getter
    private final long createdTs;
    private int commandsHashCode = 0;

    /*
     *
     *
     * */
    public TelegramBotImpl(String id, String username, String token, String scriptId,
                           boolean executeScript, ScriptLauncherGateway scriptLauncherGateway) {
        this.id = id;
        this.username = username;
        this.scriptId = scriptId;
        this.scriptLauncherGateway = scriptLauncherGateway;
        this.executeScript = executeScript;
        this.createdTs = System.currentTimeMillis();
        this.pengradTgBot = new com.pengrad.telegrambot.TelegramBot(token);
        this.pengradTgBot.setUpdatesListener((updates) -> {
            updates.forEach(this::handle);
            return -1;
        });
    }

    @Override
    public synchronized Optional<IncomingUpdate> getUpdate() {
        IncomingUpdate incomingUpdate = queue.poll();

        if (incomingUpdate == null) {
            return Optional.empty();
        } else {
            return Optional.of(incomingUpdate);
        }
    }

    @Override
    public synchronized void clearCommands() {
        if (commandsHashCode == 0)
            return;

        pengradTgBot.execute(new SetMyCommands());
        commandsHashCode = 0;
    }

    @Override
    public synchronized void setCommandsIfAbsent(List<Command> commands) {
        int hc = commands.hashCode();

        if (commandsHashCode == hc)
            return;

        BotCommand[] result = new BotCommand[commands.size()];
        int i = 0;

        for (Command command : commands) {
            result[i] = new BotCommand(command.getCommand(), command.getDescription());
            ++i;
        }

        SetMyCommands setMyCommands = new SetMyCommands(result);
        pengradTgBot.execute(setMyCommands);
        commandsHashCode = hc;
    }

    @Override
    public synchronized void stop() {
        pengradTgBot.removeGetUpdatesListener();
        pengradTgBot.shutdown();
    }

    @Override
    public synchronized void send(OutgoingMessage outgoingMessage) {
        SendMessage message = new SendMessage(outgoingMessage.getChatId(), outgoingMessage.getText());
        setMarkup(message, outgoingMessage);
        pengradTgBot.execute(message);
    }

    /*
     *
     *
     * */
    private synchronized void handle(Update update) {
        try {
            if (queue.size() < MAX_QUEUE_LENGTH)
                queue.poll();

            if (update.callbackQuery() != null) {
                CallbackQuery callbackQuery = update.callbackQuery();
                pengradTgBot.execute(new AnswerCallbackQuery(callbackQuery.id()));

                User user = update.callbackQuery().from();
                String username = user.username();
                String firstName = user.firstName();
                String lastName = user.lastName();
                Long userId = user.id();
                Integer date = callbackQuery.maybeInaccessibleMessage().date();

                queue.add(new IncomingCallbackQuery((long) date, callbackQuery.maybeInaccessibleMessage().chat().id(), userId, username, firstName, lastName, callbackQuery.data()));
            }

            if (update.message() != null) {
                Message message = update.message();
                User user = message.from();
                String username = user.username();
                String firstName = user.firstName();
                String lastName = user.lastName();
                Long userId = user.id();
                Integer date = message.date();

                queue.add(new IncomingTextMessage((long) date, message.chat().id(), userId, username, firstName, lastName, message.text()));
            }

            if (executeScript)
                scriptLauncherGateway.execute(scriptId, Collections.emptyMap());
        } catch (Exception e) {
            log.warn("Exception", e);
        }
    }

    private void setMarkup(SendMessage message, OutgoingMessage outgoingMessage) {
        Markup markup = outgoingMessage.getMarkup();
        if (!markup.isEmpty()) {
            InlineKeyboardButton[][] rows = new InlineKeyboardButton[markup.getKeyboard().size()][];
            int i = 0;

            for (List<Button> buttons : markup.getKeyboard()) {
                InlineKeyboardButton[] cols = new InlineKeyboardButton[buttons.size()];
                int j = 0;

                for (Button button : buttons) {
                    InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(button.getText());
                    inlineKeyboardButton.setCallbackData(button.getCallbackData());
                    cols[j] = inlineKeyboardButton;
                    ++j;
                }

                rows[i] = cols;
                ++i;
            }

            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
            message.setReplyMarkup(inlineKeyboardMarkup);
        }
    }

}
