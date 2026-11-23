<div class="pb-5">
    <a href="/plugin/timer">Tasks</a>
</div>

The `timer` module is used to schedule script execution.

## Example
```lua
local Timer = require("timer")

-- Schedules a script to run with a 1-second delay (1000 ms); 
-- returns a string representing the scheduled task Id (string)
local taskName = "task_1"
local delay = 1000
local scriptId = "xxxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxx"
local args = {}

-- Returns the scheduled task Id (string)
local id = Timer.createTask(taskName, delay, scriptId, args)

-- Cancels the scheduled script execution.
-- Returns true if the task was successfully canceled
local flag = Timer.cancelTaskById(id)
-- local flag = Timer.cancelTaskByName("task_1")

-- Checks if a task exists.
-- Returns true if the task is scheduled 
-- local flag = Timer.taskExistsById(id)
-- local flag = Timer.taskExistsByName("task_1")

-- Returns information about scheduled tasks (table, array)
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