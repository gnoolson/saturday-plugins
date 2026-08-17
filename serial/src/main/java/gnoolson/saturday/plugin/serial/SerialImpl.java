package gnoolson.saturday.plugin.serial;

import com.fazecast.jSerialComm.SerialPort;
import gnoolson.locker.Locker;
import gnoolson.locker.OptimisticLocalLocker;
import gnoolson.saturday_plugin_api.Output;
import gnoolson.saturday_plugin_api.ScriptLauncherGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static gnoolson.saturday.plugin.serial.Consts.*;

public class SerialImpl implements Serial, EventLauncher {

    public static final int DELAY = 5_000;
    public static final String EVENT = "event";
    public static final String DEVICE_ID = "deviceId";
    public static final String REASON = "reason";
    private static final Logger log = LoggerFactory.getLogger(SerialImpl.class);
    private final Map<String, Device> devices = new ConcurrentHashMap<>();
    private final Timer checkConnectionTimer = new Timer();
    private final ScriptLauncherGateway scriptLauncherGateway;
    private final ExecutorService scriptLauncherExecutorService = Executors.newCachedThreadPool();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Locker locker = new OptimisticLocalLocker(0, 10, 5000);

    /*
     *
     *
     * */
    public SerialImpl(ScriptLauncherGateway scriptLauncherGateway) {
        this.scriptLauncherGateway = scriptLauncherGateway;
        this.checkConnectionTimer.schedule(new ConnectDeviceTimerTask(), DELAY);
    }

    @Override
    public boolean registerDeviceIfAbsent(String deviceId, Params params) {
        try (Locker.LockHandle ignore = locker.lockIds(deviceId)) {
            Device device = new Device(deviceId, params);
            return devices.putIfAbsent(deviceId, device) == null;
        }
    }

