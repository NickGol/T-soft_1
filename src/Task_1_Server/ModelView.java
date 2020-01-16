package Task_1_Server;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ModelView implements ModelViewInterface {

    @FXML
    private TableView<TableData> table;

    @FXML
    private TableColumn<TableData,String> timeColumn;

    @FXML
    private TableColumn<TableData,String> directionColumn;

    @FXML
    private TableColumn<TableData,String> userColumn;

    @FXML
    private TableColumn<TableData,String> dataColumn;

    private static final String Column1Key = "time";
    private static final String Column2Key = "direction";
    private static final String Column3Key = "user";
    private static final String Column4Key = "data";
    private ObservableList<TableData> tableDatalist;
    private ServerModel serverModel;

    @FXML
    void initialize() {
        tableDatalist = FXCollections.observableArrayList();
        timeColumn.setCellValueFactory(new PropertyValueFactory<TableData,String>(Column1Key));
        directionColumn.setCellValueFactory(new PropertyValueFactory<TableData,String>(Column2Key));
        userColumn.setCellValueFactory(new PropertyValueFactory<TableData,String>(Column3Key));
        dataColumn.setCellValueFactory(new PropertyValueFactory<TableData,String>(Column4Key));
        table.setItems(tableDatalist);
        /*for(int i=0; i<2; i++) {
            tableDatalist.add( new TableData( String.valueOf(i), String.valueOf(i*10), String.valueOf(i*100)));
        }*/
        serverModel = new ServerModel(this);
    }

    @Override
    public void addDataToTable(TableData dataItem) {
        tableDatalist.add(dataItem);
    }
}