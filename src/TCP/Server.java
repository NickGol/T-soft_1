package TCP;


import com.sun.istack.internal.NotNull;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

public class Server implements ServerInterfaceForNetwork, ServerInterfaceForUser {

    private Map<String, LinkedList<String>> transmitBuffer;
    private Map<String, LinkedList<String>> receiveBuffer;
    private List<TCPConnectionInterface> connectionsList = new LinkedList<>();
    //private final ArrayList<TCPConnectionInterface> connectionsList = new ArrayList<>();
    private ArrayList<TCPServerObserver> tcpServerObservers = new ArrayList<TCPServerObserver>();
    private static Server uniqueInstance = null;
    //private static ScheduledExecutorService executor;// = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {
        new Server();
    }

    public static Server getInstance() {
        if( uniqueInstance==null ) {
            uniqueInstance = new Server();
        }
        return uniqueInstance;
    }

    private Server() {

        transmitBuffer = Collections.synchronizedMap(new HashMap<String, LinkedList<String>>());
        receiveBuffer = Collections.synchronizedMap(new HashMap<String, LinkedList<String>>());

        Runnable serverTask = () -> {
            startServer();
        };
        Thread thread = new Thread(serverTask);
        thread.start();
    }

    private void startServer() {
        System.out.println("Server running...");
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            while(true) {
                try {
                    TCPConnectionInterface connection = new TCPConnectionServer(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println("TCPConnection exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnectionInterface tcpConnection) {
        connectionsList.add(tcpConnection);
    }

    @Override
    public synchronized void onSendString(TCPConnectionInterface tcpConnection) {
        notifyTCPServerObserversTransmit(tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnectionInterface tcpConnection, String value) {
        writeReceiveBuffer(tcpConnection, value);
        notifyTCPServerObserversReceive(tcpConnection);

        //sendToAllConnections(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnectionInterface tcpConnection) {
        connectionsList.remove(tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnectionInterface tcpConnection, Exception e) {
        System.out.println("TCPConnection exception: " + e);
    }

    @Override
    public synchronized Boolean writeTransmitBuffer(String id, String data) {
        synchronized (transmitBuffer) {
            if( transmitBuffer.get(id)==null ) {
                transmitBuffer.put( id, new LinkedList<String>() );
            }
            return transmitBuffer.get(id).add(data);
        }
    }

    @Override
    public synchronized String readTransmitBuffer(TCPConnectionInterface tcpConnection) {
        synchronized (transmitBuffer) {
            return transmitBuffer.get(tcpConnection.getId()).removeFirst();
        }
    }

    @Override
    public synchronized Boolean writeReceiveBuffer(TCPConnectionInterface tcpConnection, String data) {
        synchronized (receiveBuffer) {
            if( receiveBuffer.get(tcpConnection.getId())==null ) {
                receiveBuffer.put( tcpConnection.getId(), new LinkedList<String>() );
            }
            return receiveBuffer.get(tcpConnection.getId()).add(data);
        }
    }

    @Override
    public synchronized String readReceiveBuffer(String id) {
        synchronized (receiveBuffer) {
            return receiveBuffer.get(id).removeFirst();
        }
    }

    @Override
    public synchronized void registerObserver(@NotNull TCPServerObserver observer) {
        tcpServerObservers.add(observer);
    }

    @Override
    public synchronized void removeObserver(@NotNull TCPServerObserver observer) {
        int i = tcpServerObservers.indexOf(observer);
        if( i>=0 ) {
            tcpServerObservers.remove(observer);
        }
    }

    private void notifyTCPServerObserversReceive(TCPConnectionInterface tcpConnection) {
        for( TCPServerObserver observer : tcpServerObservers) {
            observer.receiveEvent(tcpConnection.getId());
        }
    }

    private void notifyTCPServerObserversTransmit(TCPConnectionInterface tcpConnection) {
        for( TCPServerObserver observer : tcpServerObservers) {
            observer.transmitEvent(tcpConnection.getId());
        }
    }

}

