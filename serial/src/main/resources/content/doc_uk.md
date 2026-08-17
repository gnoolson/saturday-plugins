<div class="pb-5">
    <a href="/plugin/serial" role="button" class="secondary">Пристрої</a>
</div>


Модуль `Serial` використовується для приймання та передавання даних через послідовний порт.

Для початку роботи необхідно зареєструвати пристрій, вказавши параметри підключення. Після цього модуль автоматично підтримуватиме з'єднання
та виконуватиме обмін даними в асинхронному режимі.

Одним із основних параметрів є назва послідовного порту. Її можна вказати явно (якщо вона відома заздалегідь `port = "ttyUSB0"`) або визначити автоматично під час пошуку пристрою (`port = Serial.SEARCH`).
Рекомендується використовувати автоматичний пошук.

###### Отримання вхідних даних

Отримання вхідних даних можна організувати двома способами:
- автоматичний запуск Сценарію-обробника після надходження даних (рекомендується);
- періодична перевірка наявності даних за допомогою Розкладу.

Отримання даних не блокує потік виконання.
Після отримання першого байта дані починають накопичуватися у внутрішньому вхідному буфері, а також запускається таймер очікування завершення приймання.

Сценарій для обробки вхідних даних буде запущений, якщо виконається хоча б одна з таких умов:
- знайдено термінатор (якщо його задано);
- вхідний буфер повністю заповнений;
- минув час очікування (`readTime`).

Перед запуском Сценарію (обробки вхідних даних) вміст вхідного буфера копіюється. Отримати копію даних можна за допомогою `Serial.popInputData()`.

Дані, що знаходяться після термінатора, а також дані, які не помістилися у вхідний буфер, переносяться на початок буфера та будуть використані під час наступного циклу приймання.
Якщо під час наступного циклу приймання попередні дані ще не були отримані за допомогою `Serial.popInputData()`, вони будуть перезаписані новими.

Рекомендується встановлювати розмір вхідного буфера не меншим за максимальний розмір очікуваного пакета даних і використовувати термінатор, якщо це дозволяє протокол обміну.

###### Примітка

Модуль `Serial` не гарантує отримання повного пакета за один виклик `Serial.popInputData()`.
Він самостійно накопичує отримані байти у внутрішньому буфері та дає можливість їх отримати лише після завершення приймання (за термінатором, заповненням буфера або закінченням часу очікування).


`Serial` працює з масивами `byte[]` Java (`userdata`). Для зручної роботи з ними рекомендується використовувати внутрішній клас `ByteArray`.


##### Приклад

