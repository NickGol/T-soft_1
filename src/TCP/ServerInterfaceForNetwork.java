package TCP;

public interface ServerInterfaceForNetwork {

    void onConnectionReady(TCPConnectionInterface tcpConnection);
    void onReceiveString(TCPConnectionInterface tcpConnection, String value);
    void onSendString(TCPConnectionInterface tcpConnection);
    void onDisconnect(TCPConnectionInterface tcpConnection);
    void onException(TCPConnectionInterface tcpConnection, Exception e);
    String readTransmitBuffer(TCPConnectionInterface tcpConnection);
    Boolean writeReceiveBuffer(TCPConnectionInterface tcpConnection, String data);
}