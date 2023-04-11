package nikola.makric;

import nikola.makric.Automata.*;
import nikola.makric.Generator.CodeGenerator;
import specification.Lexer;
import specification.RlCreator;


import java.io.BufferedReader;
import java.io.FileReader;

public class Main {
    public static void main(String[] args) {
        Lexer lexer = new Lexer("path");
        for (var string : lexer.specification) {
            System.out.println(string);
        }
        String nikola = "a,,,b,c,d,s";
        try {
            DFA paranA = new DFA();
            paranA.addWholeSetOfStates(new String[]{"q0","q1"});
            paranA.addFinalState("q0");
            paranA.setWholeAlphabet(new Character[]{'a','b'});
            paranA.addStartState("q0");
            paranA.addTransition("q0",'a',"q1");
            paranA.addTransition("q1",'a',"q0");
            paranA.addTransition("q0",'b',"q0");
            paranA.addTransition("q1",'b',"q1");
            CodeGenerator cg = new CodeGenerator(paranA);
            cg.simulationGenerator();
        } catch (Exception e) {

        }


        //EpsilonNFA machine = new EpsilonNFA();
        //machine.addWholeSetOfStates(new String[]{"q1", "q2", "q3"});
        //machine.addStartState("q1");
        //machine.addFinalState("q3");
        //try {
        //    machine.setWholeAlphabet(new Character[]{'a', 'b'});
        //    machine.addTransition("q1", 'a', new String[]{"q1", "q2"});
        //    machine.addTransition("q2", 'b', new String[]{"q3"});
        //    machine.addTransition("q3", 'b', new String[]{"q3"});
        //    System.out.println(machine.acceptsString("aabc"));
        //} catch (Exception e) {
        //    System.out.println(e);
        //}
////
        //DFA machine2 = new DFA();
        //machine2.addWholeSetOfStates(new String[]{"q1", "q2"});
        //machine2.addStartState("q1");
        //machine2.addFinalState("q2");
        //try {
        //    machine2.setWholeAlphabet(new Character[]{'a', 'b'});
        //    machine2.addTransition("q1", 'a', "q2");
        //    machine2.addTransition("q1", 'b', "q1");
        //    machine2.addTransition("q2", 'a', "q1");
        //    machine2.addTransition("q2", 'b', "q2");
        //    System.out.println(machine2.acceptsString("aabbb"));
        //    System.out.println(machine2.acceptsString("aaabbb"));
        //    System.out.println(machine2.acceptsString("abbb"));
        //    System.out.println(machine2.acceptsString("bbb"));
        //    System.out.println(machine2.acceptsString("aqbbb"));
        //} catch (Exception e) {
        //    System.out.println(e);
        //}
        // RegularExpression re = new RegularExpression();
        // RegularExpression re2 = new RegularExpression();
        // re2.setWholeAlphabet(new Character[]{'a','b','$'});
        // re2.setRegularExpression("b.a");
        // var boze= ConversionClass.toDFA(re2);
        // //EpsilonNFA noveee = (EpsilonNFA) ConversionClass.toNFA(re2);
        // re.setWholeAlphabet(new Character[]{'a','b','$'});
        // re.setRegularExpression("a.b");
        ////DFA noviAutomat = (DFA) ConversionClass.toDFA(re);
        ////EpsilonNFA novis = (EpsilonNFA)OperationClass.KleenStar(noveee);
        ////System.out.println(novis.acceptsString("ababab"));
        ////RegularExpression re3 = new RegularExpression();
        ////re3.setWholeAlphabet(new String[]{"a","b","$"});
        ////re3.setRegularExpression("a");
        //Convertable nigdjeVeze = OperationClass.concat(re2,re);
        //var ajmoFinale =(DFA) ConversionClass.toDFA(nigdjeVeze);
        //DFA nigdjeVeze1 = (DFA)ConversionClass.toDFA(re2);
        //System.out.println(ajmoFinale.acceptsString("baab"));
        //nigdjeVeze1.minimizeDFA();
        ////Convertable nigdjeVeze2 = OperationClass.Union(nigdjeVeze1,re3);
        ////DFA nigdjeVeze3 = (DFA) ConversionClass.toDFA(nigdjeVeze2);
        // EpsilonNFA lengthTest = new EpsilonNFA();
        // lengthTest.setWholeAlphabet(new String[]{"a","b","$"});
        // lengthTest.addWholeSetOfStates(new String[]{"q0","q1","q2","q3","q4","q5","q6","q7"});
        // lengthTest.addStartState("q0");
        // lengthTest.addTransition("q0","a",new String[]{"q1"});
        // lengthTest.addTransition("q0","b",new String[]{"q2"});
        // lengthTest.addTransition("q1","a",new String[]{"q3"});
        // lengthTest.addTransition("q1","b",new String[]{"q2"});
        // lengthTest.addTransition("q2","a",new String[]{"q4"});
        // lengthTest.addTransition("q3","a",new String[]{"q5"});
        // lengthTest.addTransition("q3","b",new String[]{"q4"});
        // lengthTest.addTransition("q4","a",new String[]{"q5"});
        // lengthTest.addTransition("q4","b",new String[]{"q6"});
        // lengthTest.addTransition("q6","a",new String[]{"q7"});
        // lengthTest.addTransition("q7","a",new String[]{"q5"});
        // lengthTest.addFinalState("q5");
        // lengthTest.addFinalState("q1");
        // var dfa = (DFA)ConversionClass.toDFA(lengthTest);
        // System.out.println(nigdjeVeze1.longestWordLength());
        // RegularExpression sds = new RegularExpression();
        //sds.setRegularExpression("nigdjeVeze1");
        //var sfs = ConversionClass.toDFA(sds);
        //int trueCounter=0;int falseCounter = 0;
//
        //    Convertable BozePomozi = OperationClass.concat(re2,re);
        //    Convertable BozePomozi2 = OperationClass.KleenStar(BozePomozi);
        //    DFA uf = (DFA) ConversionClass.toDFA(BozePomozi2);
        //    System.out.println((uf.acceptsString("$")));



        //System.out.println(paranA.longestWordLength());
        //System.out.println(noviAutomat.shortestWordLength());
        //System.out.println(noveee.longestWordLength());

        //System.out.println(AutomataEquivalence.areTheyEqual(noviAutomat,paranA));
        //System.out.println(AutomataEquivalence.areTheyEqual(re,re2));
        //System.out.println(paranA.acceptsString("$"));
        //System.out.println(noviAutomat.acceptsString("$"));
        //System.out.println(noveee.acceptsString("a"));
        //EpsilonNFA automat = new EpsilonNFA();
        //automat.setWholeAlphabet(new String[]{"a", "b"});
        //automat.addStartState("q0");
        //automat.addWholeSetOfStates(new String[]{"q0","q1","q2","q3","q4"});
        //automat.addTransition("q0","a",new String[]{"q0"});
        //automat.addTransition("q0",EpsilonNFA.eps,new String[] {"q1"});
        //automat.addTransition("q1","b",new String[]{"q4","q2"});
        //automat.addTransition("q1",EpsilonNFA.eps,new String[]{"q0"});
        //automat.addTransition("q4","a",new String[]{"q1"});
        //automat.addTransition("q2","a",new String[]{"q2","q3"});
        //automat.addTransition("q3","b",new String[]{"q3"});
        //automat.addTransition("q3",EpsilonNFA.eps,new String []{"q1"});
        //automat.addFinalState("q1");
        //System.out.println(automat.acceptsString("$"));
//
        //EpsilonNFA konverzija = new EpsilonNFA();
        //konverzija.setWholeAlphabet(new String[]{"0","1"});
        //konverzija.addWholeSetOfStates(new String []{"q0","q1","q2","q3","q4"});
        //konverzija.addTransition("q0",EpsilonNFA.eps,new String[]{"q1","q2"});
        //konverzija.addTransition("q1","0",new String[]{"q3"});
        //konverzija.addTransition("q2","1",new String[]{"q3"});
        //konverzija.addTransition("q3","1",new String[]{"q4"});
        //konverzija.addStartState("q0");
        //konverzija.addFinalState("q4");
        //ConversionClass.toDFA(konverzija);
//
        //EpsilonNFA konverzija2 = new EpsilonNFA();
        //konverzija2.addWholeSetOfStates(new String []{"q0","q1","q2"});
        //konverzija2.setWholeAlphabet(new String[]{"0","1","2"});
        //konverzija2.addTransition("q0",EpsilonNFA.eps,new String[]{"q1"});
        //konverzija2.addTransition("q0","0",new String[]{"q0"});
        //konverzija2.addTransition("q1","1",new String[]{"q1"});
        //konverzija2.addTransition("q1",EpsilonNFA.eps,new String[]{"q2"});
        //konverzija2.addTransition("q2","2",new String[]{"q2"});
        //konverzija2.addStartState("q0");
        //konverzija2.addFinalState("q2");
        //DFA dfaConverted = (DFA)ConversionClass.toDFA(konverzija);
        //System.out.println("evo me");
        //System.out.println(dfaConverted.acceptsString("01"));
        //System.out.println(dfaConverted.acceptsString("11"));
        //System.out.println(dfaConverted.acceptsString("111"));
        //System.out.println(dfaConverted.acceptsString("000"));

        // Set<String> skup = new HashSet<>();
        // skup.add("q0");
        // var klozur = automat.eClosure(skup);
        // System.out.println("Klozur od q0:");
        // for(var State : klozur)
        // {
        //     System.out.println(State+" ");
        // }
        // A.addAllFinalStates(new String[]{"q1","q4","q8"});
        // A.setWholeAlphabet(new String[]{"a","b"});
        //machine3.setWholeAlphabet(new String[]{"1", "0"});
        //machine3.addTransition("q0","1","q1");
        //machine3.addTransition("q0","0","q2");
        //machine3.addTransition("q1","1","q1");
        //machine3.addTransition("q1","0","q1");
        //machine3.addTransition("q2","1","q8");
        //machine3.addTransition("q2","0","q4");
        //machine3.addTransition("q3","1","q1");
        //machine3.addTransition("q3","0","q3");
        //machine3.addTransition("q4","1","q4");
        //machine3.addTransition("q4","0","q4");
        //machine3.addTransition("q5","1","q4");
        //machine3.addTransition("q5","0","q6");
        //machine3.addTransition("q6","1","q6");
        //machine3.addTransition("q6","0","q6");
        //machine3.addTransition("q7","1","q8");
        //machine3.addTransition("q7","0","q6");
        //machine3.addTransition("q8","1","q8");
        //machine3.addTransition("q8","0","q8");
        //DFA.minimizeDFA(machine3);

        //A.addTransition("q0", "a", "q6");
        //A.addTransition("q0", "b", "q1");
        //A.addTransition("q1", "a", "q2");
        //A.addTransition("q1", "b", "q4");
        //A.addTransition("q2", "a", "q5");
        //A.addTransition("q2", "b", "q3");
        //A.addTransition("q3", "a", "q8");
        //A.addTransition("q3", "b", "q3");
        //A.addTransition("q4", "a", "q6");
        //A.addTransition("q4", "b", "q8");
        //A.addTransition("q5", "a", "q2");
        //A.addTransition("q5", "b", "q3");
        //A.addTransition("q6", "a", "q4");
        //A.addTransition("q6", "b", "q7");
        //A.addTransition("q7", "a", "q8");
        //A.addTransition("q7", "b", "q3");
        //A.addTransition("q8", "a", "q7");
        //A.addTransition("q8", "b", "q4");
        //A.addTransition("q9", "a", "q10");
        //A.addTransition("q9", "b", "q8");
        //A.addTransition("q10", "a", "q3");
        //A.addTransition("q10", "b", "q9");

        //A.minimizeDFA();


    }
}