package proj9AhnDeGrawHangSlagerbantam.lexer;

import proj9AhnDeGrawHangSlagerbantam.util.ErrorHandler;
import javax.xml.transform.Source;
import java.io.*;

public class Scanner
{
    private SourceFile sourceFile;
    private ErrorHandler errorHandler;
    private Character currentChar;


    public Scanner(ErrorHandler handler) {
        errorHandler = handler;
        currentChar = ' ';
        sourceFile = null;
    }

    public Scanner(String filename, ErrorHandler handler) {
        errorHandler = handler;
        currentChar = ' ';
        sourceFile = new SourceFile(filename);
    }

    public Scanner(Reader reader, ErrorHandler handler) {
        errorHandler = handler;
        sourceFile = new SourceFile(reader);
    }

    /* Each call of this method builds the next Token from the contents
     * of the file being scanned and returns it. When it reaches the end of the
     * file, any calls to scan() result in a Token of kind EOF.
     */
    public Token scan()
    {

        char eofToken = this.sourceFile.eof;

        while ( !(this.currentChar = this.sourceFile.getNextChar()).equals(eofToken))  {
            System.out.println(this.currentChar);

        }

        return null;  // REMOVE THIS LINE AND REPLACE IT WITH YOUR CODE
    }

}
