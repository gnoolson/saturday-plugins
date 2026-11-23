The `http_client` module is used to send HTTP requests.

## Example
```lua
local HttpClient = require("http_client")

-- HTTP request object
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

-- sends an HTTP request; returns a response (table)
local response = HttpClient.send(request)

Output.print(response)

-- HTTP status code (number)
local code = response.code

-- HTTP headers (table)
local headers = response.headers

-- response body (string)
local body = response.body

Output.print(body)
--[[
{
    key_1": "value_1",
    "id": 101
}
]]
```