```lua
local Serial = require("serial")

-- реєстрація пристрою;
-- необхідно вказати ідентифікатор пристрою (id) та параметри підключення;
-- повертає true, якщо пристрій успішно зареєстровано;
-- повертає false, якщо пристрій з таким id уже зареєстровано
local flag1 = Serial.registerDeviceIfAbsent("super_device", {
    port = Serial.SEARCH,           -- або конкретна назва порту, наприклад "ttyUSB0" чи "COM1"
    baudRate = 9600,                -- необов'язково; значення за замовчуванням: 9600
    numDataBits = 8,                -- необов'язково; за замовчуванням: 8
    numStopBits = 1,                -- необов'язково; за замовчуванням: 1
    parity = 0,                     -- необов'язково; за замовчуванням: NO_PARITY; (0-NO_PARITY; 1-ODD_PARITY; 2-EVEN_PARITY; 3-MARK_PARITY; 4-SPACE_PARITY;)
    readTime = 1000,                -- необов'язково; максимальний час очікування завершення приймання даних (мс); за замовчуванням: 1000
    inputBufferLength = 1024,       -- необов'язково; розмір вхідного буфера (байт); за замовчуванням: 1024
    inputTerminator = ByteArray.fromString("\r\n\r\n"):getJBytes(), -- необов'язково; термінатор; за замовчуванням: порожній Java byte[]
    dataReceivedEventHandler = Args.scriptId,         -- необов'язково; id Сценарію для обробки вхідних даних; за замовчуванням: ""
    searchDeviceEventHandler = Args.scriptId,         -- необов'язково; id Сценарію для пошуку пристрою; за замовчуванням: ""
    deviceConnectedEventHandler = Args.scriptId,      -- необов'язково; id Сценарію для обробки підключення; за замовчуванням: ""
    deviceDisconnectedEventHandler = Args.scriptId,   -- необов'язково; id Сценарію для обробки відключення; за замовчуванням: ""
    connectionFailedEventHandler = Args.scriptId      -- необов'язково; id Сценарію для обробки помилки підключення; за замовчуванням: ""
})


-- скасування реєстрації пристрою;
-- повертає true, якщо реєстрацію було скасовано
--[[
local flag2 = Serial.unregisterDevice("super_device")
]]

-- передавання даних пристрою;
-- необхідно вказати:
--   id пристрою (string);
--   дані (Java byte[] (userdata));
--   кількість байтів для передавання;
-- повертає кількість переданих байтів
local data = ByteArray.fromString("hello super device\r\n\r\n")
local length = Serial.writeData(
    "super_device",
    data:getJBytes(),
    data:length()
)


-- отримання вхідних даних;
-- необхідно вказати id пристрою;
-- повертає Java byte[] (userdata);
-- якщо даних немає, повертає nil;
-- розмір масиву завжди дорівнює inputBufferLength
local bytes = Serial.popInputData("super_device")
local data2 = ByteArray.fromJBytes(bytes)


-- перевірка, чи підключений пристрій;
-- повертає true, якщо пристрій підключений
local flag3 = Serial.isDeviceAvailable("super_device")

-- перевірка, чи зареєстрований пристрій;
-- повертає true, якщо пристрій зареєстрований
local flag4 = Serial.isDeviceRegistered("super_device")


-- очищення внутрішнього буфера накопичення даних;
-- не видаляє дані, які вже готові до отримання через popInputData()
--[[
Serial.resetInputBuffer("super_device")
]]


-- пошук пристрою;
--
-- якщо під час реєстрації пристрою було вказано searchDeviceEventHandler,
-- Сценарій буде автоматично запущений після початку пошуку;
--
-- якщо під час реєстрації було вказано
-- port = Serial.SEARCH і searchDeviceEventHandler = "",
-- функцію Serial.searchDevice() необхідно викликати самостійно;
--
-- для пошуку потрібного пристрою необхідно реалізувати функцію пошуку;
--
-- під час пошуку операції введення/виведення (I/O) є блокуючими,
-- тому рекомендується завершувати пошук якомога швидше;
--
-- пошук можна виконувати:
--   - за метаданими порту
--     (openPort.systemPortName, openPort.portDescription);
--   - за допомогою власного протоколу.
--
-- якщо пошук запускається вручну, перевірка Args.event == Serial.SEARCH_DEVICE не потрібна.
if Args.event == Serial.SEARCH_DEVICE then

    -- функція повинна повернути true, якщо потрібний пристрій знайдено
    local searchFunction = function(openPort)

        -- openPort — тимчасово відкритий послідовний порт,
        -- доступний лише під час виконання функції пошуку;
        -- якщо функція поверне false,
        -- порт буде автоматично закрито.

        -- приклад пошуку за системною назвою порту
        -- return openPort.systemPortName == "ttyUSB0"

        -- приклад пошуку за власним протоколом
        openPort:write(ByteArray.fromString("GET_ID"):getJBytes())

        -- отримання відповіді;
        -- read(bufferLength, timeout);
        -- inputTerminator під час пошуку не використовується
        local response = ByteArray.fromJBytes(
            openPort:read(128, 1000)
        ):getString(0)

        return response == "IM_SUPER_DEVICE"
    end

    -- повертає true, якщо пристрій знайдено та підключено
    local flag = Serial.searchDevice("super_device", searchFunction)

end



-- обробка події отримання даних
if Args.event == Serial.DATA_RECEIVED then
    local data = Serial.popInputData("super_device")
    local dataString = ByteArray.fromJBytes(data):getString(0)

    if Log.isDebugEnabled() then
        Log.debug(
            "Device Id:", Args.deviceId,
            "Reason:", Args.reason,
            "Data Received:", dataString
        )
    end
end


-- обробка події підключення пристрою
if Args.event == Serial.DEVICE_CONNECTED then
    if Log.isDebugEnabled() then
        Log.debug(
            "Device Id:",
            Args.deviceId,
            "Connected!"
        )
    end
end


-- обробка події відключення пристрою
if Args.event == Serial.DEVICE_DISCONNECTED then
    if Log.isDebugEnabled() then
        Log.debug(
            "Device Id:",
            Args.deviceId,
            "Disconnected!"
        )
    end
end


-- обробка помилки підключення
if Args.event == Serial.CONNECTION_FAILED then
    if Log.isDebugEnabled() then
        Log.debug(
            "Device Id:",
            Args.deviceId,
            "Reason:",
            Args.reason
        )
    end
end


--  повертає інформацію про зареєстровані пристрої (table, array)
local infoList = Serial.info()
Output.print(infoList)

--[[ що буде надруковано
[
   {
      "dataReceivedEventHandler":"396df2c6-7507-4780-9afd-125fb76683cf",
      "connectionTimestamp":1786112452684,
      "port":"SEARCH",
      "baudRate":9600,
      "terminator":"PSk=", -- відображено в форматі Base64
      "connectionFailedEventHandler":"396df2c6-7507-4780-9afd-125fb76683cf",
      "readTime":1000,
      "numDataBits":8,
      "parity":0,
      "deviceDisconnectedEventHandler":"396df2c6-7507-4780-9afd-125fb76683cf",
      "connected":true,
      "searchDeviceEventHandler":"396df2c6-7507-4780-9afd-125fb76683cf",
      "systemPortName":"ttyUSB0",
      "deviceId":"super_device",
      "inputBufferLength":1024,
      "deviceConnectedEventHandler":"396df2c6-7507-4780-9afd-125fb76683cf",
      "numStopBits":1,
      "portDescription":"CP2102 USB to UART Bridge Controller"
   }
]
]]

```