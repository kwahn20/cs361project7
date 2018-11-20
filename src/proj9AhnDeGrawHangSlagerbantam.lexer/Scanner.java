package proj9AhnDeGrawHangSlagerbantam.lexer;

import proj9AhnDeGrawHangSlagerbantam.util.CompilationException;
import proj9AhnDeGrawHangSlagerbantam.util.ErrorHandler;

import java.io.*;
import java.sql.Time;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

import proj9AhnDeGrawHangSlagerbantam.util.Error;


public class Scanner
{
    private SourceFile sourceFile;
    private ErrorHandler errorHandler;
    private Character currentChar;
    private boolean goToNextChar = true;

    private final Set<Character> illegalIdentifierOrKeywordChars =
            Set.of('"', '/', '+', '-', '>', '<', '=', '&', '{',
                    '}', '[', ']', '(', ')', ';', ':', '!', ' ',
                    '.', ',', '\r', '\n');

    public Scanner(ErrorHandler handler) {
        errorHandler = handler;
        currentChar = ' ';
        sourceFile = null;
    }

    public Scanner(String filename, ErrorHandler handler) {
        errorHandler = handler;
        currentChar = ' ';
        try {
            sourceFile = new SourceFile(filename);
        }
        catch (CompilationException e){
            throw e;
        }
    }

    public Scanner(Reader reader, ErrorHandler handler) {
        errorHandler = handler;
        sourceFile = new SourceFile(reader);
    }


    public List<Error> getErrors() {
        return this.errorHandler.getErrorList();
    }

    /* Each call of this method builds the next Token from the contents
     * of the file being scanned and returns it. When it reaches the end of the
     * file, any calls to scan() result in a Token of kind EOF.
     */
    public Token scan() {

        if (this.goToNextChar) {
            currentChar = sourceFile.getNextChar();
        }

        if (currentChar.equals(SourceFile.eof)) return new Token(Token.Kind.EOF,
                currentChar.toString(), this.sourceFile.getCurrentLineNumber());

        //gets rid of whitespace
        else if (currentChar.equals('\t') || currentChar.equals('\r')
                || currentChar.equals('\n') || currentChar.equals('\f') || currentChar.equals(' ')) {
            this.goToNextChar = true;
            return this.scan();
        }


        switch(currentChar) {

            case('*'):
                this.goToNextChar = true;
                return new Token(Token.Kind.MULDIV, currentChar.toString(),
                        this.sourceFile.getCurrentLineNumber());

            case('"'): return this.getStringConstToken();

            case('/'): return this.getCommentOrMulDivToken();

            case('+'): return this.getPlusToken();

            case('-'): return this.getMinusToken();

            case('>'): return this.getCompareToken();

            case('<'): return this.getCompareToken();

            case('='): return this.getEqualsToken();

            case('&'): return getBinaryLogicToken();

            case('|'): return getBinaryLogicToken();

            case('{'):
                this.goToNextChar = true;
                return new Token(Token.Kind.LCURLY,
                    currentChar.toString(), this.sourceFile.getCurrentLineNumber());

            case('}'):
                this.goToNextChar = true;
                return new Token(Token.Kind.RCURLY,
                    currentChar.toString(), this.sourceFile.getCurrentLineNumber());

            case('['):
                this.goToNextChar = true;
                return new Token(Token.Kind.LBRACKET,
                    currentChar.toString(), this.sourceFile.getCurrentLineNumber());

            case(']'):
                this.goToNextChar = true;
                return new Token(Token.Kind.RBRACKET,
                    currentChar.toString(), this.sourceFile.getCurrentLineNumber());

            case('('):
                this.goToNextChar = true;
                return new Token(Token.Kind.LPAREN,
                    currentChar.toString(), this.sourceFile.getCurrentLineNumber());

            case(')'):
                this.goToNextChar = true;
                return new Token(Token.Kind.RPAREN,
                    currentChar.toString(), this.sourceFile.getCurrentLineNumber());

            case(';'):
                this.goToNextChar = true;
                return new Token(Token.Kind.SEMICOLON,
                    currentChar.toString(), this.sourceFile.getCurrentLineNumber());

            case(':'):
                this.goToNextChar = true;
                return new Token(Token.Kind.COLON,
                    currentChar.toString(), this.sourceFile.getCurrentLineNumber());

            case('!'): return this.getUnaryNotOrCompareToken();

            case('.'):
                this.goToNextChar = true;
                return new Token(Token.Kind.DOT,
                    currentChar.toString(), this.sourceFile.getCurrentLineNumber());

            case(','):
                this.goToNextChar = true;
                return new Token(Token.Kind.COMMA,
                    currentChar.toString(), this.sourceFile.getCurrentLineNumber());

            default:

                if (Character.isDigit(currentChar)) return getIntConstToken();
                else if (Character.isLetter(currentChar)) return getIdentifierOrKeywordToken();
                else {
                    this.goToNextChar = true;
                    return new Token(Token.Kind.ERROR, currentChar.toString(),
                            this.sourceFile.getCurrentLineNumber());
                }
         }
    }

