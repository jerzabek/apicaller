package ga.jarza.apicaller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Controller {

  public final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
  public TextArea textField;
  public TextField adressField, reptext;
  public TextField headerKey, headerValue;
  public Button loadButton;
  public CheckBox repcb;
  public TableColumn keyCol, valCol;
  public TableView<Header> table;

  /**
   * Runnable interface that calls the loadAndWrite() method.
   */
  public final Runnable reloader = () -> {
    loadAndWrite();
    System.out.println("reload performed");
  };

  boolean running = false;

  /**
   * This method sends an HTTP request to the given adress, if it is valid. Then it displays it.
   */
  public void loadAndWrite() {
    // Checking if there is an acceptable value in the adress text field
    if(adressField.getText().replaceAll(" ", "").equals(""))
      return;

    try {
      HttpPost httpPost = new HttpPost(adressField.getText());

      // CORS
      //httpPost.setConfig(RequestConfig.copy(httpPost.getConfig()).setAuthenticationEnabled(false).build());

      // Adding the header's to the http request
      List<NameValuePair> postData = new java.util.ArrayList<>();
      for(Header x : table.getItems()){
        postData.add(new BasicNameValuePair(x.getKey(), x.getValue()));
      }

      httpPost.setEntity(new UrlEncodedFormEntity(postData));
      final HttpResponse response = HttpClients.createDefault().execute(httpPost);

      // formatting the given json file
      String jsonStr = EntityUtils.toString(
        response.getEntity())
        .replaceAll("}", "\n}\n")
        .replaceAll(",", ",\n")
        .replaceAll("\\[", "[\n")
        .replaceAll("]", "\n]\n")
        .replaceAll("\\{", " {\n");

      JSONObject json = new JSONObject(jsonStr);

      textField.setText(jsonStr);

      // Setting up the frequent API calling based on the repcb checkbox
      if (repcb.isSelected()) {
        if (!running) {
          ses.scheduleWithFixedDelay(reloader, 0, Long.parseLong(reptext.getText()), TimeUnit.SECONDS);
          running = true;
        }
      } else {
        ses.shutdownNow();
        running = false;
      }

    } catch (Throwable ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Returns pre-set headers on start up
   */
  public ObservableList<Header> getHeaders(){
    ObservableList<Header> headers = FXCollections.observableArrayList();
    return headers;
  }

  /**
   * This method is called whenever the Add Header button is called.
   */
  public void addHeader(){
    if(headerKey.getText().replaceAll(" ", "").equals("")
      || headerValue.getText().replaceAll(" ", "").equals(""))
      return;

    table.getItems().add(new Header(headerKey.getText(), headerValue.getText()));
    headerKey.clear();
    headerValue.clear();
  }

  /**
   * Stops the API being called periodically.
   */
  public void stopRefreshing(){
    ses.shutdownNow();
  }

  /**
   * Removes the selected TableView items.
   */
  public void removeHeaders(){
    table.getSelectionModel().getSelectedItems().forEach(table.getItems()::remove);
  }

  /**
   * Gives the user the option to save the data that the given API responded with.
   */
  public void saveData() {
    FileChooser fc = new FileChooser();
    fc.setTitle("Save the API's result");
    fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Plain text", "*.txt"), new FileChooser.ExtensionFilter("DAT file", "*.dat"));
    File file = fc.showSaveDialog(Main.stage);

    String ext = fc.getSelectedExtensionFilter().getExtensions().get(0);

    if (file != null) {
      try {
        Files.write(Paths.get(file.getAbsolutePath()), Collections.singleton(textField.getText()), Charset.forName("UTF-8"));
      } catch (IOException ex) {
        System.out.println(ex.getMessage());
      }
    }
  }

}
