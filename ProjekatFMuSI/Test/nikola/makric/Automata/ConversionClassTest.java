package nikola.makric.Automata;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConversionClassTest {

    @Test
    void toDFA() {
        //Provjera za konverziju NFA u DFA 1
        EpsilonNFA automat = new EpsilonNFA();
        automat.addWholeSetOfStates(new String[]{"q0", "q1", "q2", "q3", "q4", "q5"});
        automat.addStartState("q0");
        automat.addFinalState("q5");
        automat.setWholeAlphabet(new Character[]{'a', 'b'});
        try {
            automat.addTransition("q0", 'a', new String[]{"q1"});
            automat.addTransition("q1", 'b', new String[]{"q2"});
            automat.addTransition("q1", 'ε', new String[]{"q5"});
            automat.addTransition("q2", 'a', new String[]{"q1"});
            automat.addTransition("q5", 'b', new String[]{"q3", "q5"});
            automat.addTransition("q3", 'a', new String[]{"q4"});
            automat.addTransition("q4", 'b', new String[]{"q5"});
            DFA dfaReprezentacija = (DFA)ConversionClass.toDFA(automat);
            System.out.println(dfaReprezentacija.getStates().size());
            assertTrue(dfaReprezentacija.acceptsString("aba"));
            assertTrue(dfaReprezentacija.acceptsString("ab"));
            assertFalse(dfaReprezentacija.acceptsString("abaa"));
        }catch(Exception e){e.printStackTrace();}
        //Provjera za konverziju NFA u DFA 2
        EpsilonNFA example1 = new EpsilonNFA();
        example1.addWholeSetOfStates(new String[]{"q0","q1","q2","q3","q4"});
        example1.addStartState("q0");
        example1.addFinalState("q4");
        example1.setWholeAlphabet(new Character[]{'0','1'});
        try
        {
           example1.addTransition("q0",'ε',new String[]{"q1","q2"});
           example1.addTransition("q1",'0',new String[]{"q3"});
           example1.addTransition("q2",'1',new String[]{"q3"});
           example1.addTransition("q3",'1',new String[]{"q4"});
           DFA reprezentacijaDFAE1 = (DFA)ConversionClass.toDFA(example1);
           System.out.println(reprezentacijaDFAE1.getStates().size());
           //oni su na slici zaboravili dead state
           assertTrue(reprezentacijaDFAE1.getStates().size()==4);
           assertTrue(reprezentacijaDFAE1.acceptsString("01"));
           assertFalse(reprezentacijaDFAE1.acceptsString("0"));
           assertFalse(reprezentacijaDFAE1.acceptsString("010"));
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        //Provjera za konverziju NFA u DFA 3
        EpsilonNFA example2 = new EpsilonNFA();
        example2.addWholeSetOfStates(new String[]{"q0","q1","q2"});
        example2.addStartState("q0");
        example2.addFinalState("q2");
        example2.setWholeAlphabet(new Character[]{'0','1','2'});
        try
        {
            example2.addTransition("q0",'0',new String[]{"q0"});
            example2.addTransition("q0",'ε',new String[]{"q1"});
            example2.addTransition("q1",'1',new String[]{"q1"});
            example2.addTransition("q1",'ε',new String[]{"q2"});
            example2.addTransition("q2",'2',new String[]{"q2"});
            DFA reprezentacijaDFAE2 = (DFA)ConversionClass.toDFA(example2);
            //oni su na slici zaboravili dead state
            assertTrue(reprezentacijaDFAE2.getStates().size()==4);
            assertTrue(reprezentacijaDFAE2.getFinalStates().size()==3);
            assertTrue(reprezentacijaDFAE2.acceptsString("012"));
            assertTrue(reprezentacijaDFAE2.acceptsString("2"));
            assertFalse(reprezentacijaDFAE2.acceptsString("10"));
            assertFalse(reprezentacijaDFAE2.acceptsString("121"));
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        //provjera za konverziju RE u DFA
        RegularExpression re = new RegularExpression();
        re.setWholeAlphabet(new Character[]{'a','b'});
        re.setRegularExpression("a.b");
        var DFARepresentation =(DFA) ConversionClass.toDFA(re);
        try {
            assertTrue(DFARepresentation.acceptsString("ab"));
            assertFalse(DFARepresentation.acceptsString("a"));
            assertFalse(DFARepresentation.acceptsString("b"));
            re.setRegularExpression("b*");
            DFARepresentation = (DFA)ConversionClass.toDFA(re);
            assertTrue(DFARepresentation.acceptsString(EpsilonNFA.eps.toString()));
            assertTrue(DFARepresentation.acceptsString("bbbbb"));
            re.setRegularExpression("a.a|a*.b|a*.b*");
            DFARepresentation = (DFA)ConversionClass.toDFA(re);
            assertTrue(DFARepresentation.acceptsString("b"));
            assertTrue(DFARepresentation.acceptsString("aaaaab"));
            assertTrue(DFARepresentation.acceptsString("b"));
            assertTrue(DFARepresentation.acceptsString("aaabbbbb"));
            assertTrue(DFARepresentation.acceptsString(EpsilonNFA.eps.toString()));
        }catch(Exception e){e.printStackTrace();}
    }

    @Test
    void toNFA() {
        //Konverzija regularnog izraza u NFA
        RegularExpression re = new RegularExpression();
        re.setWholeAlphabet(new Character[]{'0','1','2'});
        re.setRegularExpression("0*|1*.0.2*.0|2.2|2*.1*.0*|1.1.1|1*.1.2*.1.0*.1");
        var EpsilonNFARepresentation = (EpsilonNFA)ConversionClass.toNFA(re);
        try {
            assertTrue(EpsilonNFARepresentation.acceptsString("000000"));
            assertTrue(EpsilonNFARepresentation.acceptsString(EpsilonNFA.eps.toString()));
            assertTrue(EpsilonNFARepresentation.acceptsString("00"));
            assertTrue(EpsilonNFARepresentation.acceptsString("1020"));
            assertTrue(EpsilonNFARepresentation.acceptsString("22"));
            assertTrue(EpsilonNFARepresentation.acceptsString("2222221111110"));
            assertTrue(EpsilonNFARepresentation.acceptsString("111"));
            assertTrue(EpsilonNFARepresentation.acceptsString("11111112222222100000001"));
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        }
    }
