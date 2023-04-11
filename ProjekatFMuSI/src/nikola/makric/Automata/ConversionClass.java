package nikola.makric.Automata;

import nikola.makric.helperClasses.Pair;

import java.util.*;

public class ConversionClass {

    //Konverzija u DFA
    public static Convertable toDFA(Convertable objectToConvert) {
        //ako je poslati objekat vec DFA, samo ga vratimo
        if (objectToConvert instanceof DFA) {
            return objectToConvert;
            //Ukoliko je rijec o EpsilonNFA
        } else if (objectToConvert instanceof EpsilonNFA epsilonNFA) {
            Set<Set<String>> dfaStates = new HashSet<>();
            //Uzimamo alfabet tog automata
            Set<Character> alphabet = new HashSet<>(epsilonNFA.getAlphabet());
            alphabet.remove(EpsilonNFA.eps);//ne trebaju nam epsilon tranzicije za ovaj algoritam


            //Inicijalizujemo pocetno stanje novog DFA
            Set<String> dfaStartState = new HashSet<>();
            dfaStartState.add(epsilonNFA.getStartState());
            dfaStartState = epsilonNFA.eClosure(dfaStartState);
            dfaStates.add(dfaStartState);
            //Definisemo skup skupova stanja, gdje svaki skup ce predstavljati novo stanje u DFA
            ConversionClass.stateConversionNFA_DFA(dfaStates, alphabet, epsilonNFA);
            //Generisemo nove atribute DFA i vracamo novokreirani DFA
            return ConversionClass.generateNewAttributesNFA_DFA(dfaStates, epsilonNFA, alphabet);
        } else {
            //ukoliko je poslata reprezentacija regularni izraz, pretvorimo ga prvo u NFA pa onda u DFA
            Convertable nfaVersion = ConversionClass.toNFA(objectToConvert);
            return ConversionClass.toDFA(nfaVersion);
        }
    }
    //Konverzija u NFA
    public static Convertable toNFA(Convertable objectToConvert) {
        //ako je vec poslata reprezentacija u obliku NFA vracamo ga odma
        if (objectToConvert instanceof EpsilonNFA) {
            return objectToConvert;
        } else if (objectToConvert instanceof RegularExpression re) {
            //vrsimo konverziju stringa u postfix
            String postfix = ConversionClass.in2Post(re);
            EpsilonNFA noviAutomat = new EpsilonNFA();
            assert postfix != null;
            //funkcija za evaluaciju postfixa i kreiranje automata
            ConversionClass.createAutomata(postfix, re, noviAutomat);
            return noviAutomat;

        } else {
            //Implementirati funkciju za pretvaranje iz DFA u NFA
            DFA objectToConvertDFA = (DFA) objectToConvert;
            Set<Character> newAlphabet = new HashSet<>(objectToConvertDFA.getAlphabet());
            Set<String> newStates = new HashSet<>(objectToConvertDFA.getStates());
            String newStartState = objectToConvertDFA.getStartState();
            Set<String> newFinalStates = new HashSet<>(objectToConvertDFA.getFinalStates());
            Hashtable<Pair<String, Character>, Set<String>> newdeltaFunction = new Hashtable<>();

            for (String state : objectToConvertDFA.getStates()) {
                for (Character symbol : objectToConvertDFA.getAlphabet()) {
                    Set<String> noviPrelaz = new HashSet<>();
                    noviPrelaz.add((objectToConvertDFA.getTransition(state, symbol)));
                    newdeltaFunction.put(new Pair<>(state, symbol), noviPrelaz);
                }
            }
            EpsilonNFA epsilonNFA = new EpsilonNFA();
            epsilonNFA.addStartState(newStartState);
            epsilonNFA.setStates(newStates);
            epsilonNFA.setAlphabet(newAlphabet);
            epsilonNFA.setFinalStates(newFinalStates);
            epsilonNFA.setDeltaFunction(newdeltaFunction);
            return epsilonNFA;

        }


    }

