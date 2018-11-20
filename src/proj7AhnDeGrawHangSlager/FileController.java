/*
 * File: FileController.java
 * Names: Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * Class: CS 361
 * Project 7
 * Date: November 2, 2018
 * ---------------------------
 * Edited From: Zena Abulhab, Paige Hanssen, Kyle Slager, Kevin Zhou
 * Project 5
 * Date: October 12, 2018
 *
 */

package proj7AhnDeGrawHangSlager;

import javafx.event.Event;

import java.util.List;
import java.util.Optional;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.util.HashMap;

import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import proj9AhnDeGrawHangSlagerbantam.lexer.Scanner;
import proj9AhnDeGrawHangSlagerbantam.lexer.Token;
import proj9AhnDeGrawHangSlagerbantam.util.Error;
import proj9AhnDeGrawHangSlagerbantam.util.ErrorHandler;

/**
 * This class contains the handlers for each of the menu options in the IDE.
 *
 * Keeps track of the tab pane, the current tab, the index of the current tab
 * within the pane, and the File objects of the current tabs.
 *
 * @author  Zena Abulhab, Paige Hanssen, Kyle Slager Kevin Zhou (Project 5)
 * @author  Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * @version 3.0
 * @since   11-2-2018
 */
public class FileController {

    private JavaTabPane javaTabPane;

    // "True" means that the file has not been changed since its last save,
    // if any. False means that something has been changed in the file.
//    private HashMap<Tab, Boolean> saveStatus;

    private HashMap<Tab, String> tabFilepathMap;

    private VBox vBox;

    /**
     * ContextMenuController handling context menu actions
     */
    private ContextMenuController contextMenuController;

    private DirectoryController directoryController;

    private Scanner scanner;

    /**
     * Constructor for the class. Intializes the save status
     * and the tabFilepathMap in a HashMap
     */
    public FileController(VBox vBox, JavaTabPane javaTabPane, DirectoryController directoryController) {
        this.vBox = vBox;
        this.javaTabPane = javaTabPane;
        this.directoryController = directoryController;
        this.tabFilepathMap = new HashMap<>();
    }

    /**
     * Sets the contextMenuController.
     *
     * @param contextMenuController ContextMenuController handling context menu actions
     */
    public void setContextMenuController(ContextMenuController contextMenuController) {
        this.contextMenuController = contextMenuController;
    }

    /**
     * Returns the name of the file open in the current tab.
     * @return The name of the currently open file
     */
    protected String getFilePath(){
        Tab curTab = this.javaTabPane.getSelectionModel().getSelectedItem();
        return tabFilepathMap.get(curTab);
    }

    /**
     * Handler for the "About" menu item in the "File" menu.
     * Creates an Information alert dialog to display author and information of this program
     */
    public void handleAbout() {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("About");
        dialog.setHeaderText(null);
        dialog.setContentText("V3 Authors: Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager\n" +
                "Version 2 Authors: Zena Abulhab, Paige Hanssen, Kyle Slager, Kevin Zhou\n" +
                "Version 1 Authors: Kevin Ahn, Matt Jones, Jackie Hang, Kevin Zhou\n" +
                "This application is a basic IDE with syntax highlighting.");
        dialog.showAndWait();
    }

    /**
     * Handler for the "New" menu item in the "File" menu.
     * Adds a new Tab to the TabPane, adds null to the tabFilepathMap HashMap,
     * and false to the saveStatus HashMap
     */
    public void handleNew(File file) {
        this.javaTabPane.createNewTab(this, contextMenuController, file);
        JavaTab t = (JavaTab)this.javaTabPane.getSelectionModel().getSelectedItem();
        if (file == null) this.tabFilepathMap.put(t, null);
        else this.tabFilepathMap.put(t, file.getPath());

    }

