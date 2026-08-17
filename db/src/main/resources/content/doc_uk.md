<div class="pb-5">
    <a href="/plugin/db" role="button" class="secondary">Data Sources</a>
</div>

Модуль `db` використовується для роботи з базами даних.

Перед початком роботи необхідно отримати або створити об'єкт класу `DataSource`. Для цього потрібно викликати функцію `getOrCreateDataSource()`, передавши:

- ідентифікатор `DataSource` (ключ для кешування);
- шлях до файлу `*.properties` з параметрами підключення.

Створений `DataSource` кешується, тому повторні виклики з однаковим ідентифікатором повертають той самий об'єкт.

Закривати `DataSource` необов'язково. Поки працює **Saturday**, усі створені `DataSource` зберігаються всередині Плагіна та можуть повторно використовуватися іншими Сценаріями.

Для виконання SQL-запитів необхідно отримати об'єкт класу `Connection` із `DataSource`. Після цього можна виконувати такі операції:

- `query`
- `update`
- `commit`
- `rollback`
- `close`

Об'єкти `Connection` отримуються з пулу підключень `DataSource` (використовується `org.apache.commons.dbcp2.BasicDataSource`) і повторно використовуються системою.

Після завершення роботи підключення рекомендується викликати `close()`. Якщо цього не зробити, підключення буде автоматично повернуте до пулу після завершення роботи Сценарію.

##### Приклад
```lua
local DB = require("db")

-- отримання DataSource (table)
local dataSource = DB.getOrCreateDataSource(
"mariadb",
"file:./plugins/db/mariadb.properties"
)

--[[
# Вміст файлу mariadb.properties

db.driver_path=file:/data/projects/java/saturday/lib/mariadb-java-client-3.5.5.jar
db.driver_class_name=org.mariadb.jdbc.Driver
db.jdbc_url=jdbc:mariadb://localhost:3306/
db.username=admin
db.password=root
]]

-- функція getConnection(transactionLevel) приймає один із рівнів ізоляції транзакцій
-- (підтримка залежить від конкретної СКБД):
--
-- "READ_UNCOMMITTED" - дозволяє читати незакомічені дані;
-- "READ_COMMITTED"   - дозволяє читати лише закомічені дані;
-- "REPEATABLE_READ"  - гарантує однаковий результат повторного читання;
-- "SERIALIZABLE"     - найвищий рівень ізоляції.
--
-- За замовчуванням використовується "REPEATABLE_READ".

local connection = dataSource:getConnection()

-- оновлення даних
local updatedRows1 =
connection:update("CREATE DATABASE IF NOT EXISTS my_database")

local updatedRows2 =
connection:update("CREATE TABLE IF NOT EXISTS my_database.users (id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, name TEXT, age INTEGER)")

local updatedRows3 =
connection:update("INSERT INTO my_database.users (name, age) VALUES ('root', 35)")

connection:commit()
-- connection:rollback()

-- виконання SELECT; повертає ResultSet (table)
local result = connection:query("SELECT * FROM my_database.users")

local user = {
name = result[1].name,
age = result[1].age
}

-- закриття підключення (необов'язково)
connection:close()

-- інформація про відкриті DataSource (table, array)
local list = DB.info()

Output.print(list)

--[[
{
"1":{
"idleConnections":1,
"id":"mariadb",
"driverClassName":"org.mariadb.jdbc.Driver",
"jdbcUrl":"jdbc:mariadb://localhost:3306/",
"activeConnections":0,
"username":"admin",
"driverPath":"file:/data/projects/java/saturday/lib/mariadb-java-client-3.5.5.jar"
}
}
]]

-- закриття DataSource
DB.closeDataSource(dataSource.id)
```