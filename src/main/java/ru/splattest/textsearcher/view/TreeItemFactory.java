package ru.splattest.textsearcher.view;

import javafx.scene.control.TreeItem;

import java.nio.file.Path;
import java.util.*;

/**
 * Created by Антон on 12.08.2018.
 */
public class TreeItemFactory {

    private Map<Path, TreeItem<Path>> treeItems;
    private TreeItem<Path> root;

    public TreeItem<Path> getRootWithChildren(Path source, List<Path> files)
    {
        treeItems = new HashMap<>();
        root = new TreeItem<>(source);
        treeItems.put(source, root);
        files.forEach(f -> buildParents(new TreeItem<Path>(f)));
        return root;
    }

    private void buildParents(TreeItem<Path> treeItem) {
        Path parent = treeItem.getValue().getParent();
        if(treeItems.containsKey(treeItem.getValue().getParent()))
        {
            treeItems.get(parent).getChildren().add(treeItem);
        }
        else
        {
            TreeItem<Path> parentItem = new TreeItem<>(parent);
            parentItem.getChildren().add(treeItem);
            treeItems.put(parent, parentItem);
            buildParents(parentItem);
        }
    }


}
