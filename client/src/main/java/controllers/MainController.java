package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    VBox clientPanel,cloudPanel;

    BasePanelController client,cloud;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        client = (ClientPanelController) clientPanel.getProperties().get("ctrl");
        cloud = (CloudPanelController) cloudPanel.getProperties().get("ctrl");
    }

    public void btnExit() {

        Platform.exit();
    }

    public void btnClientCopy(ActionEvent actionEvent) {
        if (client.getSelectedItem() != null){
            Path srcPath = Paths.get(client.getCurrentPath()).resolve(client.getSelectedItem().getName());
            Path dstPath = Paths.get(cloud.getCurrentPath());
            client.btnCopy(srcPath,dstPath,cloud);
        }else{
            new Alert(Alert.AlertType.ERROR, "select a file!").showAndWait();
        }
    }

    public void btnClientMove(ActionEvent actionEvent) {
        if (client.getSelectedItem() != null){
            Path srcPath = Paths.get(client.getCurrentPath()).resolve(client.getSelectedItem().getName());
            Path dstPath = Paths.get(cloud.getCurrentPath());
            client.btnMove(srcPath,dstPath,cloud);
        }else{
            new Alert(Alert.AlertType.ERROR, "select a file!").showAndWait();
        }
    }

    public void btnCloudCopy(ActionEvent actionEvent) {
        if (cloud.getSelectedItem() != null){
            Path srcPath = Paths.get(cloud.getCurrentPath()).resolve(cloud.getSelectedItem().getName());
            Path dstPath = Paths.get(client.getCurrentPath());
            cloud.btnCopy(srcPath,dstPath,client);
        }else{
            new Alert(Alert.AlertType.ERROR, "select a file!").showAndWait();
        }
    }

    public void btnCloudMove(ActionEvent actionEvent) {
        if (cloud.getSelectedItem() != null){
            Path srcPath = Paths.get(cloud.getCurrentPath()).resolve(cloud.getSelectedItem().getName());
            Path dstPath = Paths.get(client.getCurrentPath());
            cloud.btnMove(srcPath,dstPath,client);
        }else{
            new Alert(Alert.AlertType.ERROR, "select a file!").showAndWait();
        }
    }
}

