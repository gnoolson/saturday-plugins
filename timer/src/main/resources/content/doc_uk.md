<div class="pb-5">
    <a href="/plugin/timer">Завдання</a>
</div>

Модуль `timer` використовується для планування запуску сценаріїв.

## Приклад
```lua
local Timer = require("timer")

-- запуск сценарію із затримкою 1 секунда (1000 мс); 
-- повертає string — Id запланованого виконання
local taskName = "task_1"
local delay = 1000
local scriptId = "xxxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxx"
local args = {}

-- повертає id запланованого виконання (string)
local id = Timer.createTask(taskName, delay, scriptId, args)

-- скасування запланованого виконання сценарію.
-- повертає true, якщо було що скасувати
local flag = Timer.cancelTaskById(id)
-- local flag = Timer.cancelTaskByName("task_1")

-- перевірка чи існує завдання
-- повертає true, якщо завдання було заплановано 
-- local flag = Timer.taskExistsById(id)
-- local flag = Timer.taskExistsByName("task_1")

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