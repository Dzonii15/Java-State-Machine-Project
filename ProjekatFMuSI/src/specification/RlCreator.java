package specification;

import nikola.makric.Automata.Convertable;
import nikola.makric.Automata.DFA;
import nikola.makric.Automata.EpsilonNFA;
import nikola.makric.Automata.RegularExpression;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RlCreator {
    private final List<List<Token>> tokenList;

    public RlCreator(List<List<Token>> tokenList)
    {
        this.tokenList = tokenList;
    }

    public Convertable returnRLR()
    {
        //u slucaju da je lekser detektovao neke greske vratice null pa to moramo provjeriti
        if(tokenList == null)
            return null;
        //uzimamo prvi token iz liste da vidimo reprezentaciju koja je trazena
        String representation = tokenList.get(0).get(0).Value;
        //alfabet reprezentacije
        Set<Character>alphabet = new HashSet<>();
        if(representation.equals("RE"))
        {
            //Uzimamo alfabet iz tokena
            this.getAlphabetFromTokens(alphabet,1);
            //Uzimamo regularni izraz
            String regExpression = tokenList.get(2).get(0).Value;
            RegularExpression reg = new RegularExpression();
            reg.setAlphabet(alphabet);
            reg.setRegularExpression(regExpression);
            //Osiguravamo da li je regularni izraz dobro napisan
            boolean checker= reg.assureCorectness();
            if(!checker)
            {
                System.out.println("Invalid specification");
                return null;
            }
            //provjeravamo za Stringove koje je korisnik unio da li su prihvaceni
            var acceptStringTokens = tokenList.get(3);
            for(Token token : acceptStringTokens)
            {
                try {
                    System.out.println("String " + token.Value + " je prihvacen? " + reg.acceptString(token.Value));
                }catch(Exception e)
                {
                    System.out.println("String "+ token.Value + " nije validnog formata za datu specifikaciju regularnog jezika");
                }
            }
            return reg;

        }else {
            Set<String> states = new HashSet<>();
            Set<String> finalStates = new HashSet<>();
            //Funkcija kupi i popunjava atribute DFA ili NFA osim tranzicija
            //Znaci ovo su atributi koji su validni i za DFA i za NFA
            String startState = this.fetchUntilTransition(states,finalStates,alphabet);
            var transitionTokenList = tokenList.get(5);
            if(representation.equals("DFA"))
            {
                //Instanciram i popunjavam DFA jer zelim da pozivam AddTransition funkciju koja ima semanticke provjere
                DFA dfaAutomata = new DFA();
                dfaAutomata.setStateSet(states);
                dfaAutomata.setFinalStatesWithCheck(finalStates);
                dfaAutomata.addStartState(startState);
                dfaAutomata.setAlphabetSet(alphabet);
                //Za svaki token koji predstavlja jednu tranziciju
                for(Token token : transitionTokenList)
                {
                    //split-am da dobijem stanje - karakter - stanje
                    var transitionParts = token.Value.split("-");
                    //i pokusavam dodati tranziciju
                    try {
                        dfaAutomata.addTransition(transitionParts[0], transitionParts[1].charAt(0), transitionParts[2]);
                    }catch(Exception e)
                    {
                        System.out.println("Invalid specification");
                        return null;
                    }
                }
                //Pod pretpostavkom da je kreiran dobro automat i provjeravamo Stringove koje je korisnik poslao na ispitivanje
                var acceptStringTokens = tokenList.get(6);
                for(Token token : acceptStringTokens)
                {
                    try {
                        System.out.println("String " + token.Value + " je prihvacen? " + dfaAutomata.acceptsString(token.Value));
                    }catch(Exception e)
                    {
                        System.out.println("String "+ token.Value + "nije validnog formata za datu specifikaciju regularnog jezika");
                    }
                }
                return dfaAutomata;
            }
            else
            {
                EpsilonNFA nfaAutomat = new EpsilonNFA();
                nfaAutomat.setStates(states);
                nfaAutomat.setFinalStatesWithCheckup(finalStates);
                nfaAutomat.addStartState(startState);
                nfaAutomat.setAlphabet(alphabet);
                //Kreiramo delta funkciju za dati automat jedina razlika sto je ovdje kodomen skup stanja u koje se prelazi
                for(Token token : transitionTokenList)
                {
                    var tokenSeparation = token.Value.split("-");
                    var destinationArray = tokenSeparation[2].split(",");
                    try {
                        nfaAutomat.addTransition(tokenSeparation[0], tokenSeparation[1].charAt(0), destinationArray);
                    }catch(Exception e)
                    {
                        System.out.println("Invalid specification");
                        return null;
                    }
                }
                var acceptStringTokens = tokenList.get(6);
                for(Token token : acceptStringTokens)
                {
                    try {
                        System.out.println("String " + token.Value + " je prihvacen? " + nfaAutomat.acceptsString(token.Value));
                    }catch(Exception e)
                    {
                        System.out.println("String "+ token.Value + "nije validnog formata za datu specifikaciju regularnog jezika");
                    }
                }
                return nfaAutomat;
            }

        }


    }
    private String fetchUntilTransition(Set<String> states,Set<String> finalStates,Set<Character> alphabet)
    {
        //Ucitavamo stanja na osnovu tokena koje smo proslijedili
        var stateTokenList = tokenList.get(1);
        for(Token token : stateTokenList)
        {
            states.add(token.Value);
        }
        //Ucitavamo pocetno stanje
        String startState = tokenList.get(2).get(0).Value;

        //Ucitavamo finalna stanja
        var finalStateTokenList = tokenList.get(3);
        for(Token token : finalStateTokenList)
        {
            finalStates.add(token.Value);
        }
        //Ucitavamo alfabet
        this.getAlphabetFromTokens(alphabet,4);
        return startState;
    }
    //uzimamo alfabet iz tokena
    private void getAlphabetFromTokens(Set<Character>alphabet, int tokenListIndex)
    {
        var alphabetTokenList = tokenList.get(tokenListIndex);
        for(Token token : alphabetTokenList)
        {
            alphabet.add(token.Value.charAt(0));
        }
    }
}
