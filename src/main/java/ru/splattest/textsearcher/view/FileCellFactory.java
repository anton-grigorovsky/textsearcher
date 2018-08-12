package ru.splattest.textsearcher.view;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import ru.splattest.textsearcher.tools.IconStorage;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Антон on 12.08.2018.
 */
public class FileCellFactory implements Callback<TreeView<Path>, TreeCell<Path>> {

    public TreeCell<Path> call(TreeView<Path> tv) {
        return new TreeCell<Path>() {

            @Override
            protected void updateItem(Path item, boolean empty) {
                super.updateItem(item, empty);

                setText((empty || item == null) ? "" : item.getFileName().toString());
                if(item != null)
                {
                    if(Files.isRegularFile(item))
                    {
                        setGraphic(new ImageView(
                                new Image("img/file.png", 20, 20, true, true)));
                    }
                    else if(Files.isDirectory(item))
                    {
                        setGraphic(new ImageView(
                                new Image("img/folder.png", 20, 20, true, true)));
                    }
                }
            }

        };
    }
}
