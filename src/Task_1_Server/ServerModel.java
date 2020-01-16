package Task_1_Server;

import DataObjects.DataItem;
import DataObjects.ParametersToSend;
import TCP.Server;
import TCP.ServerInterfaceForUser;
import TCP.TCPServerObserver;
import com.sun.istack.internal.NotNull;

import java.io.IOException;
import java.util.*;

public class ServerModel implements TCPServerObserver {

    DataReaderInterface csvReader;
    XMLSerializer xmlSerializer = new XMLSerializer();
    ServerInterfaceForUser tcpServer;
    Map<String, List<DataItem>> dataMap = new HashMap<>();
    Map<String, String> infoMap = new HashMap<>();
    Map<String, List<ParametersToSend>> dataMapFromServer = new HashMap<>();
    private ModelViewInterface modelView;

    public static void main(String[] args) {
        //ServerModel asd = new ServerModel(/*new CSVReader()*/);
    }

    /*public ServerModel(DataReaderInterface csvReader) {
        this.csvReader = csvReader;
        convertDataToMap(this.csvReader.readData("src\\Data.csv", 3));
        convertInfoToMap(this.csvReader.readData("src\\Users.csv", 2));
        tcpServer = Server.getInstance();
        tcpServer.registerObserver(this);
        InitializeTCPServerTransmitBuffer();
    }*/

    public ServerModel(ModelViewInterface modelView) {
        this.modelView = modelView;
        this.csvReader = new CSVReader();
        convertDataToMap(this.csvReader.readData("Data.csv", 3));
        convertInfoToMap(this.csvReader.readData("Users.csv", 2));
        tcpServer = Server.getInstance();
        tcpServer.registerObserver(this);
        InitializeTCPServerTransmitBuffer();
        startClients();
    }

    private void convertDataToMap(List<List<String>> parameters) {
        for( List<String> dataObj : parameters ) {
            if( dataMap.get( dataObj.get(1) ) == null ) {
                dataMap.put( dataObj.get(1), new LinkedList<DataItem>() );
            }
            dataMap.get(dataObj.get(1)).add(new DataItem(dataObj.get(0), dataObj.get(1), dataObj.get(2)));
        }
    }

    private void convertInfoToMap(List<List<String>> parameters) {
        for( List<String> dataObj : parameters ) {
            if( infoMap.get( dataObj.get(1) ) == null ) {
                infoMap.put( dataObj.get(0), dataObj.get(1) );
            }
        }
    }

    private void generateParameterValues(String id) {

        List<DataItem> dataItems = dataMap.get(id);
        Random random = new Random();
        for( DataItem dataItem : dataItems ) {
            dataItem.setParamValue(random.nextDouble() * 10);
        }
    }

    private String createXMLString(List<ParametersToSend> parametersToSend) {

        String str = xmlSerializer.serialize( parametersToSend )/* + "\n\r"*/;
        //List<ParametersToSend> parameters = xmlSerializer.deSerialize(str);
        return str;
    }

    private List<ParametersToSend> convertDataItemsToParamToSend(@NotNull List<DataItem> dataItems) {

        List<ParametersToSend> listParameters = new LinkedList<>();

        for( DataItem dataItem : dataItems ) {
            listParameters.add(new ParametersToSend( dataItem.getParamName(), dataItem.getParamValue()));
        }
        return listParameters;
    }

    private void generateInitialParameterValues() {
        for (String id : dataMap.keySet()) {
            generateParameterValues(id);
        }
    }
    private void InitializeTCPServerTransmitBuffer() {
        generateInitialParameterValues();
        for( String id: dataMap.keySet()) {
            List<ParametersToSend> parameters = convertDataItemsToParamToSend(dataMap.get(id));
            tcpServer.writeTransmitBuffer( id, createXMLString(parameters) );
        }
    }

    private void updateDataFromServer(String id, List<ParametersToSend> parameters) {
        if( dataMapFromServer.get(id) == null ) {
            dataMapFromServer.put( id, parameters );
        } else {
            dataMapFromServer.replace( id, parameters );
        }
    }

    @Override
    public void transmitEvent(String id) {
        String data = createStringFromData(dataMap.get(id));
        modelView.addDataToTable( new TableData( "Отправка", infoMap.get(id), data));

        generateParameterValues(id);
        List<ParametersToSend> parameters = convertDataItemsToParamToSend(dataMap.get(id));

        tcpServer.writeTransmitBuffer( id, createXMLString(parameters) );
    }

    @Override
    public void receiveEvent(String id) {
        List<ParametersToSend> parameters = xmlSerializer.deSerialize( tcpServer.readReceiveBuffer(id) );
        updateDataFromServer(id, parameters);

        String data = createStringFromData(dataMapFromServer.get(id));
        modelView.addDataToTable( new TableData( "Приём", infoMap.get(id), data));
    }

    private <T> String createStringFromData(List<T> list) {
        StringBuilder str = new StringBuilder();
        for( T listElem : list ) {
            str.append(listElem.toString());
        }
        return str.toString();
    }

    private void startClients() {
        Runtime re = Runtime.getRuntime();
        try{
            for(String id : dataMap.keySet()) {
                re.exec("java -jar " + "T-soft_1_Client.jar " + id);
            }
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
}
