package TCP;

public interface TCPServerObserver {
    void transmitEvent(String id);
    void receiveEvent(String id);
}
