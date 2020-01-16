package TCP;


import com.sun.istack.internal.NotNull;

import java.io.IOException;
import java.util.*;

public class Client implements ServerInterfaceForNetwork, ServerInterfaceForUser {

    private Map<String, LinkedList<String>> transmitBuffer;
    private Map<String, LinkedList<String>> receiveBuffer;
    private List<TCPConnectionInterface> connectionsList = new LinkedList<>();
    //private final ArrayList<TCPConnectionInterface> connectionsList = new ArrayList<>();
    private ArrayList<TCPServerObserver> tcpServerObservers = new ArrayList<TCPServerObserver>();
    private static Client uniqueInstance = null;
    private TCPConnectionInterface connection;
    private String ipAddress = "127.0.0.1";
    private int port = 8189;

    private static String id;
    //private static ScheduledExecutorService executor;// = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {
        new Client();
    }

    public static Client getInstance(String id) {
        Client.id = id;
        if( uniqueInstance==null ) {
            uniqueInstance = new Client();
        }
        return uniqueInstance;
    }

    private Client() {

        transmitBuffer = Collections.synchronizedMap(new HashMap<String, LinkedList<String>>());
        receiveBuffer = Collections.synchronizedMap(new HashMap<String, LinkedList<String>>());

        //Runnable serverTask = () -> {
            startClient();
        //};
        //Thread thread = new Thread(serverTask);
        //thread.start();
    }

    private void startClient() {
        System.out.println("Client running...");
        try {
            connection = new TCPConnectionClient(this, ipAddress, port, id);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("TCPConnection exception: " + e);
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


//    public static String getId() {
//        return id;
//    }
//
//    public static void setId(String id) {
//        this.id = id;
//    }

}

