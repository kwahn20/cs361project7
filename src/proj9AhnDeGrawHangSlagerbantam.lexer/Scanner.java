package proj9AhnDeGrawHangSlagerbantam.lexer;

import proj9AhnDeGrawHangSlagerbantam.util.ErrorHandler;
import javax.xml.transform.Source;
import java.io.*;
import java.util.Set;
import proj9AhnDeGrawHangSlagerbantam.util.Error;


public class Scanner
{
    private SourceFile sourceFile;
    private ErrorHandler errorHandler;
    private Character currentChar;
    private boolean goToNextChar = true;

    private final Set<Character> illegalIdentifierOrKeywordChars =
            Set.of('"', '/', '+', '-', '>', '<', '=', '&', '{',
                    '}', '[', ']', '(', ')', ';', ':', '!', ' ');

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

            case('+'): return this.getPlusToken();

            case('-'): return this.getMinusToken();

            case('>'): return this.getCompareToken();

            case('<'): return this.getCompareToken();

            case('='): return this.getEqualsToken();

            case('&'): return getBinaryLogicToken();

            case('|'): return getBinaryLogicToken();

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

            case('!'): return this.getNotToken();

            case('.'): return new Token(Token.Kind.DOT,
                    currentChar.toString(), this.sourceFile.getCurrentLineNumber());

            case(','): return new Token(Token.Kind.COMMA,
                    currentChar.toString(), this.sourceFile.getCurrentLineNumber());

            default:

                if (Character.isDigit(currentChar)) return getIntConstToken();
                else return getIdentifierOrKeywordToken();
         }
    }

    /**
     *
     * @return a token of Kind.BINARYLOGIC (|| or &&) or Kind.ERROR if neither found
     */
    private Token getBinaryLogicToken() {

        Character prevChar = currentChar;
        currentChar = this.sourceFile.getNextChar();

        if (currentChar.equals(prevChar)) {
            this.goToNextChar = true;

            String spelling = prevChar.toString().concat(currentChar.toString());
            return new Token(Token.Kind.BINARYLOGIC, spelling,
                    this.sourceFile.getCurrentLineNumber());
        }
        else {
            this.goToNextChar = false;
            return new Token(Token.Kind.ERROR, currentChar.toString(),
                    this.sourceFile.getCurrentLineNumber());
        }
    }

    /**
     *
     * @return a token of Kind COMPARE, could be >, >=, <, <=
     */
    private Token getCompareToken() {
        Character prevChar = currentChar;
        currentChar = this.sourceFile.getNextChar();

        if (currentChar.equals('=')) {
            this.goToNextChar = true;
            String tokenSpelling = prevChar.toString().concat(currentChar.toString());
            return new Token(Token.Kind.COMPARE, tokenSpelling, this.sourceFile.getCurrentLineNumber());
        }
        else {
            this.goToNextChar = false;
            return new Token(Token.Kind.COMPARE, prevChar.toString(), this.sourceFile.getCurrentLineNumber());
        }
    }

    /**
     *
     * @return a token of Kind either COMPARE (if !=) or UNARYNOT (if just !)
     */
    private Token getNotToken(){
        currentChar = this.sourceFile.getNextChar();
        if (currentChar.equals('=')){
            this.goToNextChar = true;
            return new Token(Token.Kind.COMPARE,
                    "!=", this.sourceFile.getCurrentLineNumber());
        }
        else {
            this.goToNextChar = false;
            return new Token(Token.Kind.UNARYNOT,
                    currentChar.toString(), this.sourceFile.getCurrentLineNumber());
        }
    }

    /**
     *
     * @return a token of Kind PLUSMINUS
     */
    private Token getEqualsToken() {

        Character prevChar = currentChar;
        currentChar = this.sourceFile.getNextChar();

        if (currentChar.equals(prevChar)) {
            this.goToNextChar = true;

            String spelling = prevChar.toString().concat(currentChar.toString());
            return new Token(Token.Kind.COMPARE, spelling,
                    this.sourceFile.getCurrentLineNumber());
        }
        else {
            this.goToNextChar = false;
            return new Token(Token.Kind.ASSIGN, currentChar.toString(),
                    this.sourceFile.getCurrentLineNumber());
        }
    }

    /**
     *
     * @return a token of Kind PLUSMINUS
     */
    private Token getPlusToken() {

        Character prevChar = currentChar;
        currentChar = this.sourceFile.getNextChar();

        if (currentChar.equals(prevChar)) {
            this.goToNextChar = true;

            String spelling = prevChar.toString().concat(currentChar.toString());
            return new Token(Token.Kind.UNARYINCR, spelling,
                    this.sourceFile.getCurrentLineNumber());
        }
        else {
            this.goToNextChar = false;
            return new Token(Token.Kind.PLUSMINUS, currentChar.toString(),
                    this.sourceFile.getCurrentLineNumber());
        }
    }

    /**
     *
     * @return a token of Kind PLUSMINUS
     */
    private Token getMinusToken() {

        Character prevChar = currentChar;
        currentChar = this.sourceFile.getNextChar();

        if (currentChar.equals(prevChar)) {
            this.goToNextChar = true;

            String spelling = prevChar.toString().concat(currentChar.toString());
            return new Token(Token.Kind.UNARYDECR, spelling,
                    this.sourceFile.getCurrentLineNumber());
        }
        else {
            this.goToNextChar = false;
            return new Token(Token.Kind.PLUSMINUS, currentChar.toString(),
                    this.sourceFile.getCurrentLineNumber());
        }
    }

    private Token getIntConstToken() {
        String spelling = "";
        while(Character.isDigit(currentChar)){
            spelling.concat(currentChar.toString());
            currentChar = this.sourceFile.getNextChar();
        }

        this.goToNextChar = false;
        if(Integer.parseInt(spelling) < Math.pow(2,31)-1)
            return new Token(Token.Kind.INTCONST, spelling, this.sourceFile.getCurrentLineNumber());
        else
            this.errorHandler.register(Error.Kind.LEX_ERROR,
                    this.sourceFile.getFilename(), this.sourceFile.getCurrentLineNumber(),
                    "INVALID INTEGER CONSTANT");
            return new Token(Token.Kind.ERROR, currentChar.toString(),
                this.sourceFile.getCurrentLineNumber());
    }



     /**
     * @return a token of Kind.IDENTIFIER or Kind.ERROR if its an
     *
     * if it should be a keyword, it will be converted to the appropriate Kind in the
     * Token constructer
     */
    private Token getIdentifierOrKeywordToken() {
        String spelling = "";
        while(!illegalIdentifierOrKeywordChars.contains(currentChar)){

            if(Character.isLetterOrDigit(currentChar) || currentChar.equals('_')) {
                spelling.concat(currentChar.toString());
                currentChar = this.sourceFile.getNextChar();
            }
            else{
                this.errorHandler.register(Error.Kind.LEX_ERROR,
                        this.sourceFile.getFilename(), this.sourceFile.getCurrentLineNumber(),
                        "INVALID IDENTIFIER CHARACTER");

                this.goToNextChar = true;
                return new Token(Token.Kind.ERROR, currentChar.toString(),
                        this.sourceFile.getCurrentLineNumber());
            }
        }
        this.goToNextChar = false;
        return new Token(Token.Kind.IDENTIFIER, spelling, this.sourceFile.getCurrentLineNumber());
    }

    public static void main (String[] args){

    }

}
