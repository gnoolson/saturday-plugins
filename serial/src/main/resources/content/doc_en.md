<div class="pb-5">
    <a href="/plugin/serial" role="button" class="secondary">Devices</a>
</div>


The `Serial` module is used for receiving and transmitting data through a serial port.

To start working with the module, register a device and specify its connection parameters. After that, the module will automatically maintain the connection and perform data exchange asynchronously.

One of the main parameters is the serial port name. It can be specified explicitly (if it is known in advance, `port = "ttyUSB0"`) or determined automatically during device discovery (`port = Serial.SEARCH`).
Automatic device discovery is recommended.

###### Receiving Input Data

Input data can be handled in two ways:
* automatically starting a handler Script when data is received (recommended);
* periodically checking for available data using a Schedule.

Receiving data does not block the execution thread.
After the first byte is received, data starts accumulating in the internal input buffer, and a timer for waiting for the end of data reception is started.

The Script for processing input data will be started when at least one of the following conditions is met:
* a terminator is found (if specified);
* the input buffer is completely filled;
* the waiting time (`readTime`) has elapsed.

Before the Script is started to process the input data, the contents of the input buffer are copied. The copy of the data can be retrieved using `Serial.popInputData()`.

Data following the terminator, as well as data that does not fit into the input buffer, is moved to the beginning of the buffer and will be used during the next reception cycle.
If the previous data has not been retrieved using `Serial.popInputData()` by the time the next reception cycle starts, it will be overwritten by new data.

It is recommended to set the input buffer size to at least the maximum expected packet size and to use a terminator whenever the communication protocol allows it.

###### Note

The `Serial` module does not guarantee that a complete packet will be received in a single call to `Serial.popInputData()`.
It accumulates received bytes in the internal buffer and makes them available only after the reception is complete (when a terminator is found, the buffer is filled, or the waiting time expires).

`Serial` works with Java `byte[]` arrays (`userdata`). For convenient work with these arrays, it is recommended to use the built-in `ByteArray` class.



##### Example

```lua
local Serial = require("serial")

-- registering a device;
-- specify the device identifier (id) and connection parameters;
-- returns true if the device was successfully registered;
-- returns false if a device with the specified id is already registered
local flag1 = Serial.registerDeviceIfAbsent("super_device", {
    port = Serial.SEARCH,           -- or a specific port name, for example "ttyUSB0" or "COM1"
    baudRate = 9600,                -- optional; default: 9600
    numDataBits = 8,                -- optional; default: 8
    numStopBits = 1,                -- optional; default: 1
    parity = 0,                     -- optional; default: NO_PARITY; (0-NO_PARITY; 1-ODD_PARITY; 2-EVEN_PARITY; 3-MARK_PARITY; 4-SPACE_PARITY;)
    readTime = 1000,                -- optional; maximum time to wait for the end of data reception (ms); default: 1000
    inputBufferLength = 1024,       -- optional; input buffer size (bytes); default: 1024
    inputTerminator = ByteArray.fromString("\r\n\r\n"):getJBytes(), -- optional; terminator; default: empty Java byte[]
    dataReceivedEventHandler = Args.scriptId,         -- optional; id of the Script for processing input data; default: ""
    searchDeviceEventHandler = Args.scriptId,         -- optional; id of the Script for device discovery; default: ""
    deviceConnectedEventHandler = Args.scriptId,      -- optional; id of the Script for handling the connection event; default: ""
    deviceDisconnectedEventHandler = Args.scriptId,   -- optional; id of the Script for handling the disconnection event; default: ""
    connectionFailedEventHandler = Args.scriptId      -- optional; id of the Script for handling connection failures; default: ""
})


-- unregistering the device;
-- returns true if the device was successfully unregistered
--[[
local flag2 = Serial.unregisterDevice("super_device")
]]


-- sending data to the device;
-- specify:
--   device id (string);
--   data (Java byte[] (userdata));
--   number of bytes to send;
-- returns the number of bytes sent
local data = ByteArray.fromString("hello super device\r\n\r\n")
local length = Serial.writeData(
    "super_device",
    data:getJBytes(),
    data:length()
)


-- receiving input data;
-- specify the device id;
-- returns Java byte[] (userdata);
-- returns nil if there is no data;
-- the array size is always equal to inputBufferLength
local bytes = Serial.popInputData("super_device")
local data2 = ByteArray.fromJBytes(bytes)


-- checking whether the device is connected;
-- returns true if the device is connected
local flag3 = Serial.isDeviceAvailable("super_device")


-- checking whether the device is registered;
-- returns true if the device is registered
local flag4 = Serial.isDeviceRegistered("super_device")


-- clearing the internal data accumulation buffer;
-- does not remove data that is already available through popInputData()
--[[
Serial.resetInputBuffer("super_device")
]]


-- device discovery;

-- to find the required device, implement a search function;

-- if the search is started manually, checking Args.event == Serial.SEARCH_DEVICE is not required.
if Args.event == Serial.SEARCH_DEVICE then

    -- the function must return true if the required device is found
    local searchFunction = function(openPort)

        -- openPort is a temporarily opened serial port;
        -- it is available only while the search function is being executed;
        -- if the function returns false,
        -- the port will be closed automatically.

        -- example of searching by the system port name
        -- return openPort.systemPortName == "ttyUSB0"

        -- example of searching using a custom protocol
        openPort:write(ByteArray.fromString("GET_ID"):getJBytes())

        -- receiving the response;
        -- read(bufferLength, timeout);
        -- inputTerminator is not used during device discovery
        local response = ByteArray.fromJBytes(
            openPort:read(128, 1000)
        ):getString(0)

        return response == "IM_SUPER_DEVICE"
    end

    -- returns true if the device was found and connected
    local flag = Serial.searchDevice("super_device", searchFunction)
end


-- handling the data received event
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


-- handling the device connected event
if Args.event == Serial.DEVICE_CONNECTED then
    if Log.isDebugEnabled() then
        Log.debug(
            "Device Id:",
            Args.deviceId,
            "Connected!"
        )
    end
end


-- handling the device disconnected event
if Args.event == Serial.DEVICE_DISCONNECTED then
    if Log.isDebugEnabled() then
        Log.debug(
            "Device Id:",
            Args.deviceId,
            "Disconnected!"
        )
    end
end


-- handling a connection failure
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


-- returns information about registered devices (table, array)
local infoList = Serial.info()
Output.print(infoList)

--[[ what will be printed
[
    {
        "dataReceivedEventHandler":"396df2c6-7507-4780-9afd-125fb76683cf",
        "connectionTimestamp":1786112452684,
        "port":"SEARCH",
        "baudRate":9600,
        "terminator":"PSk=", -- displayed in Base64 format
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