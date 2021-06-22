package controllers;

import commandtype.*;
import utils.FileInfo;
import utils.Network;

import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CloudPanelController extends BasePanelController {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Network.start();
        updateList(null);
        super.initialize(location, resources);
    }

    @Override
    protected void fileCreate(Path path) {
        Network.sendMsg(new Command(path, Command.BaseCommand.NEW));
    }

    @Override
    protected void fileRename(Path oldName, Path newName) {
        Network.sendMsg(new Command(oldName, newName, Command.BaseCommand.RENAME));
    }

    @Override
    protected void fileDelete(Path path) {
        Network.sendMsg(new Command(path, Command.BaseCommand.DELETE));
    }

    @Override
    public void btnCopy(Path srcPath, Path dstPath, BasePanelController updatedPanel) {
        System.out.printf("[Cloud]:%s -> %s [Client] \n",srcPath.toString(),dstPath.toString());
        Path path = srcPath;
        Network.sendMsg(new FileRequest(path));
        new Thread(() -> {
            while (true) {
                FileMessage msg;
                try {
                    msg = (FileMessage) Network.readObject();
                    Files.write(dstPath.resolve(msg.getName()), msg.getData(), StandardOpenOption.CREATE);
                    updatedPanel.updateList(dstPath);
                    System.out.println(path.toString() + " copied");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }).start();
    }

    @Override
    public void btnMove(Path srcPath, Path dstPath, BasePanelController updatedPanel) {
        System.out.printf("[Cloud]:%s -> %s [Client] \n",srcPath.toString(),dstPath.toString());
        Path path = srcPath;
        Network.sendMsg(new FileRequest(path));
        new Thread(() -> {
            while (true) {
                FileMessage msg;
                try {
                    msg = (FileMessage) Network.readObject();
                    Files.write(dstPath.resolve(msg.getName()), msg.getData(), StandardOpenOption.CREATE);
                    updatedPanel.updateList(dstPath);
                    System.out.println(path.toString() + " copied");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fileDelete(srcPath);
                updateList(srcPath.getParent());
                return;
            }
        }).start();
    }

    @Override
    protected void updateList(Path path) {
        Network.sendMsg(new DirRequest(path));
        new Thread(() -> {
            while (true) {
                DirMessage msg;
                try {
                    msg = (DirMessage) Network.readObject();
                    System.out.println(msg.getClass() + " received");
                    pathField.setText(msg.getPath());
                    List<FileInfo> pathList = Arrays.stream(msg.getDir())
                            .map(str -> Paths.get(str))
                            .map(FileInfo::new)
                            .collect(Collectors.toList());
                    clientFiles.getItems().clear();
                    for (FileInfo fileInfo : pathList) {
                        clientFiles.getItems().add(fileInfo);
                    }
                    clientFiles.sort();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }).start();
    }

}
