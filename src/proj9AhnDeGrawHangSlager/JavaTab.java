package proj9AhnDeGrawHangSlager;

import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import org.fxmisc.flowless.VirtualizedScrollPane;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class JavaTab extends Tab {
    private JavaCodeArea codeArea;


    public JavaTab(FileController fileController, ContextMenuController contextMenuController,
                   JavaTabPane tabPane, String filename, File file) {

        super(filename);

        codeArea = new JavaCodeArea(contextMenuController);

        // bind code area to method updating its saved status in the tabSavedStatusMap of the TabPane
        codeArea.setOnKeyPressed(
                (event) -> tabPane.updateTabSavedStatus(this, false));
        this.setContent(new VirtualizedScrollPane<>(codeArea,
                ScrollPane.ScrollBarPolicy.ALWAYS,
                ScrollPane.ScrollBarPolicy.ALWAYS));

        if (file != null) {
            String fileText = getFileContents(file);
            codeArea.replaceText(fileText);
        }

        this.setOnCloseRequest( (event) -> fileController.handleClose(event) );

        // enable the tab's right-click menu
        contextMenuController.setupTabContextMenuHandler(this);
    }

    /**
     *
     * @param file the file tha
     * @return
     */
    public String getFileContents(File file) {

        String content = "";
        if (file != null){

            try {
                Scanner scanner = new Scanner(file).useDelimiter("\\Z");
                if (scanner.hasNext())
                    content = scanner.next();
            }
            catch (FileNotFoundException | NullPointerException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("File Error");
                alert.setContentText("File not Found: Please select a new file.");
                alert.showAndWait();
            }
        }
        return content;
    }

    public JavaCodeArea getCodeArea(){
        return this.codeArea;
    }


}
