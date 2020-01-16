package TCP;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TCPConnectionClient implements TCPConnectionInterface {

    private final Socket socket;
    private final Thread connectionThread;
    private final ServerInterfaceForNetwork connection;
    private final BufferedReader in;
    private final BufferedWriter out;
    private String id;

    public TCPConnectionClient(ServerInterfaceForNetwork connection, String ipAddr, int port) throws IOException {
        this(connection, new Socket(ipAddr, port), "1");
    }

    public TCPConnectionClient(ServerInterfaceForNetwork connection, String ipAddr, int port, String id) throws IOException {
        this(connection, new Socket(ipAddr, port), id);


    }

    public TCPConnectionClient(ServerInterfaceForNetwork clientObject, Socket socket, String id) throws IOException {
        this.id = id;
        this.connection = clientObject;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        connectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    clientObject.onConnectionReady(TCPConnectionClient.this);
                    sendHelloMessage();
                    while(!connectionThread.isInterrupted()) {

                        String msg = readMessage();
                        clientObject.onReceiveString(TCPConnectionClient.this, msg);

                        sendString(clientObject.readTransmitBuffer(TCPConnectionClient.this));
                        clientObject.onSendString(TCPConnectionClient.this);
                    }
                } catch (IOException e) {
                    clientObject.onException(TCPConnectionClient.this, e);
                } finally {
                    clientObject.onDisconnect(TCPConnectionClient.this);
                }
            }
        });
        connectionThread.start();
    }

    private synchronized void sendString(String value) {
        try {
            out.write(value + "\n end of message \n");
            out.flush();
        } catch (IOException e) {
            connection.onException(TCPConnectionClient.this, e);
            disconnect();
        }
    }

    private synchronized void disconnect() {
        connectionThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            connection.onException(TCPConnectionClient.this, e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private void sendHelloMessage() throws IOException {
        try {
            out.write("Hello. ID = " + id + "\n end of message \n");
            out.flush();
        } catch (IOException e) {
            connection.onException(TCPConnectionClient.this, e);
            disconnect();
        }
    }

    private String readMessage() throws IOException {

        String line = in.readLine();
        StringBuilder msg = new StringBuilder();
        while ( !line.equals( " end of message ") ) {
            msg.append(line + "\n");
            line = in.readLine();
        }
        //System.out.println(msg.deleteCharAt(msg.length()-1).toString());
        return msg.deleteCharAt(msg.length()-1).toString();
    }
}
