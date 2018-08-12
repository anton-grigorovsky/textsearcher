package ru.splattest.textsearcher.tools;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Created by Антон on 11.08.2018.
 */
public class IconStorage {
    private static final Node folderIcon = new ImageView(
            new Image("img/folder.png", 20, 20, true, true));
    private static final Node fileIcon = new ImageView(
            new Image("img/file.png", 20, 20, true, true));
    private static final Node diskIcon = new ImageView(
            new Image("img/disk.png", 20, 20, true, true));

    public static Node getFolderIcon() {
        return folderIcon;
    }

    public static Node getFileIcon() {
        return fileIcon;
    }

    public static Node getDiskIcon() {
        return diskIcon;
    }
}
