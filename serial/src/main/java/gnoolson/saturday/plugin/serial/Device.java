package gnoolson.saturday.plugin.serial;

import com.fazecast.jSerialComm.SerialPort;
import lombok.Getter;
import lombok.Setter;

public class Device {

    @Getter
    private final String id;
    @Getter
    private final Params params;
    @Setter
    private byte[] inputData;
    @Getter
    private SerialPort port;
    private ResetInputBufferGateWay resetInputBufferGateWay;
    @Getter
    @Setter
    private long connectionTimestamp;

    /*
     *
     *
     * */
    public Device(String id, Params params) {
        this.id = id;
        this.params = params;
    }

    public boolean isConnected() {
        if (port == null)
            return false;
        return port.isOpen();
    }

    public boolean disconnect() {
        if (port == null)
            return false;

        port.removeDataListener();

        return port.closePort();
    }

    public int writeData(byte[] buffer, int length) {
        if (port.isOpen())
            return port.writeBytes(buffer, length);

        return 0;
    }

    public byte[] popInputData() {
        if (inputData == null)
            return new byte[0];

        byte[] result = inputData;
        inputData = null;
        return result;
    }

    public void setPort(SerialPort port, ResetInputBufferGateWay resetInputBufferGateWay) {
        if (this.port != null)
            this.port.closePort();

        this.resetInputBufferGateWay = resetInputBufferGateWay;
        this.port = port;
    }

    public void resetInputBuffer() {
        if (resetInputBufferGateWay == null)
            return;

        resetInputBufferGateWay.execute();
    }

}
