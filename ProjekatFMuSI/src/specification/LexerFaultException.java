package specification;

public class LexerFaultException extends Exception{
    public LexerFaultException()
    {
        super("Lexer fault detected");
    }
    public LexerFaultException(String msg)
    {
        super(msg);
    }
}
