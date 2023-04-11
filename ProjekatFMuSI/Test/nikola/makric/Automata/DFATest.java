package nikola.makric.Automata;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DFATest {

    @Test
    void acceptsString() {
        //automat koji prihvata paran broj a
        DFA automat1 = new DFA();
        automat1.addWholeSetOfStates(new String[]{"q0","q1"});
        automat1.addStartState("q0");
        automat1.addFinalState("q0");
        automat1.addToAlphabet('a');
        automat1.addToAlphabet('b');
        try {
            automat1.addTransition("q0",'a',"q1");
            automat1.addTransition("q0",'b',"q0");
            automat1.addTransition("q1",'a',"q0");
            automat1.addTransition("q1",'b',"q1");
            assertTrue(automat1.acceptsString("aa"));
            assertFalse(automat1.acceptsString("ab"));
            assertTrue(automat1.acceptsString("ε"));
            assertTrue(automat1.acceptsString("aaaabbb"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        //automat koji prihavata rijec sa sekvencom 101 u sebi
        DFA automat2 = new DFA();
        automat2.addWholeSetOfStates(new String[]{"q0","q1","q2","q3"});
        automat2.addStartState("q0");
        automat2.addFinalState("q3");
        automat2.setWholeAlphabet(new Character[]{'1','0'});
        try
        {
            automat2.addTransition("q0",'1',"q1");
            automat2.addTransition("q0",'0',"q0");
            automat2.addTransition("q1",'1',"q1");
            automat2.addTransition("q1",'0',"q2");
            automat2.addTransition("q2",'1',"q3");
            automat2.addTransition("q2",'0',"q0");
            automat2.addTransition("q3",'1',"q3");
            automat2.addTransition("q3",'0',"q3");
            assertTrue(automat2.acceptsString("101"));
            assertFalse(automat2.acceptsString("1001"));
            assertTrue(automat2.acceptsString("0010001010"));
            assertFalse(automat2.acceptsString("ε"));
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        //automat koji prihvata samo rijec 111
        DFA automat3 = new DFA();
        automat3.addWholeSetOfStates(new String[]{"q0","q1","q2","q3","qReject"});
        automat3.setWholeAlphabet(new Character[]{'1','0'});
        automat3.addStartState("q0");
        automat3.addFinalState("q3");
        try {
            automat3.addTransition("q0", '1', "q1");
            automat3.addTransition("q0",'0',"qReject");
            automat3.addTransition("q1", '1', "q2");
            automat3.addTransition("q1",'0',"qReject");
            automat3.addTransition("q2", '1', "q3");
            automat3.addTransition("q2",'0',"qReject");
            automat3.addTransition("q3", '1', "qReject");
            automat3.addTransition("q3",'0',"qReject");
            automat3.addTransition("qReject", '1', "qReject");
            automat3.addTransition("qReject",'0',"qReject");
            assertTrue(automat3.acceptsString("111"));
            assertFalse(automat3.acceptsString("1001"));
            assertFalse(automat3.acceptsString("ε"));
            assertFalse(automat3.acceptsString("1111"));
        }catch(Exception e){}

    }

    @Test
    void minimizeDFA() {
        DFA automat = new DFA();
        automat.addWholeSetOfStates(new String[]{"q0","q1","q2","q3","q4","q5"});
        automat.addStartState("q0");
        automat.addAllFinalStates(new String[]{"q1","q2","q4"});
        automat.setWholeAlphabet(new Character[]{'0','1'});
        try{
            automat.addTransition("q0",'0',"q3");
            automat.addTransition("q0",'1',"q1");
            automat.addTransition("q1",'0',"q2");
            automat.addTransition("q1",'1',"q5");
            automat.addTransition("q2",'0',"q2");
            automat.addTransition("q2",'1',"q5");
            automat.addTransition("q3",'0',"q0");
            automat.addTransition("q3",'1',"q4");
            automat.addTransition("q4",'0',"q2");
            automat.addTransition("q4",'1',"q5");
            automat.addTransition("q5",'0',"q5");
            automat.addTransition("q5",'1',"q5");
            automat.minimizeDFA();
            assertTrue(automat.getStates().size()==3);
            assertTrue(automat.acceptsString("1"));
            assertTrue(automat.acceptsString("10"));
            assertTrue(automat.acceptsString("10000"));
            assertFalse(automat.acceptsString("11"));
            assertFalse(automat.acceptsString("0"));
            assertFalse(automat.acceptsString("ε"));
            assertFalse(automat.acceptsString("100000110"));
        }catch(Exception e){}
        DFA automat2 = new DFA();
        automat2.addWholeSetOfStates(new String[]{"q0","q1","q2","q3"});
        automat2.addStartState("q0");
        automat2.addFinalState("q1");
        automat2.addFinalState("q2");
        automat2.setWholeAlphabet(new Character[]{'a','b'});
        try{
            automat2.addTransition("q0",'a',"q1");
            automat2.addTransition("q0",'b',"q0");
            automat2.addTransition("q1",'a',"q2");
            automat2.addTransition("q1",'b',"q1");
            automat2.addTransition("q2",'a',"q1");
            automat2.addTransition("q2",'b',"q2");
            automat2.addTransition("q3",'a',"q1");
            automat2.addTransition("q3",'b',"q2");
            automat2.minimizeDFA();
            assertTrue(automat2.getStates().size()==2);
            assertTrue(automat2.acceptsString("babbb"));
            assertTrue(automat2.acceptsString("baaaaa"));
            assertTrue(automat2.acceptsString("abbbbbb"));
            assertFalse(automat2.acceptsString("bbbbbbbb"));
        }catch(Exception e)
        {

        }
    }

    @Test
    void shortestWordLength() {
        //automat koji prihvata paran broj a
        DFA automat1 = new DFA();
        automat1.addWholeSetOfStates(new String[]{"q0","q1"});
        automat1.addStartState("q0");
        automat1.addFinalState("q0");
        automat1.addToAlphabet('a');
        automat1.addToAlphabet('b');
        try {
            automat1.addTransition("q0",'a',"q1");
            automat1.addTransition("q0",'b',"q0");
            automat1.addTransition("q1",'a',"q0");
            automat1.addTransition("q1",'b',"q1");

        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(automat1.shortestWordLength()==0);
        //automat koji prihavata rijec sa sekvencom 101 u sebi
        DFA automat2 = new DFA();
        automat2.addWholeSetOfStates(new String[]{"q0","q1","q2","q3"});
        automat2.addStartState("q0");
        automat2.addFinalState("q3");
        automat2.setWholeAlphabet(new Character[]{'1','0'});
        try
        {
            automat2.addTransition("q0",'1',"q1");
            automat2.addTransition("q0",'0',"q0");
            automat2.addTransition("q1",'1',"q1");
            automat2.addTransition("q1",'0',"q2");
            automat2.addTransition("q2",'1',"q3");
            automat2.addTransition("q2",'0',"q0");
            automat2.addTransition("q3",'1',"q3");
            automat2.addTransition("q3",'0',"q3");
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        assertTrue(automat2.shortestWordLength()==3);
        DFA automat3 = new DFA();
        automat3.addWholeSetOfStates(new String[]{"q0","q1","q2","q3","q4"});
        automat3.addStartState("q0");
        automat3.addFinalState("q4");
        automat3.setWholeAlphabet(new Character[]{'a','b'});
        try{
            automat3.addTransition("q0",'a',"q1");
            automat3.addTransition("q0",'b',"q2");
            automat3.addTransition("q1",'a',"q1");
            automat3.addTransition("q1",'b',"q3");
            automat3.addTransition("q2",'a',"q1");
            automat3.addTransition("q2",'b',"q2");
            automat3.addTransition("q3",'a',"q1");
            automat3.addTransition("q3",'b',"q4");
            automat3.addTransition("q4",'a',"q1");
            automat3.addTransition("q4",'b',"q2");
            assertTrue(automat3.shortestWordLength()==3);
        }catch(Exception e)
        {}


    }

    @Test
    void longestWordLength() {
        //automat koji prihvata samo rijec 111
        DFA automat3 = new DFA();
        automat3.addWholeSetOfStates(new String[]{"q0","q1","q2","q3","qReject"});
        automat3.setWholeAlphabet(new Character[]{'1','0'});
        automat3.addStartState("q0");
        automat3.addFinalState("q3");
        try {
            automat3.addTransition("q0", '1', "q1");
            automat3.addTransition("q0",'0',"qReject");
            automat3.addTransition("q1", '1', "q2");
            automat3.addTransition("q1",'0',"qReject");
            automat3.addTransition("q2", '1', "q3");
            automat3.addTransition("q2",'0',"qReject");
            automat3.addTransition("q3", '1', "qReject");
            automat3.addTransition("q3",'0',"qReject");
            automat3.addTransition("qReject", '1', "qReject");
            automat3.addTransition("qReject",'0',"qReject");
        }catch(Exception e){}
        assertTrue(automat3.longestWordLength()==3);

        DFA automat = new DFA();
        automat.addWholeSetOfStates(new String[]{"q0","q1","q2","q3","q4","q5","q6","q7","q8","qReject"});
        automat.addStartState("q0");
        automat.addFinalState("q5");
        automat.addToAlphabet('a');
        automat.addToAlphabet('b');
        try{
            automat.addTransition("q0",'a',"q1");
            automat.addTransition("q0",'b',"qReject");
            automat.addTransition("q1",'a',"q3");
            automat.addTransition("q1",'b',"qReject");
            automat.addTransition("q2",'a',"q4");
            automat.addTransition("q2",'b',"qReject");
            automat.addTransition("q3",'a',"q5");
            automat.addTransition("q3",'b',"q2");
            automat.addTransition("q4",'a',"q6");
            automat.addTransition("q4",'b',"qReject");
            automat.addTransition("q5",'a',"qReject");
            automat.addTransition("q5",'b',"qReject");
            automat.addTransition("q6",'a',"q7");
            automat.addTransition("q6",'b',"qReject");
            automat.addTransition("q7",'a',"q8");
            automat.addTransition("q7",'b',"qReject");
            automat.addTransition("q8",'a',"q5");
            automat.addTransition("q8",'b',"qReject");
            automat.addTransition("qReject",'a',"qReject");
            automat.addTransition("qReject",'b',"qReject");
            assertTrue(automat.longestWordLength()==8);
        }catch(Exception e)
        {

        }

    }

    @Test
    void removeInaccesbileStates() {
        //prvi automat sa 1 nedostiznim stanjem
        DFA automat = new DFA();
        automat.addWholeSetOfStates(new String[]{"q0","q1","q2"});
        automat.addStartState("q0");
        automat.addFinalState("q1");
        automat.setWholeAlphabet(new Character[]{'a'});
        try
        {
            automat.addTransition("q0",'a',"q1");
            automat.addTransition("q1",'a',"q0");
            automat.addTransition("q2",'a',"q0");
            automat.removeInaccesbileStates();
            assertTrue(automat.getStates().size()==2);
        }catch(Exception e){}
        //automat koji ima stanje koje je dostizno iz nekog drugog nedostiznog stanja cineci to stanje nedostiznim
        DFA automat1 = new DFA();
        automat1.addWholeSetOfStates(new String[]{"q0","q1","q2","q3"});
        automat1.addStartState("q0");
        automat1.addFinalState("q3");
        automat1.addToAlphabet('1');
        try{
            automat1.addTransition("q0",'1',"q1");
            automat1.addTransition("q1",'1',"q0");
            automat1.addTransition("q2",'1',"q0");
            automat1.addTransition("q3",'1',"q2");
            automat1.removeInaccesbileStates();
            assertTrue(automat1.getStates().size()==2);
        }catch(Exception e){}
    }
}