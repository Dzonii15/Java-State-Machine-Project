package nikola.makric.Automata;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AutomataEquivalenceTest {

    @Test
    void areTheyEqual() {
        try{
            DFA automat1 = new DFA();
            DFA automat2 = new DFA();
            automat1.addWholeSetOfStates(new String[]{"q1","q2","q3"});
            automat2.addWholeSetOfStates(new String[]{"q4","q5","q6","q7"});
            automat1.addStartState("q1");
            automat2.addStartState("q4");
            automat1.addFinalState("q1");
            automat2.addFinalState("q4");
            automat1.setWholeAlphabet(new Character[]{'c','d'});
            automat2.setWholeAlphabet(new Character[]{'c','d'});
            automat1.addTransition("q1",'c',"q1");
            automat1.addTransition("q1",'d',"q2");
            automat1.addTransition("q2",'c',"q3");
            automat1.addTransition("q2",'d',"q1");
            automat1.addTransition("q3",'c',"q2");
            automat1.addTransition("q3",'d',"q3");
            automat2.addTransition("q4",'c',"q4");
            automat2.addTransition("q4",'d',"q5");
            automat2.addTransition("q5",'c',"q6");
            automat2.addTransition("q5",'d',"q4");
            automat2.addTransition("q6",'c',"q7");
            automat2.addTransition("q6",'d',"q6");
            automat2.addTransition("q7",'c',"q6");
            automat2.addTransition("q7",'d',"q4");
            assertTrue(AutomataEquivalence.areTheyEqual(automat1,automat2));
            RegularExpression re = new RegularExpression();
            re.setWholeAlphabet(new Character[]{'0','1'});
            re.setRegularExpression("0.1");
            DFA automat3 = new DFA();
            automat3.addWholeSetOfStates(new String[]{"q0","q1","q2","qdead"});
            automat3.setWholeAlphabet(new Character[]{'0','1'});
            automat3.addFinalState("q2");
            automat3.addStartState("q0");
            automat3.addTransition("q0",'0',"q1");
            automat3.addTransition("q0",'1',"qdead");
            automat3.addTransition("q1",'1',"q2");
            automat3.addTransition("q1",'0',"qdead");
            automat3.addTransition("q2",'0',"qdead");
            automat3.addTransition("q2",'1',"qdead");
            automat3.addTransition("qdead",'0',"qdead");
            automat3.addTransition("qdead",'1',"qdead");
            assertTrue(AutomataEquivalence.areTheyEqual(re,automat3));
        }catch(Exception e){e.printStackTrace();}
    }
}