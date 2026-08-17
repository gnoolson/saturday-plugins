package gnoolson.saturday.plugin.serial;

import gnoolson.saturday_plugin_api.Functionality;

import java.util.List;

public interface Serial extends Functionality {

    boolean registerDeviceIfAbsent(String deviceId, Params params);

    boolean unregisterDevice(String deviceId);

    boolean isDeviceRegistered(String deviceId);

    boolean searchDevice(String deviceId, SearchFunction searchFunction);

    void shutdown();

    int writeData(String deviceId, byte[] buffer, int length);

    byte[] popIncomingData(String deviceId);

    void resetInputBuffer(String deviceId);

    boolean isDeviceAvailable(String deviceId);

    List<Info> getInfo();

}