    private static void stateConversionNFA_DFA(Set<Set<String>> dfaStates, Set<Character> alphabet, EpsilonNFA epsilonNFA) {
        //imamo red ciji su elementi skupopvi stanja
        Queue<Set<String>> queue = new LinkedList<>(dfaStates);
        //sve dok red nije prazan
        while (!queue.isEmpty()) {
            //uzimamo sledeci skup
            var toVisit = queue.remove();
            //stavljamo ih u posjecene
            dfaStates.add(toVisit);
            //za svaki simbol u alpahebtu
            for (Character symbol : alphabet) {
                try {
                    //uzimamo stanja koja bi dobili kada bi dati skup izvrsio prelaz za taj simbol, tako dobijamo
                    //potencijalno jedan novi skup koji predstavlja novo stanje u DFA
                    var tmp1 = epsilonNFA.getEndStates(toVisit, symbol.toString());
                    //provjeravamo da nismo vec taj skup negdje ranije dobili
                    if (!dfaStates.contains(tmp1) && !queue.contains(tmp1))
                        //ako nismo dodajemo ga u red
                        queue.add(tmp1);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }

    private static DFA generateNewAttributesNFA_DFA(Set<Set<String>> dfaStates, EpsilonNFA epsilonNFA, Set<Character> alphabet) {
        //brojac koji sluzi za definisanje imena novih stanja
        int globalCounter = 0;
        //skup novih stanja
        Set<String> newStates = new HashSet<>();
        //skup novih finalnih stanja
        Set<String> newFStates = new HashSet<>();
        //nova delta funkcija
        Hashtable<Pair<String, Character>, String> newDeltaFunction = new Hashtable<>();
        String newName;
        String newDfaStartState = "";
        //prolazimo kroz skupove stanja
        for (var newDfaState : dfaStates) {
            Set<String> temp = new HashSet<>();
            temp.addAll(newDfaState);
            //provjeravamo da li u datom skupu ima ijedno finalno stanje
            temp.retainAll(epsilonNFA.getFinalStates());
            //provjeravamo ako je velicina skupa u pitanju je Dead State
            if (newDfaState.size() == 0)
                newName = "DEAD_STATE";
            else//u suprotnom imenujemo ga uz pomoc brojaca
                newName = Integer.toString(globalCounter++);
            //dodajemo novoimeno stanje u skup stanja novog automata
            newStates.add(newName);
            if (temp.size() > 0)
                //ukoliko smo utvrdili da u datom skupu postoji neko finalno stanje dodajemo dati skup u skup finalnih stanja
                //novog automata
                newFStates.add(newName);
            //na kraju provjeravamo da li skup stanja sadrzi pocetno stanje jer ako sadrzi to je pocetno stanje novog automata
            if (newDfaState.contains(epsilonNFA.getStartState()))
                newDfaStartState = newName;

        }
        //funkcija koja nam definise novu delta funkciju
        ConversionClass.defineDeltaNFA_DFA(dfaStates, epsilonNFA, alphabet, newDeltaFunction);
        DFA newDFA = new DFA();
        try {
            newDFA.setAlphabetSet(alphabet);
            newDFA.addToAlphabet(DFA.eps);
            newDFA.setStateSet(newStates);
            newDFA.addStartState(newDfaStartState);
            newDFA.setDeltaFunction(newDeltaFunction);
            newDFA.initializeEpsilonTransitions();
            newDFA.setFStateSet(newFStates);
        } catch (Exception e) {
            System.out.println(e);
        }
        return newDFA;
    }

    private static void defineDeltaNFA_DFA(Set<Set<String>> dfaStates, EpsilonNFA epsilonNFA, Set<Character> alphabet, Hashtable<Pair<String, Character>, String> newDeltaFunction) {
        int globalCounter = 0;
        //prelazimo kroz skupove stanja
        for (var dfaState : dfaStates) {
            //gledamo da nismo naisli na dead state
            if (dfaState.size() != 0) {
                //ako nije u pitanju dead state
                for (Character symbol : alphabet) {//prolazimo kroz svaki simbol u alfabetu
                    //skup koji nam predstavlja stanje u koje prelazimo iz trenutnog stanja za dati simbol
                    Set<String> stanje = new HashSet<>();
                    int br = 0;
                    try {
                        //uzimamo prijelaz na osnovu simbola i skupa stanja u kojem smo(glavna for petlja)
                        stanje = epsilonNFA.getEndStates(dfaState, symbol.toString());

                    } catch (Exception e) {
                        System.out.println(e);
                        System.exit(-1);
                    }
                    //ponovo prelazimo kroz skup stanja kako bi utvrdili koje je to stanje u koje smo presli prema brojacu
                    for (var dfaState2 : dfaStates) {
                        if (dfaState2.equals(stanje))
                            break;
                        if (dfaState2.size() != 0)
                            br++;
                    }
                    //ako je velicina tog stanja rijec je o dead state-u
                    if (stanje.size() == 0) {
                        newDeltaFunction.put(new Pair<>(Integer.toString(globalCounter), symbol), "DEAD_STATE");
                    } else
                        //u suprotnom u pitanju je stanje sa imenom jednakim string reprezentacijom brojaca
                        newDeltaFunction.put(new Pair<>(Integer.toString(globalCounter), symbol), Integer.toString(br));
                }
                globalCounter++;
            } else {
                //ukoliko smo naisli na dead state, definisemo autotranzicije za svaki simbol
                for (Character symbol : alphabet)
                    newDeltaFunction.put(new Pair<>("DEAD_STATE", symbol), "DEAD_STATE");
            }
        }
    }
    //kreira automat na osnovu postfix izraza
    private static void createAutomata(String postfix, RegularExpression re, EpsilonNFA newAutomata) {
        Stack<Pair<String, Set<String>>> helpStack = new Stack<>();
        int globalCounter = 0;//za imenovanje stanja
        var charArray = (postfix.toCharArray());
        Character next = charArray[0];
        int whileCounter = 1;
        Hashtable<Pair<String, Character>, Set<String>> deltaFunction = new Hashtable<>();//Delta funkcija novog automata
        //prolazimo karakter po karakter postfix izraza
        while (next != null) {
            //ukoliko smo izvukli neki simbol iz alfabeta
            if (re.isItInAlphabet(next)) {
                //Kreiramo bazicni slucaj za jedno slovo, 2 stanja jedan prelaz
                helpStack.push(ConversionClass.basicCaseRE_NFA(deltaFunction, globalCounter, next));
                globalCounter += 2;
            } else if (next.equals('*')) {
                //u pitanju je unarni operator pa nam treba samo jedan operand sa steka
                Pair<String, Set<String>> operand = helpStack.pop();
                //Pozivamo funkciju za Kleenovu zvijezdu
                helpStack.push(ConversionClass.kleeneStar(deltaFunction, globalCounter, operand));
                globalCounter += 2;
            } else if (next.equals('.')) {
                //u pitanju je binarni operator pa nam treabju dva operanda sa steka
                var operand2 = helpStack.pop();
                var operand1 = helpStack.pop();
                //Pozivamo funkciju za konkatenaciju
                helpStack.push(ConversionClass.concat(deltaFunction, operand1, operand2));
            } else if (next.equals('|')) {
                //u pitanju je binarni operator pa nam treabju dva operanda sa steka
                var operand2 = helpStack.pop();
                var operand1 = helpStack.pop();
                //pozivamo funkciju za uniju
                helpStack.push(ConversionClass.union(deltaFunction, globalCounter, operand1, operand2));
                globalCounter += 2;
            }
            if (whileCounter != charArray.length)
                next = charArray[whileCounter];
            else next = null;
            whileCounter++;
        }
        //po pravilu treba nam ostati samo jedan rezultat na steku
        var rez = helpStack.pop();
        if (!helpStack.empty()) {
            System.out.println("Neispravan izraz");
            return;
        }
        //Ako je uspjesno sve kreiramo sada automat
        //Dodajemo pocetno stanje
        newAutomata.addStartState(rez.First());
        //Dodajemo deltu
        newAutomata.setDeltaFunction(deltaFunction);
        //Dodajemo Skup finalnih stanja
        newAutomata.setFinalStates(rez.Second());
        //Dodajemo alfabet
        newAutomata.setAlphabet(re.getAlphabet());
        //Moramo postaviti sva stanja
        for (int i = globalCounter - 1; i >= 0; i--) {
            newAutomata.addOneState(Integer.toString(i));
        }

    }

    private static Pair<String, Set<String>> basicCaseRE_NFA(Hashtable<Pair<String, Character>, Set<String>> deltaFunction, int globalCounter, Character next) {
        //predstavlja bazicni slucaj ukoliko sam naisao na simbol iz alfabeta, za reprezentaciju tog jednog simbola potrebna su mi dva stanja
        //i jedna tranzicija upravo izmedju ta dva stanja koja je zapravo taj simbol
        String beginState = Integer.toString(globalCounter);//definisemo pocetno/prvo stanje od dva
        Set<String> endState = new HashSet<>();//definisemo krajnje odnosno drugo stanje u vidu skupa jer je tako kod epsilonNFA
        endState.add(Integer.toString(globalCounter + 1));
        //definisemo prelaz izmedju ta dva stanja
        deltaFunction.put(new Pair<>(beginState, next), endState);
        //vracamo par koji reprezentuje pocetno i konacno stanje trenutnog automata
        return new Pair<>(beginState, endState);
    }

    private static Pair<String, Set<String>> union(Hashtable<Pair<String, Character>, Set<String>> deltaFunction, int globalCounter,
                                                   Pair<String, Set<String>> operand1, Pair<String, Set<String>> operand2) {
        //Kreirati novo pocetno i novo zavrsno stanje
        String beginState = Integer.toString(globalCounter);
        Set<String> endState = new HashSet<>();
        endState.add(Integer.toString(globalCounter + 1));
        //Spojiti novo pocetno sa starim pocetnim
        Set<String> begToOldBeg = new HashSet<>();
        begToOldBeg.add(operand1.First());
        begToOldBeg.add(operand2.First());
        var deltaDomain = new Pair<>(beginState, EpsilonNFA.eps);
        if (deltaFunction.get(deltaDomain) == null)
            deltaFunction.put(deltaDomain, begToOldBeg);
        else deltaFunction.get(deltaDomain).addAll(begToOldBeg);
        //Spojiti stara zavrsna sa novim zavrsnim

        for (String state : operand1.Second()) {
            deltaDomain = new Pair<>(state, EpsilonNFA.eps);
            if (deltaFunction.get(deltaDomain) == null)
                deltaFunction.put(deltaDomain, endState);
            else deltaFunction.get(deltaDomain).addAll(endState);
        }
        for (String state : operand2.Second()) {
            deltaDomain = new Pair<>(state, EpsilonNFA.eps);
            if (deltaFunction.get(deltaDomain) == null)
                deltaFunction.put(deltaDomain, endState);
            else deltaFunction.get(deltaDomain).addAll(endState);
        }
        return new Pair<>(beginState, endState);


    }

    private static Pair<String, Set<String>> kleeneStar(Hashtable<Pair<String, Character>, Set<String>> deltaFunction, int globalCounter, Pair<String, Set<String>> operand) {
        //Definisemo nova stanja
        String beginState = Integer.toString(globalCounter);//Novo pocetno stanje
        Set<String> endState = new HashSet<>();
        endState.add(Integer.toString(globalCounter + 1));//Novo zavrsno stanje koje cemo predstaviti preko skupa
        //Pravimo skup stanja koje ce biti kodomen tranzicije novog pocetnog stanja sa epsilon tranzicijom
        Set<String> begToEnd = new HashSet<>();
        //u taj skup dodajemo staro pocetno stanje
        begToEnd.add(operand.First());
        //i u njega dodajemo novo zavrsno stanje
        begToEnd.add(Integer.toString(globalCounter + 1));
        //Dodajemo
        if (deltaFunction.get(new Pair<>(beginState, EpsilonNFA.eps)) == null)
            deltaFunction.put(new Pair<>(beginState, EpsilonNFA.eps), begToEnd);
        else
            deltaFunction.get(new Pair<>(beginState, EpsilonNFA.eps)).addAll(begToEnd);
        //Kraj dodavanja
        //Definisemo da proslo zavrsno stanje mora da se epsilon tranzicijom vraca u proslo pocetno stanje
        Set<String> endToBeg = new HashSet<>();
        endToBeg.add(operand.First());
        endToBeg.addAll(endState);
        for (String state : operand.Second()) {
            //Dodajemo za svako stanje iz skupa tih prethodnih zavrsnih epsilon prelaze,ako pojedanicna stanja imaju vec epsilon prelaze, prosirujemo taj skup
            //ako ne dodajemo kreiramo novo mapiranje
            var deltaDomain = new Pair<>(state, EpsilonNFA.eps);
            if (deltaFunction.get(deltaDomain) == null)
                deltaFunction.put(deltaDomain, endToBeg);
            else
                deltaFunction.get(deltaDomain).addAll(endToBeg);

        }
        //Vracamo par koji cemo staviti u stek
        return new Pair<>(beginState, endState);
    }

    private static Pair<String, Set<String>> concat(Hashtable<Pair<String, Character>, Set<String>> deltaFunction,
                                                    Pair<String, Set<String>> operand1, Pair<String, Set<String>> operand2) {
        //funkcija za konkatenaciju dva automata koja su proslijedjena kao argumenti operand1 i operand2
        //Kreiramo novo pocetno i novo zavrsno stanje, novo pocetno je zapravo pocetno od prvog operanda, dok novo zavrsno je zavrsno drugog operanda
        //definisemo novo pocetno stanje
        String beginState = operand1.First();
        //definisemo novo zavrsno stanje
        Set<String> endState = operand2.Second();
        Set<String> concat = new HashSet<>();
        concat.add(operand2.First());
        //dodajemo tranziciju izmedju zavrsnog stanja iz prvog operanda sa pocetnim stanjem drugog operanda i zapravo vrsimo konkatenaciju izmedju ta
        //dva automata
        for (String state : operand1.Second()) {
            var deltaDomain = new Pair<>(state, EpsilonNFA.eps);
            if (deltaFunction.get(deltaDomain) == null)
                deltaFunction.put(deltaDomain, concat);
            else
                deltaFunction.get(deltaDomain).addAll(concat);
        }
        return new Pair<>(beginState, endState);
    }
    //funkcija za konverziju infixa u postfix
    private static String in2Post(RegularExpression rExpression) {
        Stack<Character> stack = new Stack<>();
        int rank = 0;
        String postfix = "";
        var stringArray = (rExpression.getRegularExpression().toCharArray());
        var priorityTable = rExpression.getPriorityOfOperators();
        Character next = stringArray[0];
        int i = 1;
        while (next != null) {
            if (rExpression.isItInAlphabet(next)) {
                postfix += next;
                rank += 1;
            } else {
                while (!(stack.empty()) && (priorityTable.get(next).First() <= priorityTable.get(stack.peek()).Second())) {
                    Character x = stack.pop();
                    postfix += x;
                    if (x != '*')
                        rank -= 1;


                }
                if (!next.equals(')'))
                    stack.push(next);
                else
                    stack.pop();
            }
            if (i != stringArray.length)
                next = stringArray[i];
            else next = null;
            i++;
        }
        while (!(stack.empty())) {
            Character x = stack.pop();
            postfix += x;
            if (x != '*')
                rank -= 1;
        }
        if (rank != 1) {
            System.out.println("Nepravilan izraz");
            return null;
        }
        return postfix;

    }
}
