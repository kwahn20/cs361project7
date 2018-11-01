/*
 * File: EditController.java
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

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.NavigationActions.SelectionPolicy;

import java.util.regex.Pattern;
import java.util.Stack;

import java.util.ArrayList;


/**
 * This is the controller class for all of the edit functions
 * within the edit menu.
 *
 * @author Zena Abulhab, Paige Hanssen, Kyle Slager Kevin Zhou (Project 5)
 * @author Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * @version 3.0
 * @since 10-26-2018
 */
public class EditController {

    // Reference to the tab pane of the IDE
    private TabPane tabPane;
    private TextField findTextEntry;
    // fields relating to string finding
    private String fileTextSearched;
    private ArrayList<Integer> matchStartingIndices;
    private int curMatchLength;
    private int curMatchHighlightedIdx;
    private Button prevMatchBtn;
    private Button nextMatchBtn;
    private TextField replaceTextEntry;


    private String[] lines;
    private int caretIdxStart;
    private int caretIdxEnd;
    private String selectedText;



    /**
     * Constructor for the class. Initializes
     * the current tab to null
     */
    public EditController(TabPane tabPane, TextField findTextEntry, Button prevMatchBtn,
                          Button nextMatchBtn, TextField replaceTextEntry) {
        this.tabPane = tabPane;
        this.findTextEntry = findTextEntry;
        this.matchStartingIndices = new ArrayList<>();
        this.prevMatchBtn = prevMatchBtn;
        this.nextMatchBtn = nextMatchBtn;
        this.replaceTextEntry = replaceTextEntry;
        this.resetFindMatchingStringData();
    }

    /**
     * Handler for the "Undo" menu item in the "Edit" menu.
     */
    @FXML
    public void handleUndo() {
        getCurJavaCodeArea().undo();
    }

    /**
     * Handler for the "Redo" menu item in the "Edit" menu.
     */
    @FXML
    public void handleRedo() {
        getCurJavaCodeArea().redo();
    }

    /**
     * Handler for the "Cut" menu item in the "Edit" menu.
     */
    @FXML
    public void handleCut() {
        getCurJavaCodeArea().cut();
    }

    /**
     * Handler for the "Copy" menu item in the "Edit" menu.
     */
    @FXML
    public void handleCopy() { getCurJavaCodeArea().copy();
    }

    /**
     * Handler for the "Paste" menu item in the "Edit" menu.
     */
    @FXML
    public void handlePaste() { getCurJavaCodeArea().paste();
    }

    /**
     * Handler for the "SelectAll" menu item in the "Edit" menu.
     */
    @FXML
    public void handleSelectAll() {
        getCurJavaCodeArea().selectAll();
    }




