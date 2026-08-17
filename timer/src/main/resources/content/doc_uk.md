<div class="pb-5">
    <a href="/plugin/timer" role="button" class="secondary">Завдання</a>
</div>


Модуль `timer` використовується для планування запуску Сценаріїв та функцій зворотних викликів.

_Виконання функції зворотного виклику не є потокобезпечним. Функція запускається в тому самому Sandbox Сценарію де і була створена.
Функція запуститься навіть після того, як вийшов час кешування Sandbox._

Для прогнозованого виконання Сценарію, рекомендується використовувати функцію `createTask()`.

##### Приклад
```lua
local Timer = require("timer")

-- запуск Сценарію із затримкою 1 секунда (1000 мс); повертає string — id запланованого виконання
local taskName = "task_1"
local delay = 1000
local scriptId = "xxxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxx"
local args = {}

-- повертає id запланованого виконання (string)
local id = Timer.createTask(taskName, delay, scriptId, args)


-- запуск функції зворотного виклику із затримкою 5 секунд (5000 мс); повертає string — id запланованого виконання
local taskName2 = "task_2"
local delay2 = 5000
local callback = function()
    -- do
end

local id2 = Timer.createCallbackTask(taskName2, delay2, callback)


-- скасування запланованого виконання Сценарію.
-- Повертає boolean, якщо було що скасувати
local flag = Timer.cancelTaskById(id)
-- flag = Timer.cancelTaskByName("task_1")

-- інформація про заплановані завдання (table, array)
local info = Timer.info()

Output.print(info)
--[[
{
    "1":{
        "delay":1000,
        "name":"task_1",
        "id":"xxxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxx"
    }
}
]]
```