    /**
     * Handler for the "Open" menu item in the "File" menu.
     * Creates a FileChooser to select a file
     * Use scanner to read the file and write it into a new tab.
     */
    public void handleOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open");
        Window stage = this.vBox.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null){
            handleNew(file);
        }
        directoryController.createDirectoryTree();
    }


    /**
     * Handler for the "Close" menu item in the "File" menu.
     * Checks to see if the file has been changed since the last save.
     * If changes have been made, redirect to askSaveAndClose and then close the tab.
     * Otherwise, just close the tab.
     */
    public void handleClose(Event event) {
        System.out.println("in close");
        JavaTab curTab = (JavaTab)this.javaTabPane.getSelectionModel().getSelectedItem();

//        if (this.javaTabPane.tabIsSaved(curTab)) this.closeTab();
//        else this.askSaveAndClose(event);

        if (tabFilepathMap.get(curTab) != null) {
            // check if any changes were made
            if (this.javaTabPane.tabIsSaved(curTab))
                this.closeTab();
            else
                this.askSaveAndClose(event);
        } else {
            if(!tabFilepathMap.isEmpty()) {
                this.askSaveAndClose(event);
            }
        }
    }

    /**
     * Handler for the "Save" menu item in the "File" menu.
     * If the current tab has been saved before, writes out the content to its corresponding
     * file in storage.
     * Else if the file has never been saved, opens a pop-up window that allows the user to
     * choose a filename and directory and then store the content of the tab to storage.
     */
    public boolean handleSave() {
        JavaTab curTab = (JavaTab)this.javaTabPane.getSelectionModel().getSelectedItem();

        if (tabFilepathMap.get(curTab) != null){
            File file = new File(tabFilepathMap.get(curTab));    // this is what gets the path
            writeFile(file);
            this.javaTabPane.updateTabSavedStatus(curTab, true);
            return true;
        }
        else
            return this.handleSaveAs();
    }

    /**
     * Handler for the "Save as..." menu item in the "File" menu.
     * Opens a pop-up window that allows the user to choose a filename and directory.
     * Calls writeFile to save the file to memory.
     * Changes the name of the current tab to match the newly saved file's name.
     */
    public boolean handleSaveAs() {
        JavaTab curTab = (JavaTab)this.javaTabPane.getSelectionModel().getSelectedItem();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save as...");
        Window stage = this.vBox.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        if (file == null){
            return false;
        }
        else{
            writeFile(file);
            tabFilepathMap.replace(curTab,file.getPath());
//            saveStatus.replace(curTab, true);
            this.javaTabPane.updateTabSavedStatus(curTab, true);
            this.directoryController.createDirectoryTree();
        }
        curTab.setText(file.getName());
        return true;
    }

    /**
     * Handler for the "Exit" menu item in the "File" menu.
     * Closes all the tabs using handleClose()
     * Returns when the user cancels exiting any tab.
     */
    public void handleExit(Event event) {
        int numTabs = tabFilepathMap.size();
        // Close each tab using handleClose()
        // Check if current number of tabs decreased by one to know if the user cancelled.
        for (int i = 0; i < numTabs; i++ ) {
            this.handleClose(event);
            if (tabFilepathMap.size() == (numTabs - i)) return;

        }
        Platform.exit();
    }

    /**
     * Creates a pop-up window which allows the user to select whether they wish to save
     * the current file or not.
     * Used by handleClose.
     *
     * @param event the tab closing event that may be consumed
     */
    private void askSaveAndClose(Event event) {
        ShowSaveOptionAlert saveOptions = new ShowSaveOptionAlert();
        Optional<ButtonType> result = saveOptions.getUserSaveDecision();

        if (result.isPresent()) {
            if (result.get() == saveOptions.getCancelButton()) {
                event.consume();
                return;
            } else if (result.get() == saveOptions.getYesButton()) {

                boolean isNotCancelled = this.handleSave();

                if(isNotCancelled) {
                    this.closeTab();
                } else {
                    event.consume();
                }
                return;
            }
            this.closeTab();
        }
    }

    /**
     * Saves the text present in the current tab to a given filename.
     * Used by handleSave, handleSaveAs.
     *
     * @param file The file object to which the text is written to.
     */
    private void writeFile(File file) {
        JavaTab curTab = (JavaTab)this.javaTabPane.getSelectionModel().getSelectedItem();
        VirtualizedScrollPane<CodeArea> scrollPane = (VirtualizedScrollPane<CodeArea>) curTab.getContent();
        CodeArea codeArea = scrollPane.getContent();
        String text = codeArea.getText();

        // use a BufferedWriter object to write out the string to a file
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            writer.write(text);
            writer.close();
        }
        catch (IOException e) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText("File Error");
            alert.setContentText("Cannot find file or file is Read-only. Please select a new file.");
            alert.showAndWait();
            return;
        }

        // update File array
//        tabFilepathMap.replace(curTab, file.getPath());
//        saveStatus.replace(curTab, true);
        this.javaTabPane.updateTabSavedStatus(curTab, true);
    }


    public void handleScan() {

        JavaTab curTab = (JavaTab)this.javaTabPane.getSelectionModel().getSelectedItem();
        String filename = this.tabFilepathMap.get(curTab);

        this.scanner = new Scanner( filename, new ErrorHandler() );

        this.handleNew(null);
        curTab = (JavaTab)this.javaTabPane.getSelectionModel().getSelectedItem();

        Token nextToken;
        while ( (nextToken = scanner.scan()).kind != Token.Kind.EOF) {
            curTab.getCodeArea().appendText(nextToken.toString()+"\n");

        }

//        ErrorHandler errorHandler = scanner.getErrorHandler();
//        for( proj9AhnDeGrawHangSlagerbantam.util.Error e : errorHandler.getErrorList()){
//
//
//        }

    }

    public List<Error> getScanningErrors() {
        return this.scanner.getErrors();
    }

    /**
     * Executes process for when a tab is closed, which is to remove the filename and saveStatus at
     * the corresponding HashMaps, and then remove the Tab object from TabPane
     *
     */
    private void closeTab() {
        //NOTE: the following three lines has to be in this order removing the tab first would
        //result in calling handleUpdateCurrentTab() because the currently selected tab will
        //change, and thus the wrong File will be removed from the HashMaps
        JavaTab curTab = (JavaTab)this.javaTabPane.getSelectionModel().getSelectedItem();
//        saveStatus.remove(curTab);
        tabFilepathMap.remove(curTab);
        javaTabPane.removeTab(curTab);
    }

    /**
     *A getter to get the hashmap that stores the tabFilepathMap
     * @return  Hashmap<Tab,String> of tabs and the tabFilepathMap of files in the tabs
     */
    public HashMap<Tab,String> getTabFilepathMap(){
        return this.tabFilepathMap;
    }
}