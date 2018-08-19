package ru.splattest.textsearcher.view;

import com.sun.org.apache.xpath.internal.SourceTree;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Created by Антон on 12.08.2018.
 */
public class FileCellFactory implements Callback<TreeView<Path>, TreeCell<Path>> {

    private Consumer<Path> callBack;

    public FileCellFactory(Consumer<Path> callBack) {
        this.callBack = callBack;
    }

    public TreeCell<Path> call(TreeView<Path> tv) {
        return new TreeCell<Path>() {

            @Override
            protected void updateItem(Path item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(null);


                if(empty || item == null)
                {
                    setText("");
                }
                else
                {
                    if(item.getFileName() == null)
                    {
                        setText(item.toString());
                    }
                    else
                    {
                        setText(item.getFileName().toString());
                    }
                }

                if(item != null)
                {
                    if(Files.isRegularFile(item))
                    {

                        ContextMenu menu = new ContextMenu();
                        MenuItem menuItem = new Menu("Read file");
                        menuItem.setOnAction(event ->
                        {
                            callBack.accept(item);
                            menu.hide();
                        });
                        menu.getItems().add(menuItem);
                        setOnMouseClicked(event -> {
                            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                                callBack.accept(item);
                            }
                            if(event.getButton() == MouseButton.SECONDARY && event.getClickCount() == 1) {
                                setContextMenu(menu);
                            }
                        });
                        setGraphic(new ImageView(
                                new Image("img/file.png", 20, 20, true, true)));
                    }
                    else if(Files.isDirectory(item) && item.getFileName() != null)
                    {
                        setGraphic(new ImageView(
                                new Image("img/folder.png", 20, 20, true, true)));
                    }
                    else if(Files.isDirectory(item) && item.getFileName() == null)
                    {
                        setGraphic(new ImageView(
                                new Image("img/disk.png", 20, 20, true, true)));
                    }
                }
            }

        };
    }
}
