package gnoolson.saturday.plugin.serial;

import com.fazecast.jSerialComm.SerialPort;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
@Getter
public class OpenPort {

    private final Logger log = LoggerFactory.getLogger(OpenPort.class);

    private final SerialPort serialPort;

    /*
     *
     *
     * */
    public synchronized int write(byte[] data) {
        return serialPort.writeBytes(data, data.length);
    }

    public synchronized byte[] read(int bufferLength, int waitingTime) {
        byte[] buffer = new byte[bufferLength];
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, waitingTime, 0);
        serialPort.readBytes(buffer, bufferLength);
        return buffer;
    }

    public String getSystemPortName() {
        return serialPort.getSystemPortName();
    }

    public String getPortDescription() {
        return serialPort.getPortDescription();
    }

}