    /**
     * if a single "{", "}", "[", "]", "(", ")" is highlighted, this will attempt to find
     * the matching opening or closing character and if successful, will highlight the
     * text in between the matching set of {}, [], or (),
     * otherwise will display an appropriate error message
     */
    public void handleMatchBracketOrParen() {

        // get in-focus code area
        JavaCodeArea curJavaCodeArea = getCurJavaCodeArea();

        // get any highlighted text in the code area
        String highlightedText = curJavaCodeArea.getSelectedText();

        if (highlightedText.isEmpty()) {
            showAlert("Please Highlight a Bracket!");
            return;
        } else if (highlightedText.length() == 1) {

            // true if matching a closing character to an opening character,
            // false if matching an opening character to a closing character
            Boolean findClosingCharacter;

            if (highlightedText.equals("{") || highlightedText.equals("[")
                    || highlightedText.equals("(")) {
                findClosingCharacter = true;
            } else if (highlightedText.equals("}") || highlightedText.equals("]")
                    || highlightedText.equals(")")) {
                findClosingCharacter = false;
            } else {
                showAlert("VALID CHARACTER NOT HIGHLIGHTED\n" +
                        "VALID CHARACTERS ARE '{', '}', '[', ']', '(' or ')'");
                return;
            }

            // save length of whole file
            int fileTextLength = curJavaCodeArea.getLength();


            // this stack holds only opening "[","(","{" or closing "]",")","}" characters
            // depending which type was initially highlighted to match against
            // start with initial highlighted bracket/parenthesis/brace on the stack
            Stack<String> charStack = new Stack<>();
            charStack.push(highlightedText);

            // get the indices of the highlighted character within the file
            IndexRange highlightedCharRange = curJavaCodeArea.getSelection();

            if (findClosingCharacter) {

                String openingMatchCharacter;

                // search forward through file
                int idxAfterCharToMatch = highlightedCharRange.getEnd();
                for (int i = idxAfterCharToMatch; i < fileTextLength; i++) {

                    // get the opening char on top of stack
                    openingMatchCharacter = charStack.peek();

                    // current character being checked for a closing bracket match
                    String curChar = curJavaCodeArea.getText(i, i + 1);

                    // check that the character is not not written as a string "(" or '('
                    try {
                        if (curJavaCodeArea.getText(i - 1, i).equals("\"")
                                && curJavaCodeArea.getText(i + 1, i + 2).equals("\"")
                                || curJavaCodeArea.getText(i - 1, i).equals("'")
                                && curJavaCodeArea.getText(i + 1, i + 2).equals("'")) {
                            continue;
                        }
                    } catch (IndexOutOfBoundsException e) {
                        throw(e);
                    }
                    /* pop the top opening char off the stack if its closing match is found,
                     * otherwise push the newly found opening char onto the stack */
                    switch (curChar) {
                        case ("]"):
                            if (openingMatchCharacter.equals("[")) charStack.pop();
                            break;
                        case (")"):
                            if (openingMatchCharacter.equals("(")) charStack.pop();
                            break;
                        case ("}"):
                            if (openingMatchCharacter.equals("{")) charStack.pop();
                            break;
                        case ("["):
                            charStack.push(curChar);
                            break;
                        case ("("):
                            charStack.push(curChar);
                            break;
                        case ("{"):
                            charStack.push(curChar);
                            break;
                        default:
                            break;
                    }
                    // stack is empty if the originally highlighted character has been
                    /// matched with the current character
                    if (charStack.isEmpty()) {
                        // highlight between matching characters ({}, () or [])
                        curJavaCodeArea.selectRange(idxAfterCharToMatch, i);
                        return;
                    }
                }
                showAlert("MATCHING CLOSING CHARACTER NOT FOUND");
                return;
            } else {
                String closingMatchCharacter;
                int idxBeforeCharToMatch = highlightedCharRange.getStart();
                // search backward through file
                for (int i = idxBeforeCharToMatch; i > 0; i--) {

                    // get closing character on top of the stack
                    closingMatchCharacter = charStack.peek();

                    // check that the character is not not written as a string "(" or '('
                    try {
                        if (curJavaCodeArea.getText(i - 2, i - 1).equals("\"")
                                && curJavaCodeArea.getText(i, i + 1).equals("\"")
                                || curJavaCodeArea.getText(i - 2, i - 1).equals("'")
                                && curJavaCodeArea.getText(i, i + 1).equals("'")) {
                            continue;
                        }
                    } catch (IndexOutOfBoundsException e) {
                        throw(e);
                    }

                    // pop the top opening char off the stack if its closing match is found,
                    // otherwise push the newly found opening char onto the stack
                    // current character being checked for a closing bracket match
                    String curChar = curJavaCodeArea.getText(i - 1, i);

                    switch (curChar) {
                        case ("["):
                            if (closingMatchCharacter.equals("]")) charStack.pop();
                            break;
                        case ("("):
                            if (closingMatchCharacter.equals(")")) charStack.pop();
                            break;
                        case ("{"):
                            if (closingMatchCharacter.equals("}")) charStack.pop();
                            break;
                        case ("]"):
                            charStack.push(curChar);
                            break;
                        case (")"):
                            charStack.push(curChar);
                            break;
                        case ("}"):
                            charStack.push(curChar);
                            break;
                        default:
                            break;
                    }
                    // stack is empty if the originally highlighted character has been
                    /// matched with the current character in the file
                    if (charStack.isEmpty()) {
                        // highlight between matching characters ({}, () or [])
                        curJavaCodeArea.selectRange(i, idxBeforeCharToMatch);
                        return;
                    }
                }
                showAlert("MATCHING OPENING CHARACTER NOT FOUND");
                return;
            }
        } else {
            showAlert("VALID CHARACTERS ARE A SINGLE '{', '}', '[', ']', '(' or ')'");
        }

    }

