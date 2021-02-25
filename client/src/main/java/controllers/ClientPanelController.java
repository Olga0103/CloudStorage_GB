package controllers;

import utils.FileInfo;
import utils.Network;
import commandtype.FileMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ClientPanelController extends BasePanelController {

    @FXML
    ComboBox<String> diskSelector;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initDiskSelector();
        updateList(Paths.get(diskSelector.getSelectionModel().getSelectedItem()));
        super.initialize(location, resources);
    }

    @Override
    protected void fileCreate(Path path) {

        new File(path.toString()).mkdir();
    }

    @Override
    protected void fileDelete(Path path) {
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "File was not deleted").showAndWait();
        }
    }

    @Override
    protected void fileRename(Path oldName, Path newName) {
        try {
            Files.copy(oldName, newName);
            Files.delete(oldName);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to rename file!").showAndWait();
        }
    }

    @Override
    protected void updateList(Path path) {
        pathField.setText(path.normalize().toAbsolutePath().toString());
        try {
            clientFiles.getItems().clear();
            clientFiles.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
            clientFiles.sort();
        } catch (IOException e) {
            new Alert(Alert.AlertType.WARNING, String.format("No access %s", path), ButtonType.OK).showAndWait();
        }
    }

    public void btnSelectDisk(ActionEvent actionEvent) {
        ComboBox<String> event = (ComboBox<String>) actionEvent.getSource();
        updateList(Paths.get(event.getSelectionModel().getSelectedItem()));
    }

    private void initDiskSelector() {
        diskSelector.getItems().clear();
        for (Path path : FileSystems.getDefault().getRootDirectories()) {
            diskSelector.getItems().add(path.toString());
        }
        diskSelector.getSelectionModel().select(0);
    }

    @Override
    public void btnCopy(Path srcPath, Path dstPath, BasePanelController updatedPanel) {
        System.out.printf("[Client]:%s -> %s [Cloud]\n",srcPath.toString(),dstPath.toString());
        Network.sendMsg(new FileMessage(srcPath,dstPath));
    }

    @Override
    public void btnMove(Path srcPath, Path dstPath, BasePanelController updatedPanel) {
        System.out.printf("[Client]:%s -> %s [Cloud]\n",srcPath.toString(),dstPath.toString());
        btnCopy(srcPath,dstPath,updatedPanel);
        fileDelete(srcPath);
        updateList(srcPath.getParent());
        updatedPanel.updateList(dstPath);
    }
}
