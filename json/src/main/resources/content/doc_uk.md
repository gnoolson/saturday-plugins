Модуль `json` використовується для серіалізації та десеріалізації даних (перетворення `table` у JSON-рядок і навпаки).

## Приклад
```lua
local JSON = require("json")

-- перетворення table у JSON-рядок;
-- pretty — необов'язковий параметр
local pretty = true
local jsonString = JSON.toString({key = "value"}, pretty)

-- local jsonString = JSON.toString({key = "value"})

-- перетворення JSON-рядка у table
local table = JSON.toTable(jsonString)
```