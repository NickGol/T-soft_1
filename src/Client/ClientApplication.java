package Client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Random;

public class ClientApplication extends Application {

    private static String id;
    public static void main(String[] args) {
        id = args[0];
        //id = "1";
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception{
        Random random = new Random();
        Thread.sleep( random.nextInt(5000) );
        //Parent root = FXMLLoader.load(getClass().getResource("clientView.fxml"));
        /*FXMLLoader loader = new FXMLLoader(getClass().getResource("clientView.fxml"));
        Parent root = loader.load();
        loader.<ClientModel>getController().setId(id);*/

        FXMLLoader loader = new FXMLLoader(getClass().getResource("clientView.fxml"));

        // Create a controller instance
        ClientModelView controller = new ClientModelView(id);
        // Set it in the FXMLLoader
        loader.setController(controller);
        Parent root = loader.load();
        //FlowPane root = loader.load();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
        primaryStage.setTitle("Клиент ID = " + id);
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }
}
