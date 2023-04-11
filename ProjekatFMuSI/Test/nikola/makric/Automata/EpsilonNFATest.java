package nikola.makric.Automata;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EpsilonNFATest {

    @Test
    void acceptsString() {
        EpsilonNFA automat = new EpsilonNFA();
        automat.addWholeSetOfStates(new String[]{"q0","q1","q2","q3","q4","q5"});
        automat.addStartState("q0");
        automat.addFinalState("q5");
        automat.setWholeAlphabet(new Character[]{'a','b'});
        try
        {
            automat.addTransition("q0",'a',new String[]{"q1"});
            automat.addTransition("q1",'b',new String[]{"q2"});
            automat.addTransition("q1",'ε',new String[]{"q5"});
            automat.addTransition("q2",'a',new String[]{"q1"});
            automat.addTransition("q5",'b',new String[]{"q3","q5"});
            automat.addTransition("q3",'a',new String[]{"q4"});
            automat.addTransition("q4",'b',new String[]{"q5"});
            assertTrue(automat.acceptsString("aba"));
            assertTrue(automat.acceptsString("ab"));
            assertFalse(automat.acceptsString("abaa"));
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    void eClosure() {
        EpsilonNFA automat = new EpsilonNFA();
        automat.addWholeSetOfStates(new String[]{"q0","q1","q2","q3","q4","q5","q6"});
        automat.addFinalState("q0");
        automat.addStartState("q0");
        automat.setWholeAlphabet(new Character[]{'a','b'});
        try
        {
            //ε
            automat.addTransition("q0",'b',new String[]{"q1"});
            automat.addTransition("q1",'a',new String[]{"q2"});
            automat.addTransition("q1",'b',new String[]{"q2"});
            automat.addTransition("q1",'ε',new String[]{"q5"});
            automat.addTransition("q2",'b',new String[]{"q3"});
            automat.addTransition("q2",'a',new String[]{"q5"});
            automat.addTransition("q2",'ε',new String[]{"q6"});
            automat.addTransition("q3",'a',new String[]{"q6"});
            automat.addTransition("q4",'b',new String[]{"q0"});
            automat.addTransition("q5",'a',new String[]{"q0","q4"});
            automat.addTransition("q6",'ε',new String[]{"q5"});
            Set<String> q0Set = new HashSet<>();
            q0Set.add("q0");
            Set<String> q1Set = new HashSet<>();
            q1Set.add("q1");
            Set<String> q2Set = new HashSet<>();
            q2Set.add("q2");
            var epsilonClosureq0 = automat.eClosure(q0Set);
            var epsilonClosureq1 = automat.eClosure(q1Set);
            var epsilonClosureq2 = automat.eClosure(q2Set);
            assertTrue(epsilonClosureq0.equals(q0Set));
            q1Set.add("q5");
            assertTrue(epsilonClosureq1.equals(q1Set));
            q2Set.add("q5");
            q2Set.add("q6");
            assertTrue(epsilonClosureq2.equals(q2Set));
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}