package nikola.makric.Automata;

import nikola.makric.helperClasses.Pair;

import java.util.*;

public class OperationClass {

    //funkcija za uniju
    public static Convertable Union(Convertable object1, Convertable object2) {
        //vrsimo konverziju obe reprezentacije u DFA
        DFA object1DFA = (DFA) ConversionClass.toDFA(object1);
        DFA object2DFA = (DFA) ConversionClass.toDFA(object2);
        Set<Character> alph1 = object1DFA.getAlphabet();
        Set<Character> alph2 = object2DFA.getAlphabet();
        //provjeravamo osnovni uslov da su im alfabeti isti
        if (!alph1.equals(alph2)) {
            System.out.println("Ne ispunjava pocetni uslov");
            return null;
        }
        //uzimamo nova stanja koja ce sada biti reprezentovana uredjenim parom stanja iz pojedinacnih automata
        Set<Pair<String, String>> statePairs = getNewStates(object1DFA, object2DFA, alph1);
        //nova delta funkcija
        Hashtable<Pair<String, Character>, String> newDeltaFunction = new Hashtable<>();
        //nova stanja
        Set<String> newStates = new HashSet<>();
        //novi alfabet koji je zapravo jednak onom prethodnom
        Set<Character> newAlphabet = new HashSet<>(alph1);
        //novi skup finalnih stanja
        Set<String> newFinalStates = new HashSet<>();
        String newStartState =  defineNewAttributes(object1DFA, object2DFA, newDeltaFunction, newStates, alph1, statePairs);
        int br = 0;
        //konacno definisemo skup finalnih stanja tako sto prolazimo kroz parove i posto je u pitanju unija gledamo da je bar jedno stanje finalno
        for(var statePair : statePairs) {
            if (object1DFA.isItFinalState(statePair.First()) || object2DFA.isItFinalState(statePair.Second())) {
                newFinalStates.add(Integer.toString(br));
            }
            br++;
        }
        //konstrukcija datog DFA
        DFA newDFA = new DFA();
        newDFA.setDeltaFunction(newDeltaFunction);
        newDFA.setStateSet(newStates);
        newDFA.setAlphabetSet(newAlphabet);
        newDFA.setFStateSet(newFinalStates);
        newDFA.addStartState(newStartState);
        return newDFA;
    }
    //funkcija za presjek
    public static Convertable Intersection(Convertable object1, Convertable object2) {
        //sve isto kao kod unije
        DFA object1DFA = (DFA) object1;
        DFA object2DFA = (DFA) object2;
        object1DFA.minimizeDFA();
        object2DFA.minimizeDFA();
        Set<Character> alph1 = object1DFA.getAlphabet();
        Set<Character> alph2 = object2DFA.getAlphabet();
        if (!alph1.equals(alph2)) {
            System.out.println("Ne ispunjava pocetni uslov");
            return null;
        }
        Set<Pair<String, String>> statePairs = getNewStates(object1DFA, object2DFA, alph1);
        Hashtable<Pair<String, Character>, String> newDeltaFunction = new Hashtable<>();
        Set<String> newStates = new HashSet<>();
        Set<Character> newAlphabet = new HashSet<>(alph1);
        Set<String> newFinalStates = new HashSet<>();
        String newStartState = defineNewAttributes(object1DFA, object2DFA, newDeltaFunction, newStates, alph1, statePairs);
        int br = 0;
        //samo pri definisanju novih finalnih stanja gledamo da su oba stanja finalna kako bi dati par bio finalno stanje u novo automatu
        for (var statePair : statePairs) {
            if (object1DFA.isItFinalState(statePair.First()) && object2DFA.isItFinalState(statePair.Second())) {
                newFinalStates.add(Integer.toString(br));
            }
            br++;
        }
        DFA newDFA = new DFA();
        newDFA.setDeltaFunction(newDeltaFunction);
        newDFA.setStateSet(newStates);
        newDFA.setAlphabetSet(newAlphabet);
        newDFA.setFStateSet(newFinalStates);
        newDFA.addStartState(newStartState);
        return newDFA;
    }
    //funkcija za razliku
    public static Convertable Difference(Convertable object1, Convertable object2) {
        //sve isto kao kod unije i presjeka
        DFA object1DFA = (DFA) object1;
        DFA object2DFA = (DFA) object2;
        object1DFA.minimizeDFA();
        object2DFA.minimizeDFA();
        Set<Character> alph1 = object1DFA.getAlphabet();
        Set<Character> alph2 = object2DFA.getAlphabet();
        if (!alph1.equals(alph2)) {
            System.out.println("Ne ispunjava pocetni uslov");
            return null;
        }
        Set<Pair<String, String>> statePairs = getNewStates(object1DFA, object2DFA, alph1);
        Hashtable<Pair<String, Character>, String> newDeltaFunction = new Hashtable<>();
        Set<String> newStates = new HashSet<>();
        Set<Character> newAlphabet = new HashSet<>(alph1);
        Set<String> newFinalStates = new HashSet<>();
        String newStartState = defineNewAttributes(object1DFA, object2DFA, newDeltaFunction, newStates, alph1, statePairs);
        int br = 0;
        //samo nova finalna stanja su parovi kod kojih je prvo stanje finalno a drugo nije u svojim pojedinacnim automatima
        for (var statePair : statePairs) {
            if (object1DFA.isItFinalState(statePair.First()) && !object2DFA.isItFinalState(statePair.Second())) {
                newFinalStates.add(Integer.toString(br));
            }
            br++;
        }
        DFA newDFA = new DFA();
        newDFA.setDeltaFunction(newDeltaFunction);
        newDFA.setStateSet(newStates);
        newDFA.setAlphabetSet(newAlphabet);
        newDFA.setFStateSet(newFinalStates);
        newDFA.addStartState(newStartState);
        return newDFA;
    }
    //funkcija za komplement
    public static Convertable complement(Convertable object1)
    {
        DFA object1DFA = (DFA)ConversionClass.toDFA(object1);
        DFA newDFA = new DFA();
        //nova finalna stanja ce zapravo biti sva stanja koja nisu bila finalna u pocetnom automatu
        Set<String> newFinalStates = new HashSet<>(object1DFA.getStates());
        newFinalStates.removeAll(object1DFA.getFinalStates());
        Set<String> newStates = new HashSet<>(object1DFA.getStates());
        Set<Character> newAlphabet = new HashSet<>(object1DFA.getAlphabet());
        Hashtable<Pair<String, Character>, String> newDeltaFunction = new Hashtable<>(object1DFA.getDeltaFunction());
        newDFA.setFStateSet(newFinalStates);
        newDFA.setStateSet(newStates);
        newDFA.setDeltaFunction(newDeltaFunction);
        newDFA.setAlphabetSet(newAlphabet);
        newDFA.addStartState(object1DFA.getStartState());
        return newDFA;

    }
    //funkcija za konkatenaciju
    public static Convertable concat(Convertable object1, Convertable object2)
    {
        //Pretvaramo date reprezentacije u EpsilonNFA jer je tako lakse izvrsiti konkatenaciju
        EpsilonNFA epsilonNFA1 = (EpsilonNFA) ConversionClass.toNFA(object1);
        EpsilonNFA epsilonNFA2 = (EpsilonNFA) ConversionClass.toNFA(object2);
        Set<Character> alph1 = epsilonNFA1.getAlphabet();
        Set<Character> alph2 = epsilonNFA2.getAlphabet();
        if (!alph1.equals(alph2)) {
            System.out.println("Ne ispunjava pocetni uslov");
            return null;
        }
        //izvrsicemo promjenu imena datih automata za svaki slucaj da ne bi bilo dvosmislenosti
        changeNamesOfNFA(epsilonNFA1,0);
        changeNamesOfNFA(epsilonNFA2,1);
        //pravimo novu delta funkciju tako sto dodamo sve tranzicije i prvog i drugog automata
        Hashtable<Pair<String,Character>,Set<String>> newDeltaFunction= new Hashtable<>();
        newDeltaFunction.putAll(epsilonNFA1.getDeltaFunction());
        newDeltaFunction.putAll(epsilonNFA2.getDeltaFunction());
        //dodajemo novu tranziciju tako sto sva finalna stanja prvog automata spajamo sa pocetnim stanjem drugog automata
        Set<String>tmp = new HashSet<>();tmp.add(epsilonNFA2.getStartState());
        for(String state : epsilonNFA1.getFinalStates()) {
            if(newDeltaFunction.get(new Pair<>(state, EpsilonNFA.eps))==null)
            newDeltaFunction.put(new Pair<>(state, EpsilonNFA.eps), tmp);
            else{
                newDeltaFunction.get(new Pair<>(state, EpsilonNFA.eps)).addAll(tmp);
            }
        }
        //skup novih stanja jeste skup stanja i prvog i drugog automata
        Set<String> newStates = new HashSet<>();
        newStates.addAll(epsilonNFA1.getStates());
        newStates.addAll(epsilonNFA2.getStates());
        //novi skup finalnih stanja su finalna stanja drugog automata
        Set<String> newFStates = new HashSet<>(epsilonNFA2.getFinalStates());
        //novo pocetno stanje je pocetno stanje prvog automata
        String newStartState = epsilonNFA1.getStartState();
        //alfabet je isti
        Set<Character> newAlphabet = new HashSet<>(alph1);
        EpsilonNFA newEpsilonNFA = new EpsilonNFA();
        newEpsilonNFA.setFinalStates(newFStates);
        newEpsilonNFA.setStates(newStates);
        newEpsilonNFA.setDeltaFunction(newDeltaFunction);
        newEpsilonNFA.setAlphabet(newAlphabet);
        newEpsilonNFA.addStartState(newStartState);
        return newEpsilonNFA;



    }
    public static Convertable KleenStar(Convertable object1)
    {
        //Konverzija datog objekta u NFA
        EpsilonNFA object1NFA = (EpsilonNFA) ConversionClass.toNFA(object1);
        //Deklarisemo novu deltu
        //Prekopiramo vec postojece prelaze
        Hashtable<Pair<String, Character>, Set<String>> newDeltaFunction = new Hashtable<>(object1NFA.getDeltaFunction());
        //Definisemo novo pocetno i novo krajnje stanje
        String newBegState = Math.random()+"-"+101;
        String newEndState = Math.random()+"-"+102;
        //Novi skup stanja
        //Dodajemo vec postojeca stanja i nova napravljena
        Set<String> newStates = new HashSet<>(object1NFA.getStates());
        //Novi alfabet, zapravo ostaje isti
        Set<Character> newAlphabet = new HashSet<>(object1NFA.getAlphabet());
        //definisemo jedan izlaz da je staro pocetno stanje
        Set<String> oldBegState = new HashSet<>();
        oldBegState.add(object1NFA.getStartState());
        //A drugi da je novo zavrsno
        Set<String> newEnd = new HashSet<>();
        newEnd.add(newEndState);
        //Deklarisemo novi skup zavrsnih koji ce sada biti novo zavrsno stanje
        Set<String> newFStates = new HashSet<>();
        newFStates.add(newEndState);
        //Dodajemo prvo da o pocetnog novog ide prelaz u staro pocetno i novo zavrsno direkt
        newDeltaFunction.put(new Pair<>(newBegState,EpsilonNFA.eps),oldBegState);
        newDeltaFunction.get(new Pair<>(newBegState,EpsilonNFA.eps)).addAll(newEnd);
        newDeltaFunction.get(new Pair<>(newBegState,EpsilonNFA.eps)).addAll(newEnd);
        //Za svako staro zavrsno pravimo prelaz u staro pocetno i novo izlazno
        for(String fState : object1NFA.getFinalStates())
        {
            newDeltaFunction.put(new Pair<>(fState,EpsilonNFA.eps),oldBegState);
            newDeltaFunction.get(new Pair<>(fState,EpsilonNFA.eps)).addAll(newEnd);
        }
        EpsilonNFA newEpsilonNFA = new EpsilonNFA();
        newEpsilonNFA.setFinalStates(newFStates);
        newEpsilonNFA.setStates(newStates);
        newEpsilonNFA.setDeltaFunction(newDeltaFunction);
        newEpsilonNFA.addOneState(newBegState);
        newEpsilonNFA.addOneState(newEndState);
        newEpsilonNFA.setAlphabet(newAlphabet);
        newEpsilonNFA.addStartState(newBegState);
        return newEpsilonNFA;
    }


