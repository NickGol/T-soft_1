package Client;

import DataObjects.DataItem;
import DataObjects.ParametersToSend;
import TCP.Client;
import TCP.ServerInterfaceForUser;
import TCP.TCPServerObserver;
import Task_1_Server.*;
import com.sun.istack.internal.NotNull;

import java.util.*;

public class ClientModel implements TCPServerObserver {

    private DataReaderInterface csvReader;
    private XMLSerializer xmlSerializer = new XMLSerializer();
    private ServerInterfaceForUser tcpClient;
    //Map<String, List<DataItem>> dataMap = new HashMap<>();
    //Map<String, String> infoMap = new HashMap<>();
    private Map<String, List<ParametersToSend>> dataMapFromTCP;// = new HashMap<>();
    private Map<String, List<ParametersToSend>> dataMapToSend;// = new HashMap<>();
    //List<ParametersToSend> parametersFromTCP;
    private List<ParametersToSend> parametersToSend;
    private ModelViewInterface modelView;
    private String id;

    public static void main(String[] args) {

    }

    public ClientModel(ModelViewInterface modelView, String id) {
        this.modelView = modelView;
        dataMapFromTCP = new HashMap<>();
        dataMapToSend = new HashMap<>();
        tcpClient = Client.getInstance(id);
        tcpClient.registerObserver(this);
    }

    private String createXMLString(List<ParametersToSend> parametersToSend) {

        String str = xmlSerializer.serialize( parametersToSend ) + "\n\r";
        return str;
    }

    private List<ParametersToSend> convertDataItemsToParamToSend(@NotNull List<DataItem> dataItems) {

        List<ParametersToSend> listParameters = new LinkedList<>();

        for( DataItem dataItem : dataItems ) {
            listParameters.add(new ParametersToSend( dataItem.getParamName(), dataItem.getParamValue()));
        }
        return listParameters;
    }


    private void updateDataFromTCP(String id, List<ParametersToSend> parameters) {
        if( dataMapFromTCP.get(id) == null ) {
            dataMapFromTCP.put( id, parameters );
        } else {
            dataMapFromTCP.replace( id, parameters );
        }
    }

    @Override
    public void transmitEvent(String id) {
        String data = createStringFromData(dataMapToSend.get(id));

        modelView.addDataToTable( new TableData( "Отправка", "Сервер", data));
    }

    @Override
    public void receiveEvent(String id) {
        List<ParametersToSend> parameters = xmlSerializer.deSerialize( tcpClient.readReceiveBuffer(id) );
        updateDataFromTCP(id, parameters);

        String data = createStringFromData(dataMapFromTCP.get(id));
        modelView.addDataToTable( new TableData( "Приём", "Сервер", data));

        createDataToSend(id);
        tcpClient.writeTransmitBuffer( id, createXMLString(dataMapToSend.get(id)) );
    }

    private <T> String createStringFromData(List<T> list) {
        StringBuilder str = new StringBuilder();
        for( T listElem : list ) {
            str.append(listElem.toString());
        }
        return str.toString();
    }

    private void createDataToSend(String id) {
        List<ParametersToSend> parametersList = new LinkedList<>();
        for( ParametersToSend parameter : dataMapFromTCP.get(id) ) {
            parametersList.add(  new ParametersToSend( parameter.getParamName(), parameter.getParamValue() )  );
        }
        for( ParametersToSend parameters : parametersList ) {
            parameters.setParamValue( parameters.getParamValue()*2 );
        }
        if( dataMapToSend.get(id)==null ) {
            dataMapToSend.put(id, parametersList);
        } else {
            dataMapToSend.replace(id, parametersList);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
