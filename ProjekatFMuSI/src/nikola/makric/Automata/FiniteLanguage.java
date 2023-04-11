package nikola.makric.Automata;

import java.util.*;

public class FiniteLanguage {


    public static boolean isTheLanguageInfinite(Convertable toCheck)
    {
        //Prevodimo ga prvo u DKA i minimizujemo
        DFA toCheckDFA = (DFA)ConversionClass.toDFA(toCheck);
        toCheckDFA.minimizeDFA();
        //Algoritam za provjeru da li ima ciklusa
        Stack<String> recursionStack = new Stack<>();
        Set<String> belongsToCycle = new HashSet<>();
        Set<String> visited = new HashSet<>();
        DFS(toCheckDFA,recursionStack,belongsToCycle,visited, toCheckDFA.getStartState());
        boolean checker = BFStoCheck(toCheckDFA,belongsToCycle);
        return checker;

    }

    public static void DFS(DFA toCheckDFA,Stack<String> recursionStack,Set<String> belongsToCycle,Set<String> visited,String state)
    {
        //rekurzivna implementacija DFS-a kako bi nasli ciklus
        visited.add(state);
        recursionStack.push(state);
        //za svako stanje koje posjetimo gledamo sve njegove tranzicije i da li ima povratne grane tj da li stanje u koje prelazi
        //za dati simbol se vec nalazi u rekurzivnom steku
        for(Character symb : toCheckDFA.getAlphabet())
        {
            if(!symb.equals(DFA.eps)){
            String nState = toCheckDFA.getTransition(state,symb);
            if(recursionStack.search(nState)!=-1)
                belongsToCycle.add(nState);
            if(!visited.contains(nState))
                DFS(toCheckDFA,recursionStack,belongsToCycle,visited,nState);}


        }
        recursionStack.pop();
        return;
    }
    //konacno za svako stanje koje pripada ciklusu pokusavamo da dodjemo do finalnog stanja kako bi utvrdili da li je
    //jezik beskonacan ili ne
    public static boolean BFStoCheck(DFA toCheckDFA, Set<String>belongsToCycle)
    {
        Queue<String> kju = new LinkedList<>();
        boolean checker = false;
        Set<String> visited = new HashSet<>();
        for(String stateToCheck : belongsToCycle)
        {
            kju.add(stateToCheck);
            while(!kju.isEmpty()) {
                String toInspectState = kju.remove();
                if(toCheckDFA.isItFinalState(toInspectState)) {
                    checker = true;
                    break;
                }
                visited.add(toInspectState);
                for (Character symb : toCheckDFA.getAlphabet()) {
                    String newState = toCheckDFA.getTransition(toInspectState,symb);
                    if(!visited.contains(newState))
                        kju.add(newState);
                }
            }
            if(checker)
                break;
            kju.clear();
            visited.clear();
        }
        return checker;
    }
}