    public static String defineNewAttributes(DFA object1DFA, DFA object2DFA, Hashtable<Pair<String, Character>, String> newDeltaFunction, Set<String> newStates,
                                           Set<Character> alph1, Set<Pair<String, String>> statePairs) {
        //brojac koji pomaze pri definisanju imena stanja
        int globalCounter = 0;
        //novo pocetno stanje
        String newStartState = "";
        //Definisanje delte
        //za svaki par u skupu parova
        for (var statePair : statePairs) {
            //imenujemo taj par kao novo stanje
            newStates.add(Integer.toString(globalCounter));
            //provjeravamo da li je to par pocetnih stanja, ako jeste to je pocetno stanje novog automata
            if(statePair.equals(new Pair<>(object1DFA.getStartState(), object2DFA.getStartState())))
            {
                newStartState = Integer.toString(globalCounter);
            }
            //gledamo sada prelaze za taj dati par
            for (Character symbol : alph1) {
                //uzimamo tranzicije pojedinacnih stanja i kreiramo novi par u koji prelazimo za dati simbol
                var nextState = new Pair<>(object1DFA.getTransition(statePair.First(), symbol), object2DFA.getTransition(statePair.Second(), symbol));
                int br = 0;
                //pretrazujemo redni broj tog para u skupu kako bi znali koje ce njegovo ime da bude
                for (var statePair2 : statePairs) {
                    if (statePair2.equals(nextState)) break;
                    br++;
                }
                newDeltaFunction.put(new Pair<>(Integer.toString(globalCounter), symbol), Integer.toString(br));

            }
            globalCounter++;
        }
        return newStartState;


    }