    /**
     *
     * @return a token of Kind.COMMENT, Kind.MULDIV or Kind.ERROR
     */
    private Token getCommentOrMulDivToken() {
        Character prevChar = currentChar;
        currentChar = this.sourceFile.getNextChar();
        switch(currentChar) {

            case('/'): return this.getSingleLineCommentToken();

            case('*'): return this.getMultilineCommentToken(prevChar);

            default:
                this.goToNextChar = false;
                return new Token(Token.Kind.MULDIV, prevChar.toString(),
                    this.sourceFile.getCurrentLineNumber());
        }
    }


    /**
     *
     * @return
     */
    private Token getSingleLineCommentToken() {

        this.goToNextChar = false;
        String commentBody = "//";
        currentChar = this.sourceFile.getNextChar();    // move to first char after //
        while (!( currentChar.equals(SourceFile.eol) ||
                currentChar.equals(SourceFile.eof) )) {

            commentBody = commentBody.concat(currentChar.toString());
            currentChar = this.sourceFile.getNextChar();
        }

        this.goToNextChar = currentChar.equals(SourceFile.eol);

        return new Token(Token.Kind.COMMENT, commentBody,
                this.sourceFile.getCurrentLineNumber());
    }

    /**
     *
     * @return a token of Kind.COMMENT or Kind.ERROR
     */
    private Token getMultilineCommentToken(Character prevChar) {

        String commentBody = "/*";

        // move prevChar and currentChar past the "/*"
        for (int i = 0; i < 2; i++) {
            prevChar = currentChar;
            currentChar = this.sourceFile.getNextChar();
        }

        boolean commentTerminated = false;

        while (!commentTerminated) {

            commentBody = commentBody.concat(prevChar.toString());
            if (currentChar.equals(SourceFile.eof)) {

                this.goToNextChar = false;
                this.errorHandler.register(Error.Kind.LEX_ERROR,
                        this.sourceFile.getFilename(),
                        this.sourceFile.getCurrentLineNumber(),
                        "UNTERMINATED BLOCK COMMENT");

                return new Token(Token.Kind.ERROR,
                        commentBody.concat(currentChar.toString()),
                        this.sourceFile.getCurrentLineNumber());
            }

            else if (prevChar.equals('*') && currentChar.equals('/'))
                commentTerminated = true;

            prevChar = currentChar;
            currentChar = this.sourceFile.getNextChar();
        }
        this.goToNextChar = true;
        return new Token(Token.Kind.COMMENT, commentBody,
                this.sourceFile.getCurrentLineNumber());
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
    private Token getUnaryNotOrCompareToken(){
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
            return new Token(Token.Kind.ASSIGN, prevChar.toString(),
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
            return new Token(Token.Kind.PLUSMINUS, prevChar.toString(),
                    this.sourceFile.getCurrentLineNumber());
        }
    }

    /**
     * Creates a minus token or a unary decrement token
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
            return new Token(Token.Kind.PLUSMINUS, prevChar.toString(),
                    this.sourceFile.getCurrentLineNumber());
        }
    }

    /**
     * Returns an integer constant token, where the integer
     * value does not exceed (2^31 -1)
     * @return integer constant token
     */
    private Token getIntConstToken() {
        String spelling = "";
        while(Character.isDigit(currentChar)){
            spelling = spelling.concat(currentChar.toString());
            currentChar = this.sourceFile.getNextChar();
        }

        this.goToNextChar = false;
        try {
            if (Integer.parseInt(spelling) < Math.pow(2, 31) - 1)
                return new Token(Token.Kind.INTCONST, spelling, this.sourceFile.getCurrentLineNumber());
        }
        catch (NumberFormatException e){
            this.errorHandler.register(Error.Kind.LEX_ERROR,
                    this.sourceFile.getFilename(), this.sourceFile.getCurrentLineNumber(),
                    "INVALID INTEGER CONSTANT");
        }
        return new Token(Token.Kind.ERROR, spelling,
                this.sourceFile.getCurrentLineNumber());
    }


    /**
     * Returns a identifier or keyword token
     * if it should be a keyword, it will be converted to the appropriate Kind in the
     * Token constructor
     *
     * @return a token of Kind.IDENTIFIER or Kind.ERROR if its an invalid character
     */
    private Token getIdentifierOrKeywordToken() {
        String spelling = "";
        while(!illegalIdentifierOrKeywordChars.contains(currentChar)){

            if(Character.isLetterOrDigit(currentChar) || currentChar.equals('_')) {
                spelling = spelling.concat(currentChar.toString());
                currentChar = this.sourceFile.getNextChar();
            }
            else{
                this.errorHandler.register(Error.Kind.LEX_ERROR,
                        this.sourceFile.getFilename(), this.sourceFile.getCurrentLineNumber(),
                        "UNSUPPORTED IDENTIFIER CHARACTER");

                this.goToNextChar = true;
                spelling= spelling.concat(currentChar.toString());
                return new Token(Token.Kind.ERROR, spelling,
                        this.sourceFile.getCurrentLineNumber());
            }
        }

        this.goToNextChar = false;
        return new Token(Token.Kind.IDENTIFIER, spelling, this.sourceFile.getCurrentLineNumber());
    }

    /**
     * Returns a string constant token ensuring that
     * no strings are over 5000 characters
     *
     * @return string constant token
     */
    private Token getStringConstToken() {
        String spelling = "";
        spelling = spelling.concat(currentChar.toString());
        currentChar = this.sourceFile.getNextChar();
        //while the quote is unmatched continue getting chars
        while(!currentChar.equals('"')){

            //if you've reached an eof or a new line in a string, throws error
            if(currentChar.equals(SourceFile.eof) || currentChar.equals('\n')){
                this.errorHandler.register(Error.Kind.LEX_ERROR,
                        this.sourceFile.getFilename(), this.sourceFile.getCurrentLineNumber(),
                        "UNCLOSED QUOTE");
                this.goToNextChar = false;
                return new Token(Token.Kind.ERROR, spelling,
                        this.sourceFile.getCurrentLineNumber());
            }

            //otherwise add on to the string
            spelling = spelling.concat(currentChar.toString());
            currentChar = this.sourceFile.getNextChar();
        }

        //add on end quote
        spelling = spelling.concat(currentChar.toString());
        this.goToNextChar = true;

        //makes sure the string is less than 5000 chars
        if(spelling.length()<5000) {
            return new Token(Token.Kind.STRCONST, spelling, this.sourceFile.getCurrentLineNumber());
        }
        else{
            this.errorHandler.register(Error.Kind.LEX_ERROR,
                    this.sourceFile.getFilename(), this.sourceFile.getCurrentLineNumber(),
                    "STRING EXCEEDS MAX CHAR LENGTH 5000");
            this.goToNextChar = false;
            return new Token(Token.Kind.ERROR, spelling,
                    this.sourceFile.getCurrentLineNumber());
        }
    }


    public static void main (String[] args){
        if(args.length > 1){
            for(int i = 0; i< args.length; i ++){
                Scanner scanner;
                try {
                   scanner = new Scanner(args[i], new ErrorHandler());
                }
                catch(CompilationException e){
                    System.out.println(e);
                    continue;
                }

                Token nextToken;
                while ( (nextToken = scanner.scan()).kind != Token.Kind.EOF) {
                    System.out.println(nextToken);
                }

                if(scanner.getErrors().size() > 0){
                    System.out.println("Scanning of " + args[i] + " was not successful. "+
                            scanner.getErrors().size() +" errors were found.\n\n");
                }
                else{

                    System.out.println("Scanning of " + args[i] + " was successful. " +
                            "No errors were found.\n\n");
                }

            }
        }

    }

}