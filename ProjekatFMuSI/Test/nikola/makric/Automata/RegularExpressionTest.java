package nikola.makric.Automata;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegularExpressionTest {

    @Test
    void assureCorectness() {
        RegularExpression regExp = new RegularExpression();
        regExp.setWholeAlphabet(new Character[]{'a','b'});
        regExp.setRegularExpression("a.b*.a.(b.b.b)*");
        assertTrue(regExp.assureCorectness());
        regExp.setRegularExpression("a.b.");
        assertFalse(regExp.assureCorectness());
        regExp.setRegularExpression("((a.b)");
        assertFalse(regExp.assureCorectness());
        regExp.setRegularExpression("a**b");
        assertFalse(regExp.assureCorectness());
        regExp.setRegularExpression("a.b|a.(a*.b*)|a.b.a*.b*|a.b.a.(a*.a)*.(b*.b)");
        assertTrue(regExp.assureCorectness());
    }
}