    public static Set<Pair<String, String>> getNewStates(DFA object1DFA, DFA object2DFA, Set<Character> alph1) {
        Set<Pair<String, String>> statePairs = new HashSet<>();
        Queue<Pair<String, String>> toVisit = new LinkedList<>();
        //u pocetku inicijalizujemo novi skup stanja parom pocetnih stanja pojedinacnih automata
        //dodajemo to u red
        toVisit.add(new Pair<>(object1DFA.getStartState(), object2DFA.getStartState()));
        //sve dok red nije prazan
        while (!toVisit.isEmpty()) {
            //uzimamo prvo stanje iz reda
            var firstPair = toVisit.remove();
            statePairs.add(firstPair);
            //za svaki simbol iz alfabeta
            for (Character sym : alph1) {
                //gledamo po definiciji pojedinacne tranzicije stanja u datom paru
                var next = new Pair<>(object1DFA.getTransition(firstPair.First(), sym), object2DFA.getTransition(firstPair.Second(), sym));
                if (!statePairs.contains(next) && !toVisit.contains(next))//ako nije posjeceno stanje i ako nije vec stavljeno u red
                    toVisit.add(next);//dodajemo ga u red

            }
        }
        return statePairs;
    }
    //funkcija za promjenu imena stanja epsilonNFA
    public static void changeNamesOfNFA(EpsilonNFA epsilonNFA, int Fixer)
    {
        int globalCounter = 0;
        Random randomGenerator = new Random();
        //hes tabela gdje mapiramo staro ime stanja u novo
        Hashtable<String,String> nameTransition= new Hashtable<>();
        //skup novih stanja
        Set<String> newStates = new HashSet<>();
        //skup novih finalnih stanja
        Set<String> newFStates = new HashSet<>();
        //za svako stanje iz skupa stanja
        for(String state : epsilonNFA.getStates())
        {
            //generisemo novo ime
            String newState = (randomGenerator.nextDouble()+Fixer)+"-"+globalCounter;
            //dodamo u skup novih stanja
            newStates.add(newState);
            //provjeravamo da li je dato stanje finalno
            if(epsilonNFA.isItFinal(state))
                //ako jeste dodamo ga u skup novih finalnih stanja
                newFStates.add(newState);
            //dodajemo u hes tabelu imena
            nameTransition.put(state,newState);
            globalCounter++;
        }
        //nova delta funkcija
        Hashtable<Pair<String,Character>,Set<String>> newDeltaFunction = new Hashtable<>();
        //za svako stanje u automatu
        for(String state :epsilonNFA.getStates())
        {
            //gledamo tranzicije za svaki simbol iz alfabeta
            for(Character symbol : epsilonNFA.getAlphabet())
            {
                //uzimamo stanje u koje prelazimo
                var setToGo = epsilonNFA.getTransition(state,symbol);
                //ako postoji data tranzicija
                if(setToGo!=null){
                    //kreiramo novi skup u koji ce prelaziti dato stanje za dati simbol
                    //jer su i njima imena promijenjena
                Set<String> newSetToGo = new HashSet<>();
                for(String state1 : setToGo)
                    newSetToGo.add(nameTransition.get(state1));
                newDeltaFunction.put(new Pair<>(nameTransition.get(state),symbol),newSetToGo);}
            }

        }
        //uzimamo novo pocetno stanje
        String newStartState = nameTransition.get(epsilonNFA.getStartState());
        //postavljamo novu delta funkciju
        epsilonNFA.setDeltaFunction(newDeltaFunction);
        //postavljamo novi skup stanja
        epsilonNFA.setStates(newStates);
        //postavljamo novi skup finalnih stanja
        epsilonNFA.setFinalStates(newFStates);
        //postavljamo novo pocetno stanje
        epsilonNFA.addStartState(newStartState);

    }

}
