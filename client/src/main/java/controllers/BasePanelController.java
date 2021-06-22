package controllers;

import utils.FileInfo;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.net.URL;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public abstract class BasePanelController implements Initializable {
    @FXML
    TextField pathField;

    @FXML
    TableView<FileInfo> clientFiles;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTableView();
        mouseListener();
    }

    public abstract void btnCopy(Path srcPath, Path dstPath, BasePanelController updatedPanel);

    public abstract void btnMove(Path srcPath, Path dstPath, BasePanelController updatedPanel);

    protected abstract void updateList(Path path);

    protected abstract void fileCreate(Path path);

    protected abstract void fileRename(Path oldName, Path newName);

    protected abstract void fileDelete(Path path);

    public void btnNew(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create a new directory");
        dialog.setHeaderText("Enter title:");

        Optional<String> text = dialog.showAndWait();
        Path path = Paths.get(pathField.getText()).resolve(text.get());
        fileCreate(path);
        new File(path.toString()).mkdir();
        updateList(Paths.get(pathField.getText()));
    }

    public void btnRename(ActionEvent actionEvent) {
        FileInfo file = clientFiles.getSelectionModel().getSelectedItem();
        if (file == null) {
            new Alert(Alert.AlertType.ERROR, "Select file").showAndWait();
        } else {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Rename");
            dialog.setHeaderText("Enter new file name:");

            Optional<String> text = dialog.showAndWait();
            Path newPath = Paths.get(pathField.getText()).resolve(text.get());
            Path oldPath = Paths.get(pathField.getText()).resolve(file.getName());
            fileRename(oldPath,newPath);
            updateList(Paths.get(pathField.getText()));
        }
    }

    public void btnDelete(ActionEvent actionEvent) {
        FileInfo file = clientFiles.getSelectionModel().getSelectedItem();
        if (file == null) {
            new Alert(Alert.AlertType.ERROR, "Select file").showAndWait();
        } else {
            Path path = Paths.get(pathField.getText()).resolve(file.getName());
            fileDelete(path);
            updateList(Paths.get(pathField.getText()));
        }
    }

    private void initTableView() {
        TableColumn<FileInfo, String> fileName = new TableColumn<>("Name");
        fileName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName()));
        fileName.setPrefWidth(250);

        TableColumn<FileInfo, Long> fileSize = new TableColumn<>("Size");
        fileSize.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        fileSize.setCellFactory(column -> new TableCell<FileInfo, Long>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty || item == -1L) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%,d bytes", item));
                }
            }
        });
        fileSize.setPrefWidth(150);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        TableColumn<FileInfo, String> lastModified = new TableColumn<>("Changed");
        lastModified.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLastModified().format(dtf)));
        lastModified.setPrefWidth(150);

        clientFiles.getColumns().addAll(fileName, fileSize, lastModified);
        clientFiles.getSortOrder().add(fileSize);
    }

    public void btnUpperKey(ActionEvent actionEvent) {
        Path upperPath = Paths.get(pathField.getText()).getParent();
        if (upperPath != null) {
            updateList(upperPath);
        }
    }

    private void mouseListener() {
        clientFiles.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    Path path = Paths.get(pathField.getText()).resolve(clientFiles.getSelectionModel().getSelectedItem().getName());
                    if (clientFiles.getSelectionModel().getSelectedItem().getType() == FileInfo.FileType.DIRECTORY) {
                        updateList(path);
                    }
                }
            }
        });
    }

    public FileInfo getSelectedItem(){

        return clientFiles.getSelectionModel().getSelectedItem();
    }

    public String getCurrentPath(){

        return pathField.getText();
    }

}
