The `json` module is used to serialize and deserialize data (convert a `table` to a JSON string and vice versa).

##### Example
```lua
local JSON = require("json")

-- converts a table to a JSON string;
-- pretty is an optional parameter
local pretty = true
local jsonString = JSON.toString({key = "value"}, pretty)

-- local jsonString = JSON.toString({key = "value"})

-- converts a JSON string to a table
local table = JSON.toTable(jsonString)
```
