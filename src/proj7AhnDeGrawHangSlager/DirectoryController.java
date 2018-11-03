package proj7AhnDeGrawHangSlager;

/*
 *  File: DirectoryController.java
 *  Names: Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 *  Date: 11/02/2018

--------------------------------------------------------
 * Edited From:
 * CS361 Project 6
 * Names: Douglas Abrams, Martin Deutsch, Robert Durst, Matt Jones
 * Date: 10/27/2018
 * This file contains the DirectoryController class, handling the file
 * directory portion of the GUI.
 */


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

/**
 * This controller handles directory related actions.
 *
 * @author Douglas Abrams
 * @author Martin Deutsch
 * @author Robert Durst
 * @author Matt Jones
 */
public class DirectoryController {

    /**
     * the tree view representing the directory
     */
    private TreeView directoryTree;
    /**
     * a HashMap mapping the tabs and the associated files
     */
    private Map<Tab, String> tabFileNameMap;
    /**
     * A HashMap mapping the TreeItems and associated files
     */
    private Map<TreeItem<String>, File> treeItemFileMap;
    /**
     * TabPane defined in Main.fxml
     */
    private TabPane tabPane;
    /**
     * FileMenuController defined in main controller
     */
    private FileController fileController;

    /**
     * Sets the directory tree from Main.fxml
     *
     * @param tv the directory tree
     */
    public void setDirectoryTree(TreeView tv) {
        this.directoryTree = tv;
        this.treeItemFileMap = new HashMap<>();

        // add listener to listen for clicks in the directory tree
        EventHandler<MouseEvent> mouseEventHandle = (MouseEvent event) -> handleDirectoryItemClicked(event);
        this.directoryTree.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandle);
    }


    /**
     * Sets the tabPane.
     *
     * @param tabPane TabPane
     */
    public void setTabPane(TabPane tabPane) {
        this.tabPane = tabPane;
        // add listener to tab selection to switch directories based on open file
        this.tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) ->
                this.createDirectoryTree());
    }

    /**
     * Sets the FileController and sets the hashmap returned
     * from the FileController
     *
     * @param fileController FileController created in main Controller.
     */
    public void setFileController(FileController fileController) {
        this.fileController = fileController;
        this.tabFileNameMap = fileController.getFilenames();

    }

    /**
     * Returns the directory tree for the given file
     *
     * @param file the file
     * @return the root TreeItem of the tree
     */
    private TreeItem<String> getNode(File file) {
        // create root, which is returned at the end
        TreeItem<String> root = new TreeItem<>(file.getName());
        treeItemFileMap.put(root, file);

        for (File f : file.listFiles()) {
            if (f.isDirectory()) {
                // recursively traverse file directory
                root.getChildren().add(getNode(f));
            } else {
                TreeItem<String> leaf = new TreeItem<>(f.getName());
                root.getChildren().add(leaf);
                treeItemFileMap.put(leaf, f);
            }
        }
        return root;
    }

    /**
     * Adds the directory tree for the current file to the GUI
     */
    public void createDirectoryTree() {
        // capture current file
        String fileName = this.tabFileNameMap.get(this.tabPane.getSelectionModel().getSelectedItem());
        if (fileName != null) {
            File file = new File(fileName);
            // create the directory tree
            this.directoryTree.setRoot(this.getNode(file.getParentFile()));
            this.directoryTree.getRoot().setExpanded(false);
        }
    }

    /**
     * Event handler to open a file selected from the directory
     *
     * @param event a MouseEvent object
     */
    private void handleDirectoryItemClicked(MouseEvent event) {
        // only open file if double clicked
        if (event.getClickCount() == 2 && !event.isConsumed()) {
            event.consume();
            TreeItem selectedItem = (TreeItem) directoryTree.getSelectionModel().getSelectedItem();
            String fileName = (String) selectedItem.getValue();
            if (fileName.endsWith(".java") || fileName.endsWith(".css") || fileName.endsWith(".fxml")) {
                this.fileController.handleOpenFile(this.treeItemFileMap.get(selectedItem));
            }
        }
    }
}
