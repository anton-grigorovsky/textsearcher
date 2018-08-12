package ru.splattest.textsearcher.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ru.splattest.textsearcher.tools.IconStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alexander Bolte - Bolte Consulting (2010 - 2014).
 *
 *         This class shall be a simple implementation of a TreeItem for
 *         displaying a file system tree.
 *
 *         The idea for this class is taken from the Oracle API docs found at
 *         http
 *         ://docs.oracle.com/javafx/2/api/javafx/scene/control/TreeItem.html.
 *
 *         Basically the file sytsem will only be inspected once. If it changes
 *         during runtime the whole tree would have to be rebuild. Event
 *         handling is not provided in this implementation.
 */
public class FileTreeItem extends TreeItem<Path> {

    private List<Path> foundFiles;

    /**
     * Calling the constructor of super class in oder to create a new
     * TreeItem<File>.
     *
     * @param f
     *            an object of type File from which a tree should be build or
     *            which children should be gotten.
     */

    public FileTreeItem(Path f, List<Path> foundFiles) {
        super(f);
        this.foundFiles = foundFiles;
        super.getChildren().setAll(buildChildren(this));
    }

    /*
     * (non-Javadoc)
     *
     * @see javafx.scene.control.TreeItem#getChildren()
     */
    @Override
    public ObservableList<TreeItem<Path>> getChildren() {
        if (isFirstTimeChildren) {
            isFirstTimeChildren = false;

			/*
			 * First getChildren() call, so we actually go off and determine the
			 * children of the File contained in this TreeItem.
			 */

        }
        return super.getChildren();
    }

    /*
     * (non-Javadoc)
     *
     * @see javafx.scene.control.TreeItem#isLeaf()
     */
    @Override
    public boolean isLeaf() {
        if (isFirstTimeLeaf) {
            isFirstTimeLeaf = false;
            Path f = (Path) getValue();
            isLeaf = Files.isRegularFile(f);
        }

        return isLeaf;
    }

    /**
     * Returning a collection of type ObservableList containing TreeItems, which
     * represent all children available in handed TreeItem.
     *
     * @param treeItem
     *            the root node from which children a collection of TreeItem
     *            should be created.
     * @return an ObservableList<TreeItem<File>> containing TreeItems, which
     *         represent all children available in handed TreeItem. If the
     *         handed TreeItem is a leaf, an empty list is returned.
     */
    private ObservableList<TreeItem<Path>> buildChildren(TreeItem<Path> treeItem) {
        Path f = treeItem.getValue();
        if (f != null && Files.isDirectory(f)) {
            try
            {
                List<Path> files = Files.list(f).collect(Collectors.toList());
                if (files != null) {
                    ObservableList<TreeItem<Path>> children = FXCollections
                            .observableArrayList();

                    for (Path childFile : files) {
                        for(Path foundFile: foundFiles)
                        {
                            if(foundFile.startsWith(childFile))
                            {
                                children.add(new FileTreeItem(childFile, foundFiles));
                                break;
                            }
                        }
                    }
                    return children;
                }
            }
            catch (IOException e) {
                return FXCollections.observableArrayList();
            }

        }

        return FXCollections.emptyObservableList();
    }

    private boolean isFirstTimeChildren = true;
    private boolean isFirstTimeLeaf = true;
    private boolean isLeaf;
}
