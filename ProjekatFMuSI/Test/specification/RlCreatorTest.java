package specification;

import nikola.makric.Automata.Convertable;
import nikola.makric.Automata.EpsilonNFA;
import nikola.makric.Automata.RegularExpression;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RlCreatorTest {

    @Test
    void returnRLR() {
        Lexer lex1 = new Lexer("test.txt");
        var listaTokena = lex1.LexAnalyze();
        RlCreator rlCreator = new RlCreator(listaTokena);
        Convertable representation = rlCreator.returnRLR();
        Lexer lex2 = new Lexer("test2.txt");
        var listaTokena2 = lex2.LexAnalyze();
        RlCreator rlCreator2 = new RlCreator(listaTokena2);
        Convertable representation2 = rlCreator2.returnRLR();
        assertTrue(representation instanceof RegularExpression);
        assertTrue(representation2 instanceof EpsilonNFA);
    }
}