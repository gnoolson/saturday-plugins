package gnoolson.saturday.plugin.serial;

import com.fazecast.jSerialComm.SerialPort;
import lombok.Getter;

@Getter
public class Info {

    private final String deviceId;
    private final String port;
    private final String systemPortName;
    private final String portDescription;
    private final int baudRate;
    private final int numDataBits;
    private final int numStopBits;
    private final int parity;
    private final String searchDeviceEventHandler;
    private final String dataReceivedEventHandler;
    private final String deviceConnectedEventHandler;
    private final String deviceDisconnectedEventHandler;
    private final String connectionFailedEventHandler;
    private final int readTime;
    private final byte[] terminator;
    private final int inputBufferLength;
    private final boolean connected;
    private final long connectionTimestamp;

    /*
     *
     *
     * */
    public Info(Device device) {
        SerialPort serialPort = device.getPort();
        if (serialPort != null) {
            this.systemPortName = serialPort.getSystemPortName();
            this.portDescription = serialPort.getPortDescription();
        } else {
            this.systemPortName = "---";
            this.portDescription = "---";
        }

        this.connectionTimestamp = device.getConnectionTimestamp();
        this.deviceId = device.getId();
        this.port = device.getParams().getPort();
        this.baudRate = device.getParams().getBaudRate();
        this.numDataBits = device.getParams().getNumDataBits();
        this.numStopBits = device.getParams().getNumStopBits();
        this.parity = device.getParams().getParity();
        this.searchDeviceEventHandler = device.getParams().getSearchDeviceEventHandler();
        this.dataReceivedEventHandler = device.getParams().getDataReceivedEventHandler();
        this.deviceConnectedEventHandler = device.getParams().getDeviceConnectedEventHandler();
        this.deviceDisconnectedEventHandler = device.getParams().getDeviceDisconnectedEventHandler();
        this.connectionFailedEventHandler = device.getParams().getConnectionFailedEventHandler();
        this.readTime = device.getParams().getReadTime();
        this.terminator = device.getParams().getTerminator();
        this.inputBufferLength = device.getParams().getInputBufferLength();
        this.connected = device.isConnected();
    }

}
