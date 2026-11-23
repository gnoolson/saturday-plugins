<div class="pb-5">
    <a href="/plugin/event-bus">Events and subscriptions</a>
</div>

The `event_bus` module is used for message passing, allowing different scripts to communicate directly without tight coupling.

### How it works

1. **Script_1** subscribes to a chosen topic.
2. **Script_2** publishes a message to that topic.
3. `event_bus` triggers **Script_1** and passes the message sent by **Script_2** into `Args`.

A script cannot publish to a topic it is currently subscribed to!

### API
```lua
local eventBus = require("event_bus")

-- subscribe to the specified topic
-- returns true if subscribed; false if already subscribed
local subscribed = eventBus.on("event_name_1")

-- unsubscribe from the specified topic
-- returns true if unsubscribed; false if was not subscribed
local unsubscribed = eventBus.off("event_name_1")

-- publish a message to the specified topic
-- payload must be a key-value table (not an array)
-- returns the number of triggered scripts
local recipients = eventBus.emit("event_name_1", {pi = 3.14})

-- read payload fields via script Args
-- local pi = Args.pi

-- retrieve active subscriptions info
local info = eventBus.info()

Output.print(info)
--[[ outputs:
{
  "event_name_1" : [ "764f6190-c71d-4fe2-99f5-99c707a06cf1" ]
}
]]
```