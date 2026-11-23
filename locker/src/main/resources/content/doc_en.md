The `locker` module is used to prevent concurrent access to shared resources, since scripts may execute in parallel and read or modify the same data.

This is achieved by locking execution threads while they are inside critical sections.

The module uses optimistic locking and supports nested locks.

If a lock is not released before the script finishes execution, it is automatically released when the script terminates.

## Example

```lua
local Locker = require("locker")

-- attempts to acquire locks for the specified identifiers;
-- returns a lock handle (table)
local lockHandle = Locker.lockIds("id_1", "id_2", "id_n")

-- attempts to acquire a global lock;
-- returns a lock handle (table)
-- local lockHandle = Locker.lockGlobal()
-- !!! BEGIN CRITICAL SECTION !!!





-- !!! END CRITICAL SECTION !!!
-- releases the lock
lockHandle:close()
```