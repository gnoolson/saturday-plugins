package gnoolson.saturday.plugin.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DataEventListener implements SerialPortDataListener {

    private static final Logger log = LoggerFactory.getLogger(DataEventListener.class);

    private final Device device;
    private final byte[] inputBuffer;
    private final EventLauncher eventLauncher;
    private final ScheduledExecutorService scheduler;
    private int inputBufferIndex;
    private ScheduledFuture<?> timeoutTask = null;

    /*
     *
     *
     * */
    public DataEventListener(Device device, EventLauncher eventLauncher, ScheduledExecutorService scheduler) {
        this.device = device;
        this.eventLauncher = eventLauncher;
        this.scheduler = scheduler;
        this.inputBuffer = new byte[device.getParams().getInputBufferLength()];
    }

    /*
     *
     *
     * */
    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_PORT_DISCONNECTED | SerialPort.LISTENING_EVENT_DATA_RECEIVED;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.getEventType() == SerialPort.LISTENING_EVENT_PORT_DISCONNECTED) {
            device.disconnect();
            eventLauncher.deviceDisconnectedEvent(device);
        } else if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_RECEIVED) {
            dataReceivedEvent(event);
        }
    }

    public synchronized void resetBuffer() {
        Arrays.fill(inputBuffer, 0, inputBufferIndex, (byte) 0);
        inputBufferIndex = 0;
    }

    // only for test
    public byte[] getInputBuffer() {
        return Arrays.copyOf(inputBuffer, inputBuffer.length);
    }

    /*
     *
     *
     * */
    private void dataReceivedEvent(SerialPortEvent event) {
        byte[] chunk = event.getReceivedData();
        handleData(chunk);
    }

    private synchronized void handleData(byte[] chunk) {
        if (chunk == null || chunk.length == 0)
            return;

        if (timeoutTask != null && !timeoutTask.isDone())
            timeoutTask.cancel(false);

        byte[] terminator = device.getParams().getTerminator();
        boolean searchTerminator = terminator.length > 0;

        boolean terminatorFound = false;    // H-PRIORITY
        boolean bufferIsFull = false;       // L-PRIORITY

        int length;
        if ((chunk.length + inputBufferIndex) > inputBuffer.length) {
            length = inputBuffer.length - inputBufferIndex;
        } else {
            length = chunk.length;
        }

        int usedCunkIndex;
        for (usedCunkIndex = 0; usedCunkIndex < length; usedCunkIndex++) {
            inputBuffer[inputBufferIndex] = chunk[usedCunkIndex];
            inputBufferIndex++;

            if (searchTerminator && isTerminatorFound(terminator)) {
                terminatorFound = true;
                usedCunkIndex++;
                break;
            }
        }

        if (inputBufferIndex >= inputBuffer.length)
            bufferIsFull = true;

        if (terminatorFound) {
            if (log.isDebugEnabled())
                log.debug("Device {}. Terminator found", device.getId());

            dataReceived("TERMINATOR_FOUND");
            handleChunkRemainder(usedCunkIndex, chunk);
        } else if (bufferIsFull) {
            if (log.isDebugEnabled())
                log.debug("Device {}. Input Buffer is Full", device.getId());


            dataReceived("BUFFER_IS_FULL");
            handleChunkRemainder(usedCunkIndex, chunk);
        } else {
            timeoutTask = scheduler.schedule(
                    () -> {
                        synchronized (DataEventListener.this) {
                            if (inputBufferIndex == 0)
                                return;

                            if (log.isDebugEnabled())
                                log.debug("Device {}. Read Timeout", device.getId());

                            dataReceived("TIMEOUT");
                        }
                    },
                    device.getParams().getReadTime(),
                    TimeUnit.MILLISECONDS
            );
        }
    }

    private synchronized void handleChunkRemainder(int chunkIndex, byte[] chunk) {
        if (chunkIndex < chunk.length) {
            handleData(Arrays.copyOfRange(chunk, chunkIndex, chunk.length));
        }
    }

    private boolean isTerminatorFound(byte[] terminator) {
        if (inputBufferIndex < terminator.length)
            return false;


        int bufferOffset = inputBufferIndex - terminator.length;

        for (int i = 0; i < terminator.length; i++) {
            if (inputBuffer[bufferOffset + i] != terminator[i])
                return false;

        }
        return true;
    }

    private void dataReceived(String reason) {
        byte[] copy = Arrays.copyOfRange(inputBuffer, 0, inputBufferIndex);

        device.setInputData(copy);
        resetBuffer();

        eventLauncher.dataReceivedEvent(device, reason);
    }

}