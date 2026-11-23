The `now` module is used to retrieve the current date and time.

It is an alternative to the `os.time()` and `os.date()` functions when the **JseOsLib** library is unavailable.

## Example
```lua
local Now = require("now")

-- returns the current date and time (table)
local date = Now.getDate()

-- returns the current Unix timestamp in milliseconds (number)
local ts = Now.getTime()
```