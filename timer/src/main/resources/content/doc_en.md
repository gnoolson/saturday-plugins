<div class="pb-5">
    <a href="/plugin/timer" role="button" class="secondary">Tasks</a>
</div>



The `timer` module is used to schedule the execution of Scripts and callback functions.

_Callback execution is **not thread-safe**. The callback is executed in the same Script Sandbox in which it was created.  
It may still be executed even after the Sandbox cache has expired._

For predictable Script execution, it is recommended to use the `createTask()` function.

##### Example

```lua
local Timer = require("timer")

-- schedules a Script to run after a 1-second (1000 ms) delay;
-- returns the scheduled task id (string)
local taskName = "task_1"
local delay = 1000
local scriptId = "xxxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxx"
local args = {}

-- returns the scheduled task id (string)
local id = Timer.createTask(taskName, delay, scriptId, args)


-- schedules a callback function to run after a 5-second (5000 ms) delay;
-- returns the scheduled task id (string)
local taskName2 = "task_2"
local delay2 = 5000
local callback = function()
    -- do
end

local id2 = Timer.createCallbackTask(taskName2, delay2, callback)


-- cancels a scheduled task;
-- returns true if the task was successfully canceled
local flag = Timer.cancelTaskById(id)
-- flag = Timer.cancelTaskByName("task_1")

-- returns information about scheduled tasks (table, array)
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