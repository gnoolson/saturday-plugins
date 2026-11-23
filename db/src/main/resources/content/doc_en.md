<div class="pb-5">
    <a href="/plugin/db">Data Sources</a>
</div>

The `db` module is used to interact with databases.

Before you begin, you need to obtain or create an instance of the `DataSource` class. To do this, call the `getOrCreateDataSource()` function and pass the following arguments:

- The `DataSource` identifier (used as a cache key);
- The path to a `*.properties` file containing connection parameters.

Once created, the `DataSource` is cached, so subsequent calls with the same identifier will return the exact same object.

Closing the `DataSource` is optional. As long as **Saturday** is running, all created `DataSource` instances are retained within the plugin and can be reused by other scripts.

To execute SQL queries, you must acquire a `Connection` object from the `DataSource`. With a connection, you can perform the following operations:

- `query`
- `update`
- `commit`
- `rollback`
- `close`

`Connection` objects are retrieved from the `DataSource` connection pool (powered by `org.apache.commons.dbcp2.BasicDataSource`) and are recycled by the system.

After finishing your work with a `Connection`, you must call `close()` to release it (even if you omit this step, the connection will be automatically returned to the pool once the script finishes execution).

## Example
```lua
local DB = require("db")

-- Retrieve a DataSource (table)
local dataSource = DB.getOrCreateDataSource(
    "mariadb",
    "file:./plugins/db/mariadb.properties"
)

--[[
# Contents of the mariadb.properties file

db.driver_path=file:/data/projects/java/saturday/plugins/db/mariadb-java-client-3.5.5.jar
db.driver_class_name=org.mariadb.jdbc.Driver
db.jdbc_url=jdbc:mariadb://localhost:3306/
db.username=admin
db.password=root
]]

-- The getConnection(transactionLevel) function accepts one of the following transaction isolation levels
-- (support depends on the specific DBMS):
--
-- "READ_UNCOMMITTED" - Allows reading uncommitted data;
-- "READ_COMMITTED"   - Allows reading only committed data;
-- "REPEATABLE_READ"  - Guarantees the same result for repeated reads;
-- "SERIALIZABLE"     - The highest level of isolation.
--
-- The default level is "REPEATABLE_READ".

local connection = dataSource:getConnection()

-- Updating data
local updatedRows1 = connection:update("CREATE DATABASE IF NOT EXISTS my_database")

local updatedRows2 = connection:update("CREATE TABLE IF NOT EXISTS my_database.users (id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, name TEXT, age INTEGER)")

local updatedRows3 = connection:update("INSERT INTO my_database.users (name, age) VALUES ('root', 35)")

connection:commit()
-- connection:rollback()

-- Executing a SELECT query; returns a ResultSet (table)
local result = connection:query("SELECT * FROM my_database.users")

local user = {
    name = result[1].name,
    age = result[1].age
}

-- Closing the connection
connection:close()

-- Information about open DataSources (table, array)
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
    "driverPath":"file:/data/projects/java/saturday/plugins/db/mariadb-java-client-3.5.5.jar"
}
}
]]

-- Closing the DataSource
DB.closeDataSource(dataSource.id)
```
