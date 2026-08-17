Модуль `http_client` використовується для виконання HTTP-запитів.

##### Приклад
```lua
local HttpClient = require("http_client")

-- об'єкт HTTP-запиту
local request = {
    url = "https://jsonplaceholder.typicode.com/posts",
    method = "POST",
    headers = {
        ["Content-Type"] = "application/json; charset=UTF-8",
        ["super-header-name"] = "super-header-value"
    },
    body = "{\"key_1\":\"value_1\"}",
    connectionTimeout = 10
}

-- надсилання HTTP-запиту; повертає response (table)
local response = HttpClient.send(request)

Output.print(response)

-- HTTP-статус (number)
local code = response.code

-- HTTP-заголовки (table)
local headers = response.headers

-- тіло відповіді (string)
local body = response.body

Output.print(body)
--[[
{
    "key_1": "value_1",
    "id": 101
}
]]
```