    /**
     * creates and displays an informational alert
     *
     * @param header the content of the alert
     */
    private void showAlert(String header) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(header);
        a.show();
    }

    /**
     * comments out the line that the cursor is one if it uncommented,
     * undoes a "layer" of commenting (pair of forward slashes "//") if there >= one
     */
    public void toggleSingleLineComment() {

        JavaCodeArea curCodeArea = getCurJavaCodeArea();

        // position caret at start of line
        curCodeArea.lineStart(SelectionPolicy.ADJUST);

        // get caret index location in file
        int caretIdx = curCodeArea.getCaretPosition();

        // temporarily highlight the current line to get its text as a string
        curCodeArea.selectLine();
        String curLineText = curCodeArea.getSelectedText();
        curCodeArea.deselect();

        // regex to check if current line is commented
        if (Pattern.matches(" *\\/\\/.*", curLineText)) {

            // uncomment the line by taking out the first instance of "//"
            String curLineUncommented =
                    curLineText.replaceFirst("//", "");

            // replace the current line with the newly commented line
            curCodeArea.replaceText(caretIdx, caretIdx + curLineText.length(),
                    curLineUncommented);

            return;
        }

        // add a "//" at the beginning of the line to comment it out
        curCodeArea.replaceText(caretIdx, caretIdx, "//");
    }



    private void incrementCaretIdx(CodeArea curCodeArea){
        curCodeArea.moveTo(curCodeArea.getCurrentParagraph()+1, 0);
        caretIdxStart = curCodeArea.getCaretPosition();
    }

    private void getFullSelectedText(JavaCodeArea curCodeArea){
        if (selectedText.equals("")) {
            curCodeArea.selectLine();
            selectedText = curCodeArea.getSelectedText();
            curCodeArea.deselect();
            curCodeArea.lineStart(SelectionPolicy.ADJUST);
            caretIdxStart = curCodeArea.getCaretPosition();
            lines = selectedText.split("\\n");

        } else {

            //curCodeArea.lineStart(SelectionPolicy.ADJUST);
            IndexRange highlightedRange = curCodeArea.getSelection();

            //moves caret to the front of the selected text
            curCodeArea.moveTo(highlightedRange.getStart());

            // move to front of first highlighted line
            curCodeArea.lineStart(SelectionPolicy.EXTEND);

            // get current caret position
            caretIdxStart = curCodeArea.getCaretPosition();

            //moves to the end of the first highlighted selection
            curCodeArea.moveTo(highlightedRange.getEnd());

            //moves to the end of that line
            curCodeArea.lineEnd(SelectionPolicy.ADJUST);

            //grabs that caret idx
            caretIdxEnd = curCodeArea.getCaretPosition();

            // highlight from beginning of first highlighted line to end of highlighted section
            curCodeArea.selectRange(caretIdxStart, caretIdxEnd);

            // get all the highlighted text
            selectedText = curCodeArea.getSelectedText();

            // get list of individual string lines
            lines = selectedText.split("\\n");
        }

    }

    /**
     * comments out the line that the cursor is one if it uncommented,
     * undoes a "layer" of commenting (pair of forward slashes "//") if there >= one
     */
    public void handleCommenting(){
        JavaCodeArea curCodeArea = getCurJavaCodeArea();

        selectedText = curCodeArea.getSelectedText();
        getFullSelectedText(curCodeArea);

        for (int i = 0; i < lines.length; i++) {

            String curLineText = lines[i];

            // regex to check if current line is commented
            if (Pattern.matches(" *[ \\t]*\\/\\/.*", curLineText)) {

                // uncomment the line by taking out the first instance of "//"
                String curLineUncommented =
                        curLineText.replaceFirst("//", "");

                // replace the current line with the newly commented line
                curCodeArea.replaceText(caretIdxStart, caretIdxStart + curLineText.length(),
                        curLineUncommented);
            }
            else {
                // add a "//" at the beginning of the line to comment it out
                curCodeArea.replaceText(caretIdxStart, caretIdxStart, "//");
            }

            incrementCaretIdx(curCodeArea);

        }

    }


    /**
     * Tabs the selected text
     */

    public void handleTabbing() {
        JavaCodeArea curCodeArea = getCurJavaCodeArea();

        selectedText = curCodeArea.getSelectedText();
        getFullSelectedText(curCodeArea);

        for (int i = 0; i < lines.length; i++) {
            singleLineTabbing(caretIdxStart);
            incrementCaretIdx(curCodeArea);

        }
    }


    /**
     * Untabs the selected text
     */

    public void handleUnTabbing() {
        JavaCodeArea curCodeArea = getCurJavaCodeArea();

        selectedText = curCodeArea.getSelectedText();
        getFullSelectedText(curCodeArea);

        for (int i = 0; i < lines.length; i++) {
            String curLineText = lines[i];
            singleLineUnTabbing(curLineText, caretIdxStart);
            incrementCaretIdx(curCodeArea);
        }
    }

    /**
     * Tabs a single line
     */
    private void singleLineTabbing(int caretIdx) {
        JavaCodeArea curCodeArea = getCurJavaCodeArea();
        curCodeArea.replaceText(caretIdx, caretIdx, "\t");
    }

    /**
     * Untabs a single line
     */
    private void singleLineUnTabbing(String curLineText, int caretIdx) {

        JavaCodeArea curCodeArea = getCurJavaCodeArea();

        // regex to check if current line is tabbed
        if (Pattern.matches("(?:[ \\t].*)", curLineText)) {
            // detabs the line by taking out the first instance of a tab
            String curLineUnTabbed = curLineText.replaceFirst("[ \\t]", "");

            curCodeArea.moveTo(caretIdx);
            curCodeArea.lineStart(SelectionPolicy.ADJUST);
            caretIdx = curCodeArea.getCaretPosition();

            // replace the current line with the newly commented line
            curCodeArea.replaceText(caretIdx, caretIdx + curLineText.length(),
                    curLineUnTabbed);
            return;
        }
    }


    /**
     * searches for the text entered in the "Find" TextField
     * shows appropriate error message if nothing found or provided as search string
     * enables the Previous and Next buttons if more than one match is found
     */
    public void handleFindText(Boolean showNumMatchesAlert) {

        JavaCodeArea curJavaCodeArea = getCurJavaCodeArea();
        if (curJavaCodeArea == null) {
            showAlert("NO FILES OPEN");
            resetFindMatchingStringData();
            return;
        }

        String textToFind = this.findTextEntry.getText();
        int textToFindLength = textToFind.length();

        // check if some text was searched for
        if (textToFindLength > 0) {

            // get current file's text
            String openFileText = curJavaCodeArea.getText();

            // get index of first match, -1 if no matches
            int index = openFileText.indexOf(textToFind);

            // check if any match was found
            if (index != -1) {

                // build list of starting match indices
                this.matchStartingIndices.clear();
                while (index >= 0) {
                    this.matchStartingIndices.add(index);
                    index = openFileText.indexOf(textToFind, index + 1);

                }

                // save text of searched file
                this.fileTextSearched = openFileText;

                // first match is at the first index of the match starting indices array
                this.curMatchHighlightedIdx = 0;

                // save length of valid match
                this.curMatchLength = textToFindLength;

                // get starting index in file of first found match
                int highlightStartIdx = this.matchStartingIndices.get(0);

                // highlight first found match
                curJavaCodeArea.selectRange(highlightStartIdx,
                        highlightStartIdx+this.curMatchLength);

                if (showNumMatchesAlert) {
                    // notify the user of search results
                    showAlert(this.matchStartingIndices.size() + " MATCHES FOUND");
                }

                // enable the Previous and Next buttons if more than 1 match is found
                if (this.matchStartingIndices.size() > 1) {
                    this.setMatchNavButtonsClickable(true);
                }
                else this.setMatchNavButtonsClickable(false);

                return;
            }
            resetFindMatchingStringData();
            showAlert("NO MATCH FOUND");
            return;
        }
        resetFindMatchingStringData();
        showAlert("NOTHING TO SEARCH FOR");
    }


    /**
     * highlights the match preceding the currently highlighted match if there are
     * multiple matches found in the file
     */
    public void handleHighlightPrevMatch() {

        if (this.canHighlightMatches()) {

            JavaCodeArea curJavaCodeArea = getCurJavaCodeArea();
            if (curJavaCodeArea == null) {
                showAlert("NO FILES OPEN");
                return;
            }

            // if first match highlighted, highlight the last match
            if (this.curMatchHighlightedIdx == 0) {

                // get index of match located last in file
                int highlightStartIdx = this.matchStartingIndices.get(
                        this.matchStartingIndices.size()-1);

                // highlight this last match
                curJavaCodeArea.selectRange(highlightStartIdx,
                        highlightStartIdx+this.curMatchLength);

                // update the index of the currently highlighted match
                this.curMatchHighlightedIdx = this.matchStartingIndices.size()-1;
            }
            // otherwise highlight the previous match
            else {
                // decrement index of highlighted match
                this.curMatchHighlightedIdx--;

                // get starting index in file of preceding match
                int highlightStartIdx = this.matchStartingIndices.get(
                        this.curMatchHighlightedIdx);

                // highlight match preceding currently highlighted match
                curJavaCodeArea.selectRange( highlightStartIdx,
                        highlightStartIdx+this.curMatchLength);
            }
        }
    }

    /**
     * Highlights the next matched word available
     */
    public void handleHighlightNextMatch() {

        if (this.canHighlightMatches()) {

            JavaCodeArea curJavaCodeArea = getCurJavaCodeArea();
            if (curJavaCodeArea == null) {
                showAlert("NO FILES OPEN");
                return;
            }

            // if last match in file highlighted, wrap around to highlight the first match
            if (this.curMatchHighlightedIdx == this.matchStartingIndices.size()-1) {
                // get index of match located last in file

                int highlightStartIdx = this.matchStartingIndices.get(0);
                // highlight the match located first in the file
                curJavaCodeArea.selectRange(highlightStartIdx,
                        highlightStartIdx+this.curMatchLength);

                // update the index of the currently highlighted match
                this.curMatchHighlightedIdx = 0;
            }
            // otherwise highlight the previous match
            else {
                // increment index of highlighted match
                this.curMatchHighlightedIdx++;

                // get starting index in file of next match
                int highlightStartIdx = this.matchStartingIndices.get(
                        this.curMatchHighlightedIdx);

                // highlight match after currently highlighted match
                curJavaCodeArea.selectRange(highlightStartIdx,
                        highlightStartIdx+this.curMatchLength);
            }
        }
    }

    /**
     *
     * @return true if any matches from Find can currently be highlighted, else false
     */
    private boolean canHighlightMatches() {
        JavaCodeArea curJavaCodeArea = getCurJavaCodeArea();
        if (curJavaCodeArea == null) {
            showAlert("NO FILES OPEN");
            return false;
        }
        String openFileText = curJavaCodeArea.getText();

        // check if anything searched for
        if (this.fileTextSearched == null || this.curMatchHighlightedIdx == -1
                || this.curMatchLength == -1) {
            showAlert("MUST FIND MATCHING TEXT");
            return false;
        }
        // check if any matches found
        if (this.matchStartingIndices.size() == 0) {
            showAlert("NO MATCHES FOUND");
            return false;
        }
        // check if the file has been changed since the last search
        if (!this.fileTextSearched.equals(openFileText)) {
            showAlert("FILE HAS BEEN CHANGED SINCE PREVIOUS SEARCH, FIND AGAIN");
            setMatchNavButtonsClickable(false);
            return false;
        }
        return true;
    }

    /**
     * resets the fields used for string searching in the file when no match is found
     */
    private void resetFindMatchingStringData() {
        this.fileTextSearched = null;
        this.curMatchLength = -1;
        this.curMatchHighlightedIdx = -1;
        this.setMatchNavButtonsClickable(false);
    }

    /**
     * enables or disables the Previous and Next match navigation buttons
     * @param enable boolean denoting whether or not the Previous & Next buttons
     *               are enabled
     */
    private void setMatchNavButtonsClickable(boolean enable) {
        this.prevMatchBtn.setDisable(!enable);
        this.nextMatchBtn.setDisable(!enable);
    }

    /**
     * if there is a highlighted match, this will replace it with the text from the
     * Replace text entry
     */
    public void handleReplaceText() {


        // check that there were matches & the file has not been changed since last search
        if (this.canHighlightMatches()) {

            String textToReplaceMatch = this.replaceTextEntry.getText();

            // check that there is some text in the replace text entry
            if (textToReplaceMatch.length() > 0) {


                    // get idx of currently highlighted match
                int curHighlightedMatchStartingIdx =
                        this.matchStartingIndices.get(this.curMatchHighlightedIdx);


                // replace current highlighted mach with the replaced text
                getCurJavaCodeArea().replaceText(curHighlightedMatchStartingIdx,
                        curHighlightedMatchStartingIdx+this.curMatchLength,
                        textToReplaceMatch);
                /* call find method to update the indices of the found matches
                 * to account for the changed text */
                this.handleFindText(false);
                return;
            }
            showAlert("ENTER REPLACEMENT TEXT");
        }
    }

    /**
     * @return the JavaCodeArea currently in focus of the TabPane
     */
    public JavaCodeArea getCurJavaCodeArea() {

        if (this.tabPane.getTabs().size() == 0) return null;

        Tab curTab = this.tabPane.getSelectionModel().getSelectedItem();
        VirtualizedScrollPane<CodeArea> curPane =
                (VirtualizedScrollPane<CodeArea>) curTab.getContent();
        return (JavaCodeArea) curPane.getContent();
    }
}