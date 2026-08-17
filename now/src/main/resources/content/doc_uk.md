Модуль `now` використовується для отримання поточної дати та часу.

Є альтернативою функціям `os.time()` та `os.date()`, якщо бібліотека **JseOsLib** недоступна.

##### Приклад
```lua
local Now = require("now")

-- отримання поточної дати та часу (table)
local date = Now.getDate()

-- отримання поточного Unix Timestamp у мілісекундах (number)
local ts = Now.getTime()
```