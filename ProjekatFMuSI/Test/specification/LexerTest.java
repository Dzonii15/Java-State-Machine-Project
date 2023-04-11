package specification;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {

    @Test
    void lexAnalyze() {
        Lexer lex1 = new Lexer("test.txt");
        var listaTokena = lex1.LexAnalyze();
        assertTrue(listaTokena!=null);
        assertTrue(listaTokena.size()==4);
        Lexer lex2 = new Lexer("test2.txt");
        var listaTokena2 = lex2.LexAnalyze();
        assertTrue(listaTokena2!=null);
        Lexer lex3 = new Lexer("test3.txt");
        var listaTokena3 = lex3.LexAnalyze();
        assertTrue(listaTokena3==null);
    }
}