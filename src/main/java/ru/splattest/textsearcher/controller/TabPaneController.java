package ru.splattest.textsearcher.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Антон on 14.08.2018.
 */
public class TabPaneController {
    private static final Logger LOGGER = LogManager.getLogger(TabPaneController.class);
    private int tabCount;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab additionTab;

    @FXML
    private void initialize() {
        Button addTabButton = new Button();
        addTabButton.getStyleClass().add("tab-button");
        ImageView imageView = new ImageView(new Image("img/add_tab.png"));
        imageView.setFitHeight(15);
        imageView.setFitWidth(15);
        imageView.setSmooth(true);
        addTabButton.setGraphic(imageView);
        addTabButton.setOnAction(e -> addNewTab());
        additionTab.setGraphic(addTabButton);
        addNewTab();
        additionTab.setOnSelectionChanged(e -> {
            if(additionTab.isSelected())
            {
                tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2);
            }
        });
    }

    private void addNewTab() {
        try {
            SplitPane root = FXMLLoader.load(getClass().getResource("/fxml/MainWindow.fxml"));
            Tab tab = new Tab("Tab " + ++tabCount, root);
            int index = tabPane.getTabs().size() - 1;
            tabPane.getTabs().add(index, tab);
            tabPane.getSelectionModel().select(tab);
            tab.setClosable(true);

        }
        catch (Exception e)
        {
            LOGGER.error("Error with creating tab app: " + e.getMessage());
        }
    }
}
