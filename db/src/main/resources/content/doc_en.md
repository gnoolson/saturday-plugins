<div class="pb-5">
    <a href="/plugin/db" role="button" class="secondary">Data Sources</a>
</div>

The `db` module is used to work with databases.

Before using the module, you must obtain or create a `DataSource` instance by calling the `getOrCreateDataSource()` function and providing:

- the `DataSource` identifier (used as the cache key);
- the path to a `*.properties` file containing the connection settings.

The created `DataSource` is cached, so subsequent calls with the same identifier return the same instance.

Closing a `DataSource` is optional. As long as **Saturday** is running, all created `DataSource` instances are stored inside the Plugin and can be reused by other Scripts.

To execute SQL statements, obtain a `Connection` instance from the `DataSource`. The following operations are available:

- `query`
- `update`
- `commit`
- `rollback`
- `close`

`Connection` instances are obtained from the `DataSource` connection pool (`org.apache.commons.dbcp2.BasicDataSource`) and are reused by the system.

After finishing your work, it is recommended to call `close()`. If you do not, the connection will be automatically returned to the pool when the Script finishes execution.

##### Example
```lua
local DB = require("db")

-- returns a DataSource (table)
local dataSource = DB.getOrCreateDataSource(
"mariadb",
"file:./plugins/db/mariadb.properties"
)

--[[
# Contents of mariadb.properties

db.driver_path=file:/data/projects/java/saturday/lib/mariadb-java-client-3.5.5.jar
db.driver_class_name=org.mariadb.jdbc.Driver
db.jdbc_url=jdbc:mariadb://localhost:3306/
db.username=admin
db.password=root
]]

-- getConnection(transactionLevel) accepts one of the following
-- transaction isolation levels (support depends on the database):
--
-- "READ_UNCOMMITTED" - allows reading uncommitted data;
-- "READ_COMMITTED"   - allows reading only committed data;
-- "REPEATABLE_READ"  - guarantees the same result for repeated reads;
-- "SERIALIZABLE"     - the highest isolation level.
--
-- The default isolation level is "REPEATABLE_READ".

local connection = dataSource:getConnection()

-- update data
local updatedRows1 =
connection:update("CREATE DATABASE IF NOT EXISTS my_database")

local updatedRows2 =
connection:update("CREATE TABLE IF NOT EXISTS my_database.users (id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, name TEXT, age INTEGER)")

local updatedRows3 =
connection:update("INSERT INTO my_database.users (name, age) VALUES ('root', 35)")

connection:commit()
-- connection:rollback()

-- executes a SELECT query; returns a ResultSet (table)
local result = connection:query("SELECT * FROM my_database.users")

local user = {
name = result[1].name,
age = result[1].age
}

-- closes the connection (optional)
connection:close()

-- returns information about open DataSource instances (table, array)
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

-- closes the DataSource
DB.closeDataSource(dataSource.id)
```