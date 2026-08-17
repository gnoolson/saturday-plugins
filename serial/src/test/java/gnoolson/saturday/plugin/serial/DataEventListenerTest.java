package gnoolson.saturday.plugin.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

class DataEventListenerTest {

    @Test
    public void buffer_is_full() {
        Params params = new Params();
        params.setInputBufferLength(10);

        AtomicReference<byte[]> dataAR = new AtomicReference<>();
        AtomicReference<String> reasonAR = new AtomicReference<>();

        DataEventListener dataEventListener = new DataEventListener(new Device("super_device", params), new EventLauncher() {
            @Override
            public void deviceConnectedEvent(Device device) {
            }

            @Override
            public void deviceDisconnectedEvent(Device device) {
            }

            @Override
            public void connectionFailedEvent(Device device, String reason) {
            }

            @Override
            public void dataReceivedEvent(Device device, String reason) {
                dataAR.set(device.popInputData());
                reasonAR.set(reason);
            }

            @Override
            public void searchEvent(Device device) {
            }
        }, Executors.newSingleThreadScheduledExecutor());

        {
            SerialPortEvent serialPortEvent = Mockito.mock(SerialPortEvent.class);
            byte[] bytes = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};
            Mockito.when(serialPortEvent.getReceivedData()).thenReturn(bytes);
            Mockito.when(serialPortEvent.getEventType()).thenReturn(SerialPort.LISTENING_EVENT_DATA_RECEIVED);

            Assertions.assertNull(reasonAR.get());
            Assertions.assertNull(dataAR.get());

            dataEventListener.serialEvent(serialPortEvent);
        }

        {
            SerialPortEvent serialPortEvent = Mockito.mock(SerialPortEvent.class);
            byte[] bytes = {0x08, 0x09, 0x0A, 0x0B};
            Mockito.when(serialPortEvent.getReceivedData()).thenReturn(bytes);
            Mockito.when(serialPortEvent.getEventType()).thenReturn(SerialPort.LISTENING_EVENT_DATA_RECEIVED);

            dataEventListener.serialEvent(serialPortEvent);

            String reason = reasonAR.get();
            Assertions.assertEquals("BUFFER_IS_FULL", reason);
            reasonAR.set(null);

            byte[] actual = dataAR.get();
            dataAR.set(null);

//            byte[] inputBuffer = dataEventListener.getInputBuffer();


            Assertions.assertEquals(10, actual.length);

            Assertions.assertEquals(1, actual[0]);
            Assertions.assertEquals(2, actual[1]);
            Assertions.assertEquals(3, actual[2]);
            Assertions.assertEquals(4, actual[3]);
            Assertions.assertEquals(5, actual[4]);
            Assertions.assertEquals(6, actual[5]);
            Assertions.assertEquals(7, actual[6]);
            Assertions.assertEquals(8, actual[7]);
            Assertions.assertEquals(9, actual[8]);
            Assertions.assertEquals(10, actual[9]);
        }

        dataEventListener.resetBuffer();
        dataAR.set(null);
        reasonAR.set(null);

        {
            SerialPortEvent serialPortEvent = Mockito.mock(SerialPortEvent.class);
            byte[] bytes = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C};
            Mockito.when(serialPortEvent.getReceivedData()).thenReturn(bytes);
            Mockito.when(serialPortEvent.getEventType()).thenReturn(SerialPort.LISTENING_EVENT_DATA_RECEIVED);

            dataEventListener.serialEvent(serialPortEvent);

            byte[] inputBuffer = dataEventListener.getInputBuffer();

            Assertions.assertEquals(0x0B, inputBuffer[0]); // <<
            Assertions.assertEquals(0x0C, inputBuffer[1]); //<

