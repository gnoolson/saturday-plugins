<div class="pb-5">
    <a href="/plugin/cache" role="button" class="secondary">Keys</a>
</div>


The `cache` module provides access to an in-memory temporary data store.

Stored data is automatically removed after the specified storage time expires.

##### Example
```lua
local Cache = require("cache")

-- stores data;
-- arguments:
--   key (string),
--   value (any type),
--   storage time in milliseconds (number)
Cache.put("my_key", {Pi = 3.14}, 10000)

-- returns the stored value;
-- returns nil if the key does not exist
local data = Cache.get("my_key")

-- returns the stored value and extends its storage time;
-- returns nil if the key does not exist
data = Cache.getAndProlong("my_key")

-- returns all keys;
-- returns table (array)
local keys = Cache.getKeys()

-- removes the stored value;
-- returns true if the value was removed
local result = Cache.remove("my_key")
```