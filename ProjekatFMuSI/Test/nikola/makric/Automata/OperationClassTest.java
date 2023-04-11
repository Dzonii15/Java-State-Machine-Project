package nikola.makric.Automata;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OperationClassTest {

    @Test
    void union() {
        //unija dva dfa
        try {
            DFA automat1 = new DFA();
            automat1.addWholeSetOfStates(new String[]{"q0", "q1", "q2"});
            automat1.addStartState("q0");
            automat1.addFinalState("q1");
            automat1.setWholeAlphabet(new Character[]{'a', 'b'});
            automat1.addTransition("q0",'a',"q0");
            automat1.addTransition("q0",'b',"q1");
            automat1.addTransition("q1",'a',"q2");
            automat1.addTransition("q1",'b',"q2");
            automat1.addTransition("q2",'a',"q2");
            automat1.addTransition("q2",'b',"q2");
            DFA automat2 = new DFA();
            automat2.addWholeSetOfStates(new String[]{"q3","q4"});
            automat2.addStartState("q3");
            automat2.addFinalState("q4");
            automat2.setWholeAlphabet(new Character[]{'a','b'});
            automat2.addTransition("q3",'a',"q4");
            automat2.addTransition("q3",'b',"q3");
            automat2.addTransition("q4",'a',"q3");
            automat2.addTransition("q4",'b',"q4");

            DFA unionRepresentation = (DFA)OperationClass.Union(automat1,automat2);
            assertTrue(unionRepresentation.getStates().size()==6);
            assertTrue(unionRepresentation.acceptsString("a"));
            assertTrue(unionRepresentation.acceptsString("ab"));
            assertTrue(unionRepresentation.acceptsString("abb"));
            assertTrue(unionRepresentation.acceptsString("abbbbb"));
            assertTrue(unionRepresentation.acceptsString("b"));
            assertTrue(unionRepresentation.acceptsString("babbbb"));

        }catch(Exception e){e.printStackTrace();}
    }

    @Test
    void intersection() {
        try
        {
            DFA automat1 = new DFA();
            automat1.addWholeSetOfStates(new String[]{"q0","q1","q2"});
            automat1.addStartState("q0");
            automat1.addFinalState("q2");
            automat1.setWholeAlphabet(new Character[]{'0','1'});
            automat1.addTransition("q0",'0',"q1");
            automat1.addTransition("q0",'1',"q0");
            automat1.addTransition("q1",'0',"q1");
            automat1.addTransition("q1",'1',"q2");
            automat1.addTransition("q2",'0',"q1");
            automat1.addTransition("q2",'1',"q0");
            DFA automat2 = new DFA();
            automat2.addWholeSetOfStates(new String[]{"s0","s1"});
            automat2.addStartState("s0");
            automat2.addFinalState("s0");
            automat2.setWholeAlphabet(new Character[]{'0','1'});
            automat2.addTransition("s0",'0',"s0");
            automat2.addTransition("s0",'1',"s1");
            automat2.addTransition("s1",'0',"s1");
            automat2.addTransition("s1",'1',"s0");
            DFA intersectionRepresentation = (DFA)OperationClass.Intersection(automat1,automat2);
            assertTrue(intersectionRepresentation.getStates().size()==6);
            assertTrue(intersectionRepresentation.acceptsString("1001"));
            assertTrue(intersectionRepresentation.acceptsString("01001"));
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    void difference() {
        try {
            DFA automat1 = new DFA();
            automat1.addWholeSetOfStates(new String[]{"E", "F", "G"});
            automat1.addStartState("E");
            automat1.addFinalState("E");
            automat1.addFinalState("F");
            automat1.setWholeAlphabet(new Character[]{'a', 'b'});
            automat1.addTransition("E",'a',"E");
            automat1.addTransition("E",'b',"F");
            automat1.addTransition("F",'a',"G");
            automat1.addTransition("F",'b',"F");
            automat1.addTransition("G",'a',"G");
            automat1.addTransition("G",'b',"G");
            DFA automat2 = new DFA();
            automat2.addWholeSetOfStates(new String[]{"H","I","J"});
            automat2.addStartState("H");
            automat2.addFinalState("H");
            automat2.addFinalState("I");
            automat2.setWholeAlphabet(new Character[]{'a','b'});
            automat2.addTransition("H",'a',"I");
            automat2.addTransition("H",'b',"H");
            automat2.addTransition("I",'a',"I");
            automat2.addTransition("I",'b',"J");
            automat2.addTransition("J",'a',"J");
            automat2.addTransition("J",'b',"J");
            DFA differenceRepresentation = (DFA)OperationClass.Difference(automat1,automat2);
            assertTrue(differenceRepresentation.getStates().size()==6);
            assertTrue(differenceRepresentation.acceptsString("ab"));
            assertTrue(differenceRepresentation.acceptsString("aaaaab"));
        }catch(Exception e){e.printStackTrace();}
    }

    @Test
    void complement() {
        //DFA za paran broj a
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
            DFA complementRepresentation = (DFA)OperationClass.complement(automat1);
            assertTrue(complementRepresentation.acceptsString("a"));
            assertTrue(complementRepresentation.acceptsString("aaa"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void concat() {
        RegularExpression re1 = new RegularExpression();
        re1.setWholeAlphabet(new Character[]{'a','b'});
        re1.setRegularExpression("a.b");
        RegularExpression re2 = new RegularExpression();
        re2.setWholeAlphabet(new Character[]{'a','b'});
        re2.setRegularExpression("b.a");
        EpsilonNFA concatRepresentation = (EpsilonNFA) OperationClass.concat(re1,re2);
        try {
            assertTrue(concatRepresentation.acceptsString("abba"));
            re1.setRegularExpression("a*");
            re2.setRegularExpression("b*");
            concatRepresentation = (EpsilonNFA) OperationClass.concat(re1,re2);
            assertTrue(concatRepresentation.acceptsString("aaabb"));
            assertTrue(concatRepresentation.acceptsString(EpsilonNFA.eps.toString()));
        }catch (Exception e){e.printStackTrace();}
    }

    @Test
    void kleenStar() {
        RegularExpression re1 = new RegularExpression();
        re1.setWholeAlphabet(new Character[]{'a','b'});
        re1.setRegularExpression("a");
        EpsilonNFA kleenStarRepresentation = (EpsilonNFA) OperationClass.KleenStar(re1);
        try
        {
            assertTrue(kleenStarRepresentation.acceptsString(EpsilonNFA.eps.toString()));
            assertTrue(kleenStarRepresentation.acceptsString("a"));
            assertTrue(kleenStarRepresentation.acceptsString("aaaa"));
            re1.setRegularExpression("a.b");
            kleenStarRepresentation = (EpsilonNFA) OperationClass.KleenStar(re1);
            assertTrue(kleenStarRepresentation.acceptsString(EpsilonNFA.eps.toString()));
            assertTrue(kleenStarRepresentation.acceptsString("ab"));
            assertTrue(kleenStarRepresentation.acceptsString("abab"));
        }catch(Exception e){e.printStackTrace();}

    }
}