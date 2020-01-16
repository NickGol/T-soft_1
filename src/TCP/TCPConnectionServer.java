package TCP;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnectionServer implements TCPConnectionInterface {

    private final Socket socket;
    private final Thread connectionThread;
    private final ServerInterfaceForNetwork connection;
    private final BufferedReader in;
    private final BufferedWriter out;
    private String id;

    public TCPConnectionServer(ServerInterfaceForNetwork connection, String ipAddr, int port) throws IOException {
        this(connection, new Socket(ipAddr, port));
    }

    public TCPConnectionServer(ServerInterfaceForNetwork serverObject, Socket socket) throws IOException {
        this.connection = serverObject;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        connectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverObject.onConnectionReady(TCPConnectionServer.this);
                    waitHelloMessage();
                    while(!connectionThread.isInterrupted()) {
                        //String str = serverObject.readTransmitBuffer(TCPConnectionServer.this);
                        //System.out.println(str);
                        sendString(serverObject.readTransmitBuffer(TCPConnectionServer.this));
                        serverObject.onSendString(TCPConnectionServer.this);

                        String msg = readMessage();
                        serverObject.onReceiveString(TCPConnectionServer.this, msg);
                        Thread.sleep(5000);
                    }
                } catch (IOException | InterruptedException e) {
                    serverObject.onException(TCPConnectionServer.this, e);
                } finally {
                    serverObject.onDisconnect(TCPConnectionServer.this);
                }
            }
        });
        connectionThread.start();
    }

    private synchronized void sendString(String value) {
        try {
            //value = value + " end of message ";
            //System.out.println(value);
            out.write(value + "\n end of message \n");
            out.flush();
        } catch (IOException e) {
            connection.onException(TCPConnectionServer.this, e);
            disconnect();
        }
    }

    private synchronized void disconnect() {
        connectionThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            connection.onException(TCPConnectionServer.this, e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }

    public String getId() {
        return id;
    }

    private void waitHelloMessage() throws IOException {
        String msg = "";
        while( !msg.startsWith( "Hello. ID = ") ) {
            msg = readMessage();
            //msg = in.readLine();
        }
        //if( msg.startsWith( "Hello. ID = ") ) {
            id = msg.substring("Hello. ID = ".length());
        //}
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
