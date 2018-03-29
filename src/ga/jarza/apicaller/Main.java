package ga.jarza.apicaller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application {

  public static Controller controller;
  public static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{
      primaryStage.setTitle("API caller - by ivan jerzabek");
      stage = primaryStage;

      // storing the Controller instance created by the FXMLLoader for easy access to the UI elements
      FXMLLoader l = new FXMLLoader(getClass().getResource("sample.fxml"));
      HBox root = l.load();
      controller = l.getController();

      // Setting up some element's properties
      controller.reptext.disableProperty().bind(controller.repcb.selectedProperty().not());
      controller.reptext.setPromptText("seconds");

      controller.keyCol.setText("Key");
      controller.keyCol.setMinWidth(200);
      controller.keyCol.setCellValueFactory(new PropertyValueFactory<Header, String>("key"));

      controller.valCol.setText("Value");
      controller.valCol.setMinWidth(200);
      controller.valCol.setCellValueFactory(new PropertyValueFactory<Header, String>("value"));

      controller.table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

      controller.table.setItems(controller.getHeaders());

      controller.textField.setPrefColumnCount(2);
      controller.textField.prefWidthProperty().bind(primaryStage.widthProperty());

      // Setting up the window
      primaryStage.setMinWidth(600);
      primaryStage.setMinHeight(500);
      primaryStage.setScene(new Scene(root, 1024, 600));

      primaryStage.show();

    }

    @Override
    public void stop(){
      // Stopping the ScheduledExecutorService and the API refreshing process
      controller.ses.shutdownNow();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
