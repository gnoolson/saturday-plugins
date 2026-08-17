package gnoolson.saturday.plugin.serial;

public interface EventLauncher {

    void deviceConnectedEvent(Device device);

    void deviceDisconnectedEvent(Device device);

    void connectionFailedEvent(Device device, String reason);

    void dataReceivedEvent(Device device, String reason);

    void searchEvent(Device device);

}
