package TCP;

import com.sun.istack.internal.NotNull;

public interface ServerInterfaceForUser {

    Boolean writeTransmitBuffer(String id, String data);
    String readReceiveBuffer(String id);
    void registerObserver(@NotNull TCPServerObserver observer);
    void removeObserver(@NotNull TCPServerObserver observer);
}
