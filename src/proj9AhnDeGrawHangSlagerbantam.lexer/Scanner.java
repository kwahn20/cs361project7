package proj9AhnDeGrawHangSlagerbantam.lexer;

import proj9AhnDeGrawHangSlagerbantam.util.ErrorHandler;
import javax.xml.transform.Source;
import java.io.*;
import java.util.Set;

public class Scanner
{
    private SourceFile sourceFile;
    private ErrorHandler errorHandler;
    private Character currentChar;

    private final Set<Character> digitChars =
            Set.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');

    private final Set<Character> illegalIdentifierOrKeywordChars =
            Set.of('"', '/', '+', '-', '>', '<', '=', '&', '{',
                    '}', '[', ']', '(', ')', ';', ':', '!');


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
        currentChar = sourceFile.getNextChar();

        if (currentChar == this.sourceFile.eof) return new Token(Token.Kind.EOF,
                currentChar.toString(), this.sourceFile.getCurrentLineNumber());

        // special 'cases' are when we may be building tokens that are > 1 char long
        switch(currentChar) {

            case('"'):

            case('/'):

            case('+'):

            case('-'):

            case('>'):

            case('<'):

            case('='):

            case('&'):

            case('{'):

            case('}'):

            case('['):

            case(']'):

            case('('):

            case(')'):

            case(';'):

            case(':'):

            case('!'):

            default:

                if (digitChars.contains(currentChar)) return getIntConstToken();
                else return getIdentifierOrKeywordToken();
         }
    }

    private Token getIntConstToken() {
        return null;
    }

    private Token getIdentifierOrKeywordToken() {
        return null;
    }


}
