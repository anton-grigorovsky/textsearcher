package ru.splattest.textsearcher.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import org.apache.commons.lang3.StringUtils;
import ru.splattest.textsearcher.service.TextSearcherService;
import ru.splattest.textsearcher.tools.LoadIndicator;
import ru.splattest.textsearcher.tools.TextWalker;
import ru.splattest.textsearcher.utils.ValidationUtils;
import ru.splattest.textsearcher.view.FileCellFactory;
import ru.splattest.textsearcher.view.TreeItemFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Created by Антон on 08.08.2018.
 */
public class MainWindowController {
    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    @FXML
    private SplitPane splitPane;
    @FXML
    private TextField sourceTextField;
    @FXML
    private TextField inputTextField;
    @FXML
    private TextField formatTextField;
    @FXML
    private Button searchButton;
    @FXML
    private Button stopButton;
    @FXML
    private TextArea outputText;
    @FXML
    private Button directoryChooserButton;
    @FXML
    private TreeView<Path> treeView;
    @FXML
    private BorderPane infoPane;
    private Label infoLabel;
    private TextSearcherService service;
    private TextWalker textWalker;

    public MainWindowController() {
        service = new TextSearcherService();
    }

    @FXML
    @SuppressWarnings("unchecked")
    private void initialize() {
        directoryChooser.setTitle("Select Some Directories");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        directoryChooserButton.setOnAction(e -> chooseDirectory());
        treeView.setCellFactory(new FileCellFactory(path -> {
            outputText.setText(null);
            service.getText(path, text ->
            {
                outputText.setText("");
                System.gc();
                outputText.setText(text);
            });
            textWalker = new TextWalker(outputText);
        }));
        infoPane.minWidthProperty().bind(splitPane.widthProperty().multiply(0.1));
        infoPane.maxWidthProperty().bind(splitPane.widthProperty().multiply(0.5));
        outputText.setEditable(false);
    }

    @FXML
    private void start()
    {
        boolean success = validateInputData();
        if(success)
        {
            searchButton.setDisable(true);
            stopButton.setDisable(false);
            outputText.clear();
            infoLabel = new Label();
            infoPane.setCenter(infoLabel);
            LoadIndicator loadIndicator = new LoadIndicator(infoLabel);
            treeView.setRoot(null);
            service.setCallBack(fileList ->
            {
                stopButton.setDisable(true);
                searchButton.setDisable(false);
                if(fileList.size() == 0)
                {
                    infoLabel.setText("There are no files with \n specified format or text :(");
                }
                else
                {
                    infoPane.getChildren().remove(infoLabel);
                    TreeItem root = new TreeItemFactory().getRootWithChildren(Paths.get(sourceTextField.getText()), fileList);
                    root.setExpanded(true);
                    treeView.setRoot(root);
                    infoPane.setCenter(treeView);
                }
            });
            service.setFormat(formatTextField.getText());
            service.setPath(sourceTextField.getText());
            service.setText(inputTextField.getText());
            service.setIndicator(loadIndicator);
            service.findFilesWithText();
        }
    }

    private boolean validateInputData() {
        return ValidationUtils.isPathValid(sourceTextField) &&
                ValidationUtils.isFormatValid(formatTextField) &&
                ValidationUtils.isTextValid(inputTextField);
    }

    @FXML
    private void chooseDirectory()
    {
        String source = sourceTextField.getText();
        if(StringUtils.isNotEmpty(source))
        {
            setInitialDirectory(source);
        }
        File file = directoryChooser.showDialog(splitPane.getScene().getWindow());
        if(file != null) {
            sourceTextField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void onFindNext()
    {
        Platform.runLater(() -> {
            textWalker.findNext(inputTextField.getText());
        });

    }

    @FXML
    private void onFindPrevious()
    {
        Platform.runLater(() -> {
            textWalker.findPrevious(inputTextField.getText());
        });

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

    @FXML
    private void stop()
    {
        searchButton.setDisable(false);
        stopButton.setDisable(true);
        service.stop();
    }

}


