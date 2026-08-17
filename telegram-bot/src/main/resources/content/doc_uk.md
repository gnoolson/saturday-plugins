<div class="pb-5">
    <a href="/plugin/tg" role="button" class="secondary">Активні боти</a>
</div>


Модуль `tg` надає базову функціональність для роботи з Telegram-ботами. Бот може:
* обробляти повідомлення;
* надсилати текстові повідомлення;
* надсилати меню (набір кнопок);
* обробляти команди користувачів.

Для роботи використовується Java-бібліотека `com.pengrad.telegrambot.TelegramBot`.
Перед початком роботи необхідно отримати або створити екземпляр бота. Для цього потрібно викликати функцію `getOrCreateBot`, передавши:
* ідентифікатор бота (для зберігання в кеші);
* шлях до `*.properties` файлу з налаштуваннями.

Створений бот автоматично запускається та кешується. Повторні виклики з тим самим ідентифікатором повертатимуть той самий екземпляр бота.  
Рекомендується створити окремий Сценарій для ініціалізації та запуску бота. Такий Сценарій можна запускати автоматично після старту **Saturday** за допомогою Автостарту.  
Обробка вхідних повідомлень може виконуватися двома способами:
* бот автоматично запускає вказаний Сценарій після отримання нового повідомлення. Для цього необхідно вказати відповідні налаштування у `.properties` файлі (див. приклад);
* Сценарій запускається за Розкладом (наприклад, щосекунди) та самостійно перевіряє наявність нових повідомлень.

##### Приклад

```lua
local TG = require("tg")

-- повертає бота (table)
local bot = TG.getOrCreateBot("luaj_bot", "file:./plugins/telegram-bot/luaj_bot.properties")
--[[
# вміст файлу luaj_bot.properties
tg.token=1234567890:AAEaO1Xc7W8TNmLPVgo9Ri1161wYV4d4mpE           # токен, отриманий від BotFather
tg.username=Luaj_bot                                              # username бота (вказаний під час створення)
tg.script.execute=true                                            # виконувати Сценарій, коли бот отримує вхідне повідомлення
tg.script.script_id=xxxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxx            # Id Сценарію. Якщо tg.script.execute=false, можна не вказувати
]]

-- встановлення команд, які будуть відображатися користувачу в групі при "/"
-- встановлення команд буде виконано, якщо цей набір команд ще не було встановлено
-- зміна набору команд викличе повторне встановлення
bot:setCommandsIfAbsent({
    { command = "/help", description = "Інформація про бота" }, -- при виборі цієї команди буде надіслано текстове повідомлення /{COMMAND}@{BOT_USERNAME}
    { command = "/menu", description = "Меню" },
    { command = "/action_1", description = "Якась дія 1" },
    { command = "/action_2", description = "Якась дія 2" },
    { command = "/action_3", description = "Якась дія 3" },
    { command = "/action_n", description = "Якась дія n" },
})

-- якщо потрібно видалити всі команди
-- bot:clearCommands()

-- відправити текстове повідомлення
--[[
bot:send({
    text = "hello world",
    chatId = 1111111111111
})
]]

-- обробка накопичених вхідних повідомлень (оновлень)
while true do
    local update = bot:getUpdate() -- повертає оновлення (table)
    if update == nil then
        break
    end

    -- таблиця update може бути двох типів: "текстове повідомлення" або "зворотний виклик" (відповідь на кнопку)
    -- update.userId      -- приклад: 1111111111          -- id користувача, від якого отримано повідомлення
    -- update.created     -- приклад: 1111111111          -- час повідомлення (мс)
    -- update.text        -- приклад: "/start@Luaj_bot"   -- текст повідомлення (якщо це текстове повідомлення)
    -- update.data        -- приклад: "/callback_name_1"  -- назва зворотного виклику (якщо це відповідь на кнопку)
    -- update.firstName   -- приклад: "NAME1234"          -- ім'я користувача
    -- update.chatId      -- приклад: 1111111111111       -- id чату

    -- якщо було отримано текстове повідомлення
    if update.text then
        -- перевірка, чи це команда "/menu" (повний рядок "/menu@bot_username")
        if update.text == ("/menu@" .. bot.username) then
            bot:send({
                text = "Демо-меню для " .. update.firstName, -- хто натиснув кнопку
                chatId = update.chatId, -- id чату
                markup = { -- кнопки меню
                    { -- перший рядок
                        -- додавання userId до команди допоможе обробляти натискання кнопки лише цього користувача
                        { text = "Текст Button1 Row1", callbackData = "/callback_name_1/" .. update.userId },
                        { text = "Текст Button2 Row1", callbackData = "/callback_name_2/" .. update.userId },
                    },
                    { -- другий рядок
                        { text = "Текст Button3 Row2", callbackData = "/callback_name_3/" .. update.userId },
                        { text = "Текст Button4 Row2", callbackData = "/callback_name_4/" .. update.userId },
                    },
                    -- інші рядки
                },
            })
        end
    end

    -- якщо було отримано зворотний виклик (натискання кнопки меню)
    if update.data then
        if update.data == ("/callback_name_1/" .. update.userId) then
            bot:send({ -- надіслати текстове повідомлення
                text = update.firstName .. " натиснув 'Текст Button1 Row1'",
                chatId = update.chatId,
            })
        end
    end
end

-- зупинка бота
-- TG.stopBot(bot.id)

-- повертає інформацію про активних ботів (table, array)
local list = TG.info()
Output.print(list)

-- що буде надруковано
--{
--  "1":{
--      "created":1777379372296,
--      "id":"luaj_bot",
--      "username":"Luaj_bot"
--     }
--}
```