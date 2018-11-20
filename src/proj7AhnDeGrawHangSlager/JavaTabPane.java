package proj7AhnDeGrawHangSlager;

import javafx.scene.control.Alert;
import javafx.scene.control.TabPane;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class JavaTabPane extends TabPane {

    HashMap<JavaTab, Boolean> tabSavedStatusMap;
    public JavaTabPane() {
        super();
        this.tabSavedStatusMap = new HashMap<>();
    }
    public void createNewTab(FileController fileController,
                             ContextMenuController contextMenuController, File file) {

        // determine file name
        String filename;
        String content = "";
        if (file == null) {
            filename = "Untitled-".concat( Integer.toString(this.getTabs().size()) );
        }
        else {
            filename = file.getName();
        }
        // create the new tab
        JavaTab newTab = new JavaTab(fileController, contextMenuController,
                this, filename, file);

        // add to the list of tabs
        this.getTabs().add(0, newTab);
        // focus on the new tab
        this.getSelectionModel().select(newTab);
        // add it to the map indicating that it has never been saved

        if (file == null) this.tabSavedStatusMap.put(newTab, null);
        else this.tabSavedStatusMap.put(newTab, true);

    }

    public void updateTabSavedStatus(JavaTab t, Boolean newStatus) {

        if (this.tabSavedStatusMap.containsKey(t)) {
            this.tabSavedStatusMap.replace(t, newStatus);
        }
        // else error
    }

    public boolean getTabSavedStatus(JavaTab t) {
        return this.tabSavedStatusMap.get(t);
    }

    public boolean tabIsSaved(JavaTab t) {
        System.out.println("NULL TAB: " + t);
        return this.tabSavedStatusMap.get(t);
    }

    public void removeTab(JavaTab t) {
        this.getTabs().remove(t);
        this.tabSavedStatusMap.remove(t);
    }

}