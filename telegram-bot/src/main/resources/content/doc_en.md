<div class="pb-5">
    <a href="/plugin/tg" role="button" class="secondary">Bots</a>
</div>


The `tg` module provides basic functionality for working with Telegram bots. A bot can:
* process incoming messages;
* send text messages;
* send menus (button layouts);
* process user commands.

The module uses the Java library `com.pengrad.telegrambot.TelegramBot`.

Before using the bot, you must obtain or create a bot instance by calling the `getOrCreateBot()` function and providing:

* the bot identifier (used as the cache key);
* the path to the `*.properties` configuration file.

The created bot is automatically started and cached. Subsequent calls with the same identifier return the same bot instance.

It is recommended to create a dedicated Script for bot initialization and startup. This Script can be executed automatically after **Saturday** starts by using **Autostart**.

Incoming messages can be processed in two ways:

* the bot automatically executes the specified Script whenever a new message is received. To enable this behavior, configure the corresponding options in the `.properties` file (see the example below);
* a Script is executed by a Schedule (for example, every second) and manually checks for new incoming messages.

##### Example

```lua
local TG = require("tg")

-- returns a bot instance (table)
local bot = TG.getOrCreateBot("luaj_bot", "file:./plugins/telegram-bot/luaj_bot.properties")

--[[
# Contents of luaj_bot.properties

tg.token=1234567890:AAEaO1Xc7W8TNmLPVgo9Ri1161wYV4d4mpE           # Bot token obtained from BotFather
tg.username=Luaj_bot                                            # Bot username specified during creation
tg.script.execute=true                                          # Execute a Script when the bot receives a new message
tg.script.script_id=xxxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxx          # Script ID. Can be omitted if tg.script.execute=false
]]

-- sets the commands displayed to users when they type "/"
-- commands are updated only if the current set differs
bot:setCommandsIfAbsent({
    { command = "/help", description = "Bot information" }, -- selecting this command sends /{COMMAND}@{BOT_USERNAME}
    { command = "/menu", description = "Menu" },
    { command = "/action_1", description = "Some action 1" },
    { command = "/action_2", description = "Some action 2" },
    { command = "/action_3", description = "Some action 3" },
    { command = "/action_n", description = "Some action n" },
})

-- remove all commands if necessary
-- bot:clearCommands()

-- send a text message
--[[
bot:send({
    text = "hello world",
    chatId = 1111111111111
})
]]

-- process queued incoming updates
while true do
    local update = bot:getUpdate() -- returns an update (table)

    if update == nil then
        break
    end

    -- update can represent either a text message or a callback query (button click)
    -- update.userId      -- example: 1111111111          -- sender user ID
    -- update.created     -- example: 1111111111          -- message timestamp (ms)
    -- update.text        -- example: "/start@Luaj_bot"   -- text message (if applicable)
    -- update.data        -- example: "/callback_name_1"  -- callback data (if applicable)
    -- update.firstName   -- example: "NAME1234"          -- sender's first name
    -- update.chatId      -- example: 1111111111111       -- chat ID

    -- process a text message
    if update.text then
        -- check whether the command is "/menu" (full command "/menu@bot_username")
        if update.text == ("/menu@" .. bot.username) then
            bot:send({
                text = "Demo menu for " .. update.firstName,
                chatId = update.chatId,
                markup = {
                    {
                        -- appending userId ensures only this user can trigger the callback
                        { text = "Button1 Row1", callbackData = "/callback_name_1/" .. update.userId },
                        { text = "Button2 Row1", callbackData = "/callback_name_2/" .. update.userId },
                    },
                    {
                        { text = "Button3 Row2", callbackData = "/callback_name_3/" .. update.userId },
                        { text = "Button4 Row2", callbackData = "/callback_name_4/" .. update.userId },
                    },
                    -- additional rows
                },
            })
        end
    end

    -- process a callback query (button click)
    if update.data then
        if update.data == ("/callback_name_1/" .. update.userId) then
            bot:send({
                text = update.firstName .. " clicked 'Button1 Row1'",
                chatId = update.chatId,
            })
        end
    end
end

-- stop the bot
-- TG.stopBot(bot.id)

-- returns information about active bots (table, array)
local list = TG.info()

Output.print(list)

-- output:
--{
--  "1":{
--      "created":1777379372296,
--      "id":"luaj_bot",
--      "username":"Luaj_bot"
--     }
--}
```