package nikola.makric.Automata;

import nikola.makric.helperClasses.Pair;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EpsilonNFA implements Convertable {
    public static final Character eps = 'ε';

    //Pocetno stanje
    private String startState;
    //Alfabet
    private Set<Character> alphabet = new HashSet<>();
    //Skup stanja
    private Set<String> states = new HashSet<>();
    //Skup finalnih stanja
    private Set<String> finalStates = new HashSet<>();
    //Delta Funkcija
    private Hashtable<Pair<String, Character>, Set<String>> deltaFunction = new Hashtable<>();

    //Podrazumijevani konstruktor
    public EpsilonNFA() {
        //default dodajemo epsilon u alphabet
        alphabet.add(eps);
    }

    //Dodaje jedno stanje u skup stanja ukoliko vec ne postoji i postavlja jednu epsilon tranziciju da ide u isto stanje
    public void addOneState(String state) {
        states.add(state);
        Set<String> epsilonTransition = new HashSet<>();
        epsilonTransition.add(state);
        try {
            this.addTransition(state, eps, new String[]{state});
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //Postavlja citav skup stanja na osnovu niza Stringova
    public void addWholeSetOfStates(String[] setOfStates) {
        for (String state : setOfStates) {
            this.addOneState(state);
        }
    }
    //postavlja stanja na osnovu skupa koji se poslao
    public void setStates(Set<String>states)
    {
        this.states = states;
    }

    //Postavlja pocetno stanje i ako vec nije dodato, dodaje ga u skup stanja
    public void addStartState(String sState) {
        this.startState = sState;
        this.addOneState(sState);
    }
    //geter za pocetno stanje
    public String getStartState() {
        return this.startState;
    }

    //Dodaje simbol u alfabet
    public void addToAlphabet(Character symbol)  {
        this.alphabet.add(symbol);
    }

    //Postavlja citav alfabet, kao ulaz prima niz Stringova
    public void setWholeAlphabet(Character[] alphabetArray)  {
        for (Character symbol : alphabetArray) {
            this.addToAlphabet(symbol);
        }
    }
    //vraca skup stanja
    public Set<String> getStates()
    {
        return this.states;
    }
    //postavlja alfabet
    public void setAlphabet(Set<Character> alphabet) {
        this.alphabet = alphabet;
    }
    //vraca alfabet
    Set<Character> getAlphabet() {
        return this.alphabet;
    }

    //Dodaje finalna stanja
    public void addFinalState(String fState) {
        //Ukoliko stanje ne postoji u citavom skupu stanja, dodajemo ga i tu
        this.finalStates.add(fState);
        this.addOneState(fState);
        ;
    }
    //postavlja finalna stanja
    public void setFinalStates(Set<String> finalStates) {
        this.finalStates = finalStates;
    }
    //postavlja finalna stanja ali za svako stanje poziva funkciju addFinalState kako bi se inicijalizovali epsilon prelazi i za svaki slucaj dodao u skup stanja
    public void setFinalStatesWithCheckup(Set<String>finalStates)
    {
        for(String state : finalStates)
            this.addFinalState(state);
    }

    //Postavlja sva finalna stanja, kao ulaz prima niz finalnih stanja
    public void addAllFinalStates(String[] fStates) {
        for (String fState : fStates) {
            this.addFinalState(fState);
        }
    }
    //vraca finalna stanja
    public Set<String> getFinalStates() {
        return this.finalStates;
    }
    //provjerava da li je stanje finalno
    public boolean isItFinal(String state)
    {
        return this.finalStates.contains(state);
    }

    //Dodajemo tranziciju za delta funkciju
    public void addTransition(String state, Character sym, String[] stateArray) throws Exception {
        //ako to stanje nije u skupu stanja i simbol nije u alfabetu bacamo izuzetak
        if (!(this.states.contains(state)) || !(this.isItInAlphabet(sym)))
            throw new Exception();
        //pravimo novi skup stanja u koji prelazi automat
        Set<String> stateSet = new HashSet<>();
        for (String inputState : stateArray) {
            //provjeravamo da li stanja koja je korisnik poslao pripadaju skupu stanja
            if (!(this.states.contains(inputState)))
                throw new Exception();
            //dodajemo u skup
            stateSet.add(inputState);
        }
        Pair domain = new Pair<String, Character>(state, sym);
        if (deltaFunction.get(domain) == null)//ukoliko nemamo takvo mapiranje postavljamo novo
            deltaFunction.put(new Pair<String, Character>(state, sym), stateSet);
        else {//ukoliko imamo dodajemo u vec postojece mapiranje stanja koja su poslata kao argument funkcije, prosirujemo taj skup
            (deltaFunction.get(domain)).addAll(stateSet);
        }
    }
    //vraca tranziciju na osnovu ulaznog stanja i simbola
    public Set<String> getTransition(String state, Character symbol)
    {
        return this.deltaFunction.get(new Pair<>(state,symbol));
    }
    //vraca delta funkciju automata
    public Hashtable<Pair<String,Character>,Set<String>> getDeltaFunction()
    {
        return this.deltaFunction;
    }
    //postavlja delta funkciju
    public void setDeltaFunction(Hashtable<Pair<String, Character>, Set<String>> delta) {
        this.deltaFunction = delta;
    }

    //Glavna funkcija koja provjerava da li je prihvacen ulazni string ili ne
    //pratimo definiciju prosirene delta funkcije
    public boolean acceptsString(String input) throws Exception {
        Set<String> endStates = new HashSet<>();
        //u skup konacnih stanja dodajemo pocetno stanje
        endStates.add(this.startState);
        //uzimamo epsilon Closure od tog pocetnog stanja
        endStates = this.eClosure(endStates);
        //pozivamo funkciju koja mi vraca konacna stanja na osnovu ulaza
        endStates = this.getEndStates(endStates, input);
        //provjeravamo da li ijedno stanje iz tih konacnih stanja je finalno
        for (String endState : endStates) {
            if (this.finalStates.contains(endState))
                return true;
        }
        return false;
    }

    public Set<String> getEndStates(Set<String> endStates, String input) throws Exception {
        //za svaki simbol koji se nalazi u ulaznom stringu
        for (char sym : input.toCharArray()) {
            //provjeravamo da li taj simbol pripada alfabetu
            if (!(this.isItInAlphabet(sym)))
                //ako ne pripada bacamo izuzetak
                throw new Exception("This character is not in the alphabet");
            //pomocni skup u koji cemo staviti rezultate prelaza za dati simbol
            Set<String> tmpStateSet = new HashSet<>();
            //za svako stanje iz konacnih stanja
            for (String state : endStates) {
                //dohvatamo tranziciju
                Pair domain = new Pair<String, Character>(state, sym);
                //ukoliko postoji ta tranzicija
                if (deltaFunction.get(domain) != null)
                    //dodajemo u privremeni skup
                    tmpStateSet.addAll(this.deltaFunction.get(domain));

            }
            //konacna stanja su onda jednaka epsilon tranziciji stanja koje smo dobili u privremenom skupu
            endStates = this.eClosure(tmpStateSet);

        }
        return endStates;
    }

    //Pomocna funkcija za Epsilon Closur nekog skupa stanja
    public Set<String> eClosure(Set<String> states) {
        //skup stanja koji je zapravo rezultat klozura
        Set<String> newStates = new HashSet<>();
        newStates.addAll(states);
        //privremeni skup
        Set<String> tmp = new HashSet<>();
        do {
            tmp.addAll(newStates);
            //prolazimo kroz stanja u privremenom i uzimamo sve epsilon tranzicije
            for (String state : tmp) {
                newStates.addAll(deltaFunction.get(new Pair<>(state, eps)));
            }

        } while (!(tmp.equals(newStates)));
        return newStates;
    }

    //Provjerava da li je dati simbol u alfabetu
    private boolean isItInAlphabet(Character symbol) {
        return this.alphabet.contains(symbol);
    }
}
