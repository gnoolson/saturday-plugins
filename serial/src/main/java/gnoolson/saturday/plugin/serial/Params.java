package gnoolson.saturday.plugin.serial;

import com.fazecast.jSerialComm.SerialPort;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Params {

    private String port;

    private int baudRate = 9600;
    private int numDataBits = 8;
    private int numStopBits = SerialPort.ONE_STOP_BIT;
    private int parity = SerialPort.NO_PARITY;

    private String searchDeviceEventHandler = "";
    private String dataReceivedEventHandler = "";
    private String deviceConnectedEventHandler = "";
    private String deviceDisconnectedEventHandler = "";
    private String connectionFailedEventHandler = "";

    private int readTime = 1000;
    private byte[] terminator = new byte[0];
    private int inputBufferLength = 1024;

}
