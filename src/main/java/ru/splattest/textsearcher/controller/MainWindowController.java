package ru.splattest.textsearcher.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;
import ru.splattest.textsearcher.service.TextSearcherService;
import ru.splattest.textsearcher.tools.IconStorage;
import ru.splattest.textsearcher.view.FileCellFactory;
import ru.splattest.textsearcher.view.FileTreeItem;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 * Created by Антон on 08.08.2018.
 */
public class MainWindowController {
    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    @FXML
    private TabPane tabPane;
    @FXML
    private TextField sourceTextField;
    @FXML
    private TextField inputTextField;
    @FXML
    private TextField formatTextField;
    @FXML
    private Button searchButton;
    @FXML
    private TextArea outputText;
    @FXML
    private Button directoryChooserButton;
    @FXML
    private TreeView<Path> treeView;
    private TextSearcherService service;

    public MainWindowController() {
        init();
    }

    private void init() {
        service = new TextSearcherService();
        directoryChooser.setTitle("Select Some Directories");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }

    @FXML
    private void start()
    {
        service.findFilesWithText(sourceTextField.getText(),
                formatTextField.getText(),
                inputTextField.getText(),
                fileList -> {
                    buildTreeView(fileList);
                });
    }

    private void buildTreeView(List<Path> fileList) {
        TreeItem<Path> root = new FileTreeItem(Paths.get(sourceTextField.getText()), fileList);
        treeView.setCellFactory(new FileCellFactory());
        treeView.setRoot(root);
    }

    @FXML
    private void chooseDirectory()
    {
        String source = sourceTextField.getText();
        if(StringUtils.isNotEmpty(source))
        {
            setInitialDirectory(source);
        }
        File file = directoryChooser.showDialog(tabPane.getScene().getWindow());
        if(file != null) {
            sourceTextField.setText(file.getAbsolutePath());
        }
    }

    private void setInitialDirectory(String source)
    {
        File file = new File(source);
        if(file.exists()) {
            directoryChooser.setInitialDirectory(file);
        }
        else {
            if(source.contains(File.separator))
            {
                setInitialDirectory(source.substring(0, source.lastIndexOf(File.separator)));
            }
            else
            {
                return;
            }
        }
    }
}


