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
    private boolean goToNextChar = true;

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
        if (this.goToNextChar) currentChar = sourceFile.getNextChar();


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
                currentChar = this.sourceFile.getNextChar();

                if (currentChar.equals('=')) {
                    this.goToNextChar = true;
                    return new Token(Token.Kind.COMPARE,
                            "==", this.sourceFile.getCurrentLineNumber());
                }

                else {
                    this.goToNextChar = false;
                    return new Token(Token.Kind.LCURLY,
                            "=", this.sourceFile.getCurrentLineNumber());
                }

            case('&'):

            case('{'): return new Token(Token.Kind.LCURLY,
                    currentChar.toString(), this.sourceFile.getCurrentLineNumber());

            case('}'): return new Token(Token.Kind.RCURLY,
                    currentChar.toString(), this.sourceFile.getCurrentLineNumber());

            case('['): return new Token(Token.Kind.LBRACKET,
                    currentChar.toString(), this.sourceFile.getCurrentLineNumber());

            case(']'): return new Token(Token.Kind.RBRACKET,
                    currentChar.toString(), this.sourceFile.getCurrentLineNumber());

            case('('): return new Token(Token.Kind.LPAREN,
                    currentChar.toString(), this.sourceFile.getCurrentLineNumber());

            case(')'): return new Token(Token.Kind.RPAREN,
                    currentChar.toString(), this.sourceFile.getCurrentLineNumber());

            case(';'): return new Token(Token.Kind.SEMICOLON,
                    currentChar.toString(), this.sourceFile.getCurrentLineNumber());

            case(':'): return new Token(Token.Kind.COLON,
                    currentChar.toString(), this.sourceFile.getCurrentLineNumber());

            case('!'): return new Token(Token.Kind.UNARYNOT,
                    currentChar.toString(), this.sourceFile.getCurrentLineNumber());

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