    @Override
    public boolean searchDevice(String deviceId, SearchFunction searchFunction) {
        try (Locker.LockHandle ignore = locker.lockIds(this.toString(), deviceId)) {
            Device device = devices.get(deviceId);

            if (device == null || device.isConnected())
                return false;

            SerialPort[] ports = SerialPort.getCommPorts();

            for (SerialPort port : ports) {
                if (port.isOpen())
                    continue;

                port.setBaudRate(device.getParams().getBaudRate());
                port.setParity(device.getParams().getParity());
                port.setNumStopBits(device.getParams().getNumStopBits());
                port.setNumDataBits(device.getParams().getNumDataBits());

                if (!port.openPort())
                    continue;

                if (!searchFunction.execute(new OpenPort(port))) {
                    port.closePort();
                    continue;
                }

                port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);
                DataEventListener dataEventListener = new DataEventListener(device, this, scheduler);
                port.addDataListener(dataEventListener);
                device.setPort(port, dataEventListener::resetBuffer);

                deviceConnectedEvent(device);

                if (log.isDebugEnabled())
                    log.debug("{}. Port found: {}", deviceId, port.getSystemPortName());

                return true;
            }
            connectionFailedEvent(device, "DEVICE_NOT_FOUND");
        } catch (Exception e) {
            log.warn("Exception", e);
        }
        return false;
    }


    @Override
    public void shutdown() {
        try (Locker.LockHandle ignore = locker.lockGlobal()) {
            for (Map.Entry<String, Device> entry : devices.entrySet()) {
                Device device = entry.getValue();
                if (device.isConnected())
                    device.disconnect();
            }
        }
        devices.clear();

        scriptLauncherExecutorService.shutdown();
        scheduler.shutdown();
        checkConnectionTimer.cancel();
    }

    @Override
    public int writeData(String deviceId, byte[] buffer, int length) {
        try (Locker.LockHandle ignore = locker.lockIds(deviceId)) {
            Device device = devices.get(deviceId);
            if (device == null)
                throw new IllegalStateException(String.format("Device \"%s\" was not found", deviceId));

            return device.writeData(buffer, length);
        }
    }

    @Override
    public byte[] popIncomingData(String deviceId) {
        try (Locker.LockHandle ignore = locker.lockIds(deviceId)) {
            Device device = devices.get(deviceId);
            if (device == null)
                throw new IllegalStateException(String.format("Device \"%s\" was not found", deviceId));

            return device.popInputData();
        }
    }

    @Override
    public void resetInputBuffer(String deviceId) {
        try (Locker.LockHandle ignore = locker.lockIds(deviceId)) {
            Device device = devices.get(deviceId);
            if (device == null)
                throw new IllegalStateException(String.format("Device \"%s\" was not found", deviceId));

            device.resetInputBuffer();
        }
    }

    @Override
    public boolean isDeviceAvailable(String deviceId) {
        try (Locker.LockHandle ignore = locker.lockIds(deviceId)) {
            Device device = devices.get(deviceId);
            if (device == null)
                return false;

            return device.isConnected();
        }
    }

    @Override
    public List<Info> getInfo() {
        List<Info> result = new ArrayList<>(8);

        for (Map.Entry<String, Device> entry : devices.entrySet()) {
            Device device = entry.getValue();
            result.add(new Info(device));
        }

        return result;
    }

    @Override
    public boolean unregisterDevice(String deviceId) {
        try (Locker.LockHandle ignore = locker.lockIds(deviceId)) {
            Device device = devices.remove(deviceId);
            if (device == null)
                return false;

            if (device.isConnected())
                device.disconnect();

            return true;
        }
    }

    @Override
    public boolean isDeviceRegistered(String deviceId) {
        return devices.containsKey(deviceId);
    }

    @Override
    public void writeResultToOutput(Output output) {

    }

    @Override
    public void deviceConnectedEvent(Device device) {
        device.setConnectionTimestamp(System.currentTimeMillis());

        String deviceConnectedEventHandler = device.getParams().getDeviceConnectedEventHandler();
        if (deviceConnectedEventHandler.isEmpty())
            return;

        Map<String, Object> args = new HashMap<>();
        args.put(EVENT, DEVICE_CONNECTED);
        args.put(DEVICE_ID, device.getId());

        scriptLauncherExecutorService.execute(() -> {
            scriptLauncherGateway.execute(deviceConnectedEventHandler, args);
        });
    }

    @Override
    public void deviceDisconnectedEvent(Device device) {
        device.setConnectionTimestamp(0);

        String deviceDisconnectedEventHandler = device.getParams().getDeviceConnectedEventHandler();
        if (deviceDisconnectedEventHandler.isEmpty())
            return;

        Map<String, Object> args = new HashMap<>();
        args.put(EVENT, DEVICE_DISCONNECTED);
        args.put(DEVICE_ID, device.getId());

        scriptLauncherExecutorService.execute(() -> {
            scriptLauncherGateway.execute(deviceDisconnectedEventHandler, args);
        });
    }

    @Override
    public void connectionFailedEvent(Device device, String reason) {
        String deviceNotFoundEventHandler = device.getParams().getConnectionFailedEventHandler();
        if (deviceNotFoundEventHandler.isEmpty())
            return;

        Map<String, Object> args = new HashMap<>();
        args.put(EVENT, CONNECTION_FAILED);
        args.put(DEVICE_ID, device.getId());
        args.put(REASON, reason);

        scriptLauncherExecutorService.execute(() -> {
            scriptLauncherGateway.execute(deviceNotFoundEventHandler, args);
        });
    }

    @Override
    public void dataReceivedEvent(Device device, String reason) {
        String receivedEventHandler = device.getParams().getDataReceivedEventHandler();
        if (receivedEventHandler.isEmpty())
            return;

        Map<String, Object> args = new HashMap<>();
        args.put(EVENT, DATA_RECEIVED);
        args.put(DEVICE_ID, device.getId());
        args.put(REASON, reason);

        scriptLauncherExecutorService.execute(() -> {
            scriptLauncherGateway.execute(receivedEventHandler, args);
        });
    }

    @Override
    public void searchEvent(Device device) {
        String searchDeviceEventHandler = device.getParams().getSearchDeviceEventHandler();
        if (searchDeviceEventHandler.isEmpty())
            return;

        Map<String, Object> args = new HashMap<>();
        args.put(EVENT, SEARCH_DEVICE);
        args.put(DEVICE_ID, device.getId());

        // wait
        scriptLauncherGateway.execute(searchDeviceEventHandler, args);
    }

    /*
     *
     *
     * */
    private boolean connect(Device device) {
        try (Locker.LockHandle ignore = locker.lockIds(this.toString(), device.getId())) {
            SerialPort commPort;
            try {
                commPort = SerialPort.getCommPort(device.getParams().getPort());
            } catch (Exception e) {
                if (log.isDebugEnabled())
                    log.debug("GetCommPort exception", e);
                connectionFailedEvent(device, "PORT_UNAVAILABLE");
                return false;
            }

            commPort.setBaudRate(device.getParams().getBaudRate());
            commPort.setParity(device.getParams().getParity());
            commPort.setNumStopBits(device.getParams().getNumStopBits());
            commPort.setNumDataBits(device.getParams().getNumDataBits());

            if (!commPort.openPort()) {
                connectionFailedEvent(device, "PORT_IS_ALREADY_OPEN");
                return false;
            }

            commPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);

            DataEventListener dataEventListener = new DataEventListener(device, this, scheduler);
            commPort.addDataListener(dataEventListener);
            device.setPort(commPort, dataEventListener::resetBuffer);

            return true;
        }
    }

    /*
     *
     *
     * */
    private class ConnectDeviceTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                for (Map.Entry<String, Device> entry : devices.entrySet()) {
                    Device device = entry.getValue();

                    if (device.isConnected())
                        continue;

                    if (device.getParams().getPort().equals(Consts.SEARCH)) {
                        searchEvent(device);
                    } else {
                        if (connect(device))
                            deviceConnectedEvent(device);
                    }
                }
            } catch (Exception e) {
                log.warn("Exception", e);
            }

            checkConnectionTimer.schedule(new ConnectDeviceTimerTask(), DELAY);
        }
    }

}
