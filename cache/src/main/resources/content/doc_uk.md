<div class="pb-5">
    <a href="/plugin/cache" role="button" class="secondary">Ключі</a>
</div>


Модуль `cache` використовується для доступу до тимчасового сховища даних (in-memory).

Після завершення часу зберігання дані автоматично видаляються.

##### Приклад
```lua
local Cache = require("cache")

-- збереження даних;
-- аргументи:
--   ключ (string),
--   значення (будь-який тип),
--   час зберігання в мілісекундах (number)
Cache.put("my_key", {Pi = 3.14}, 10000)

-- отримання даних;
-- якщо ключ відсутній, повертає nil
local data = Cache.get("my_key")

-- отримання даних із продовженням часу зберігання;
-- якщо ключ відсутній, повертає nil
data = Cache.getAndProlong("my_key")

-- отримання всіх ключів;
-- повертає table (array)
local keys = Cache.getKeys()

-- видалення даних;
-- повертає boolean, який вказує, чи були видалені дані
local result = Cache.remove("my_key")
```