            Assertions.assertEquals(0x00, inputBuffer[2]);
            Assertions.assertEquals(0x00, inputBuffer[3]);
            Assertions.assertEquals(0x00, inputBuffer[4]);
            Assertions.assertEquals(0x00, inputBuffer[5]);
            Assertions.assertEquals(0x00, inputBuffer[6]);
            Assertions.assertEquals(0x00, inputBuffer[7]);
            Assertions.assertEquals(0x00, inputBuffer[8]);
            Assertions.assertEquals(0x00, inputBuffer[9]);
        }

        dataEventListener.resetBuffer();
        dataAR.set(null);
        reasonAR.set(null);

        {
            SerialPortEvent serialPortEvent = Mockito.mock(SerialPortEvent.class);
            byte[] bytes = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A};
            Mockito.when(serialPortEvent.getReceivedData()).thenReturn(bytes);
            Mockito.when(serialPortEvent.getEventType()).thenReturn(SerialPort.LISTENING_EVENT_DATA_RECEIVED);

            dataEventListener.serialEvent(serialPortEvent);

            String reason = reasonAR.get();
            Assertions.assertEquals("BUFFER_IS_FULL", reason);

            byte[] actual = dataAR.get();

            for (int i = 0; i < actual.length; i++) {
                Assertions.assertEquals(i + 1, actual[i]);
            }
        }

    }

    @Test
    public void terminator_found() {
        Params params = new Params();
        params.setInputBufferLength(10);
        params.setTerminator(new byte[]{(byte) 0xFF, (byte) 0xF0});

        AtomicReference<String> reference = new AtomicReference<>();
        DataEventListener dataEventListener = new DataEventListener(new Device("super_device", params), new EventLauncher() {
            @Override
            public void deviceConnectedEvent(Device device) {

            }

            @Override
            public void deviceDisconnectedEvent(Device device) {

            }

            @Override
            public void connectionFailedEvent(Device device, String reason) {

            }

            @Override
            public void dataReceivedEvent(Device device, String reason) {
                reference.set(reason);
            }

            @Override
            public void searchEvent(Device device) {

            }
        }, Executors.newSingleThreadScheduledExecutor());

        {
            SerialPortEvent serialPortEvent = Mockito.mock(SerialPortEvent.class);
            byte[] bytes = {0x01, 0x02, 0x03};
            Mockito.when(serialPortEvent.getReceivedData()).thenReturn(bytes);
            Mockito.when(serialPortEvent.getEventType()).thenReturn(SerialPort.LISTENING_EVENT_DATA_RECEIVED);

            dataEventListener.serialEvent(serialPortEvent);
        }

        {
            SerialPortEvent serialPortEvent = Mockito.mock(SerialPortEvent.class);
            byte[] bytes = {0x04, (byte) 0xFF, (byte) 0xF0};
            Mockito.when(serialPortEvent.getReceivedData()).thenReturn(bytes);
            Mockito.when(serialPortEvent.getEventType()).thenReturn(SerialPort.LISTENING_EVENT_DATA_RECEIVED);

            dataEventListener.serialEvent(serialPortEvent);
        }

        Assertions.assertEquals("TERMINATOR_FOUND", reference.get());
        reference.set(null);

        dataEventListener.resetBuffer();
        {
            SerialPortEvent serialPortEvent = Mockito.mock(SerialPortEvent.class);
            byte[] bytes = {0x01, 0x02, 0x03};
            Mockito.when(serialPortEvent.getReceivedData()).thenReturn(bytes);
            Mockito.when(serialPortEvent.getEventType()).thenReturn(SerialPort.LISTENING_EVENT_DATA_RECEIVED);

            dataEventListener.serialEvent(serialPortEvent);
        }

        {
            SerialPortEvent serialPortEvent = Mockito.mock(SerialPortEvent.class);
            byte[] bytes = {0x04, (byte) 0xFF, (byte) 0xF0, 0x00, 0x01};
            Mockito.when(serialPortEvent.getReceivedData()).thenReturn(bytes);
            Mockito.when(serialPortEvent.getEventType()).thenReturn(SerialPort.LISTENING_EVENT_DATA_RECEIVED);

            dataEventListener.serialEvent(serialPortEvent);
        }

        Assertions.assertEquals("TERMINATOR_FOUND", reference.get());


    }

    @Test
    public void handle_remainer() {
        {
            Params params = new Params();
            params.setInputBufferLength(10);

            DataEventListener dataEventListener = new DataEventListener(new Device("super_device", params), new EventLauncher() {
                @Override
                public void deviceConnectedEvent(Device device) {
                }

                @Override
                public void deviceDisconnectedEvent(Device device) {
                }

                @Override
                public void connectionFailedEvent(Device device, String reason) {

                }

                @Override
                public void dataReceivedEvent(Device device, String reason) {
                }

                @Override
                public void searchEvent(Device device) {
                }
            }, Executors.newSingleThreadScheduledExecutor());


            SerialPortEvent serialPortEvent = Mockito.mock(SerialPortEvent.class);
            byte[] bytes = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C};
            Mockito.when(serialPortEvent.getReceivedData()).thenReturn(bytes);
            Mockito.when(serialPortEvent.getEventType()).thenReturn(SerialPort.LISTENING_EVENT_DATA_RECEIVED);


            dataEventListener.serialEvent(serialPortEvent);

            byte[] inputBuffer = dataEventListener.getInputBuffer();

            Assertions.assertEquals(0x0B, inputBuffer[0]);
            Assertions.assertEquals(0x0C, inputBuffer[1]);
            Assertions.assertEquals(0x00, inputBuffer[2]);
            Assertions.assertEquals(0x00, inputBuffer[3]);
            Assertions.assertEquals(0x00, inputBuffer[4]);
            Assertions.assertEquals(0x00, inputBuffer[5]);
            Assertions.assertEquals(0x00, inputBuffer[6]);
            Assertions.assertEquals(0x00, inputBuffer[7]);
            Assertions.assertEquals(0x00, inputBuffer[8]);
            Assertions.assertEquals(0x00, inputBuffer[9]);
        }


        {
            Params params = new Params();
            params.setInputBufferLength(10);
            params.setTerminator(new byte[]{(byte) 0xF0, (byte) 0xF1});

            DataEventListener dataEventListener = new DataEventListener(new Device("super_device", params), new EventLauncher() {
                @Override
                public void deviceConnectedEvent(Device device) {
                }

                @Override
                public void deviceDisconnectedEvent(Device device) {
                }

                @Override
                public void connectionFailedEvent(Device device, String reason) {

                }

                @Override
                public void dataReceivedEvent(Device device, String reason) {
                }

                @Override
                public void searchEvent(Device device) {
                }
            }, Executors.newSingleThreadScheduledExecutor());


            SerialPortEvent serialPortEvent = Mockito.mock(SerialPortEvent.class);
            byte[] bytes = {0x01, 0x02, 0x03, 0x04, 0x05, (byte) 0xF0, (byte) 0xF1, 0x08, 0x09, 0x0A, 0x0B, 0x0C};
            Mockito.when(serialPortEvent.getReceivedData()).thenReturn(bytes);
            Mockito.when(serialPortEvent.getEventType()).thenReturn(SerialPort.LISTENING_EVENT_DATA_RECEIVED);


            dataEventListener.serialEvent(serialPortEvent);

            byte[] inputBuffer = dataEventListener.getInputBuffer();

            Assertions.assertEquals(0x08, inputBuffer[0]);
            Assertions.assertEquals(0x09, inputBuffer[1]);
            Assertions.assertEquals(0x0A, inputBuffer[2]);
            Assertions.assertEquals(0x0B, inputBuffer[3]);
            Assertions.assertEquals(0x0C, inputBuffer[4]);
            Assertions.assertEquals(0x00, inputBuffer[5]);
            Assertions.assertEquals(0x00, inputBuffer[6]);
            Assertions.assertEquals(0x00, inputBuffer[7]);
            Assertions.assertEquals(0x00, inputBuffer[8]);
            Assertions.assertEquals(0x00, inputBuffer[9]);
        }

    }

}