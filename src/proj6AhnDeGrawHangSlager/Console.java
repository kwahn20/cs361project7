/*
 * File: Console.java
 * Names: Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * Class: CS 361
 * Project 6
 * Date: October 26, 2018
 * ---------------------------
 * Edited From: Zena Abulhab, Paige Hanssen, Kyle Slager, Kevin Zhou
 * Project 5
 * Date: October 12, 2018
 *
 */

package proj6AhnDeGrawHangSlager;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.StyleClassedTextArea;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * This class is used to support console functionality.
 * It can be used to write new lines of text to the console.
 * It can also be used to check whether user input been given,
 * and what the command string was.
 *
 * @author  Zena Abulhab, Paige Hanssen, Kyle Slager Kevin Zhou (Project 5)
 * @author  Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * @version 1.0
 * @since   10-26-2018
 *
 */
public class Console extends StyleClassedTextArea {

    private int commandStartIndex;
    private ToolbarController toolbarController;
    private String command;
    // Whether or not a user-input command has been received
    // Constructor, using StyleClassedTextArea default
    public Console(){
        super();
        this.commandStartIndex = -1;
        this.toolbarController = null;
        this.command = "";
        this.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            this.handleKeyPressed(e);
        });
        this.addEventFilter(KeyEvent.KEY_TYPED, e -> {
            this.handleKeyTyped(e);
        });
    }

    /**
     * Set the toolbarController related to this console
     * @param toolbarController The toolbarController to assign to the field.
     */
    public void setToolbarController(ToolbarController toolbarController){
        this.toolbarController = toolbarController;
    }

    /**
     * Gets the command text that the user has input to this console.
     * @return String that the user has input to the console
     */
    public String getConsoleCommand(){
        String userCommand = this.command;
        this.command = "";

        //-1 means no command is stored
        this.commandStartIndex = -1;
        return userCommand + "\n";
    }


    /**
     * Adds a new, separate line of text to this console.
     * Used in ToolbarController when printing to the console.
     * @param newLine the string to add to the new line
     */
    public void WriteLineToConsole(String newLine, String strType){
        String separator = System.getProperty("line.separator");
        int len = newLine.length();
        this.appendText(newLine);
        this.appendText(separator);
        if(strType == "ERROR"){
            this.setStyleClass(this.getText().length() - len - 1, this.getText().length(), "err");
        }
        else if(strType == "INPUT"){
            this.setStyleClass(this.getText().length() - len - 1, this.getText().length(), "inp");
        }
        else if(strType == "CONS"){
            this.setStyleClass(this.getText().length() - len - 1, this.getText().length(), "cons");
        }
        this.setStyleClass(this.getText().length(),this.getText().length(), "normal");
        this.moveTo(this.getText().length());
        this.requestFollowCaret();
    }

    /**
     * Consume all keyTyped event if it is before the commandstartindex
     * @param e the keyEvent
     */
    private void handleKeyTyped(KeyEvent e){
        if (this.getCaretPosition() < commandStartIndex) {
            e.consume();
        }
    }

    /**
     * Handles the keyPressed events in the console
     * Updates the content in the console and command stored in the field
     * The key press would not do anything if not pressed after the command start index
     * @param e the keyEvent
     */
    private void handleKeyPressed(KeyEvent e) {
        //If there are no process running consume the event and return
//        if(!toolbarController.getTaskStatus()){
//            e.consume();
//            return;
//        }

        //If there is current command stored
        if (this.commandStartIndex != -1) {
            //Change the color of the user input text to default
            this.setStyleClass(commandStartIndex, this.getText().length(), ".default");
            this.command = this.getText().substring(commandStartIndex);
        }

        //If there are no command, update the start index of the command to the end of the current text
        else if (this.command.isEmpty()) {
            this.commandStartIndex = this.getText().length();
        }

        //If the user pressed Enter
        if (e.getCode() == KeyCode.ENTER) {
            e.consume();
            //If Enter was pressed in the middle of a command append a new line to the end
            if (this.getCaretPosition() >= commandStartIndex) {
                toolbarController.setReceivedCommand(true);
                this.appendText("\n");
                this.requestFollowCaret();
            }
        }

        //If the user pressed back space.
        else if (e.getCode() == KeyCode.BACK_SPACE) {
            //If the keypress was before the start of the command, nothing would happen
            if (this.getCaretPosition() < commandStartIndex + 1) {
                e.consume();
            }
        }

        //If the user pressed delete key.
        else if (e.getCode() == KeyCode.DELETE) {
            //If the keypress was before the start of the command, nothing would happen
            if (this.getCaretPosition() < commandStartIndex){
                e.consume();
            }
        }
    }


}

