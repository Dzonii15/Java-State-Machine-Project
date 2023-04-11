package nikola.makric.Automata;

import nikola.makric.helperClasses.Pair;

import java.util.*;

public class DFA implements Convertable {
    //simbol za prazan string
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
    private Hashtable<Pair<String, Character>, String> deltaFunction = new Hashtable<>();

    //podrazumijevani konstruktor
    public DFA() {
        this.alphabet.add(eps);
    }

    //postavlja pocetno stanje
    public void addStartState(String sState) {
        this.startState = sState;
        this.addOneState(sState);
    }

    //vraca pocetno stanje
    public String getStartState() {
        return this.startState;
    }

    // dodaje jedno stanje u skup stanja i automatski dodaje odgovarajucu epsilon tranziciju za njega
    public void addOneState(String state) {
        states.add(state);
        try {
            this.addTransition(state, eps, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //postavlja citav skup stanja na osnovu niza
    public void addWholeSetOfStates(String[] setOfStates) {
        for (String state : setOfStates) {
            this.addOneState(state);
        }
    }

    //vraca skup stanja
    public Set<String> getStates() {
        return this.states;
    }

    //vraca alfabet
    public Set<Character> getAlphabet() {
        return this.alphabet;
    }

    //vraca tranziciju na osnovu stanja i simbola
    public String getTransition(String state, Character sym) {
        return this.deltaFunction.get(new Pair<>(state, sym));
    }

    //postavlja skup stanja na osnovu proslijedjenog skupa
    public void setStateSet(Set<String> stateSet) {
        this.states = stateSet;
        this.initializeEpsilonTransitions();
    }

    //postavlja finalna stanja
    public void setFStateSet(Set<String> fStateSet) {
        this.finalStates = fStateSet;
    }

    //dodaje jedno po jedno finalno stanje
    public void setFinalStatesWithCheck(Set<String> fStateSet) {
        for (String state : fStateSet) {
            this.addFinalState(state);
        }
    }

    //postavlja citav alfabet
    public void setAlphabetSet(Set<Character> alphabetSet) {
        this.alphabet = alphabetSet;
    }

    //dodaje jedan simbol alfabetu
    public void addToAlphabet(Character symbol) {
        this.alphabet.add(symbol);
    }

    //postavlja alfabet na osnovu proslijedjenog niza
    public void setWholeAlphabet(Character[] alphabetArray) {
        for (Character symbol : alphabetArray) {
            this.addToAlphabet(symbol);
        }
    }

    //dodaje jedno finalno stanje
    public void addFinalState(String fState) {
        //Ukoliko stanje ne postoji u originalnom skupu stanja Q, dodajemo ga i tu
        this.finalStates.add(fState);
        this.addOneState(fState);

    }

    //Postavlja sva finalna stanja, kao ulaz prima niz finalnih stanja
    public void addAllFinalStates(String[] fStates) {
        for (String fState : fStates) {
            this.addFinalState(fState);
        }
    }

    //vraca skup finalnih stanja
    public Set<String> getFinalStates() {
        return this.finalStates;
    }

    //vraca delta funkciju automata
    public Hashtable<Pair<String, Character>, String> getDeltaFunction() {
        return this.deltaFunction;
    }

    //provjerava da li je stanje finalno stanje
    public boolean isItFinalState(String state) {
        return this.finalStates.contains(state);
    }

    //dodaje tranziciju u delta funkciju
    public void addTransition(String state, Character sym, String newState) throws Exception {
        //provjerava da li stanje iz kojeg se prelazi,simbol na osnovu kog se prelazi i stanje u koje se prelazi postoje kao clanovi automata
        // unutar alfabeta i skupa stanja
        if (!(this.states.contains(state)) || !(this.isItInAlphabet(sym)) || !(this.states.contains(newState)))
            throw new Exception();
        var transitionPair = new Pair<>(state, sym);
        //ovdje provjeravamo da li postoji takvo mapiranje vec, ako ne postoji dodajemo
        if (deltaFunction.get(transitionPair) == null) deltaFunction.put(transitionPair, newState);
            //a ako pokusava da promijeni onda se baca izuzetak
        else if (!deltaFunction.get(transitionPair).equals(newState)) throw new Exception();
    }

    //postavljanje delta funkcije
    public void setDeltaFunction(Hashtable<Pair<String, Character>, String> deltaFunction) {
        this.deltaFunction = deltaFunction;
    }

    //osnovna funkcija za provjeravanje da li automat prihvata ulaz
    public boolean acceptsString(String input) throws Exception {
        String currentState = this.startState;
        for (char symbol : input.toCharArray()) {
            //ukoliko imamo neki simbol u ulazu koji nije u alfabetu bacamo izuzetak
            if (!(this.isItInAlphabet(symbol))) throw new Exception("This character is not in the alphabet");
            currentState = this.deltaFunction.get(new Pair<>(currentState, symbol));

        }
        //vracamo rezultat i provjeravamo da li je stanje u kojem smo zavrsili finalno
        return this.finalStates.contains(currentState);
    }

    public void minimizeDFA() {
        //Prvi korak uklanjamo nedostizna stanja pomocu funkcije removeInaccesbileStates()
        removeInaccesbileStates();
        Object[] stateArray = this.states.toArray();
        int matrixDimension = this.states.size();
        int[][] pairMatrix = new int[matrixDimension][matrixDimension];
        //inicijalizujemo matricu za primjenu algoritma
        this.initializeMinimizationMatrix(pairMatrix, stateArray, matrixDimension);
        //Primjena algoritma, popunjavanje matrice kako bi se dobile klase ekvivalencije
        this.myphillNerodeTheorem(pairMatrix, stateArray, matrixDimension);
        //sada treba na osnovu tabele da se izvuku klase ekvivalencije
        HashSet<HashSet<String>> equivalenceStates = new HashSet<>();
        this.defineEquivalencyClasses(equivalenceStates, pairMatrix, stateArray, matrixDimension);
        //generisemo nove atribute DFA automata
        this.defineNewDFAAttributes(equivalenceStates);
    }

    public int shortestWordLength() {
        //ako je startno stanje ujedno i finalno vracamo duzinu najkrace rijeci 0
        if (this.finalStates.contains(this.startState)) {
            return 0;
        }
        //red za cekanje stanja koja trebamo posjetit
        Queue<Pair<String, Integer>> toVisitStates = new LinkedList<>();
        //stanja koja su vec posjecena
        Hashtable<String, Integer> visitedStates = new Hashtable<>();
        toVisitStates.add(new Pair<>(this.startState, 0));
        while (!toVisitStates.isEmpty()) {//sve dok red za stanja koja trebamo posjetiti nije prazan
            var stateToVisit = toVisitStates.remove();//uzimamo prvog iz reda
            visitedStates.put(stateToVisit.First(), stateToVisit.Second());//posjecujemo ga tako sto stavljamo ga u posjecene
            for (Character sym : this.alphabet) {//za svaki simbol gledamo prelaze
                var nextToQueue = this.getTransition(stateToVisit.First(),sym);//uzimamo stanje u koje prelazimo
                if (visitedStates.get(nextToQueue) == null ) { //ako nije posjecen
                    boolean hasAlreadyBeenAddedToQueue = false;//provjeravamo da li je vec u redu za cekanje
                    for(var state : toVisitStates)
                    {
                        if(nextToQueue.equals(state.First())) {
                            hasAlreadyBeenAddedToQueue = true;
                            break;
                        }
                    }
                    if ( this.finalStates.contains(nextToQueue)) {//ako je finalno stanje vracamo nivo tog finalnog stanja
                        return stateToVisit.Second() + 1;
                    }
                    if(!hasAlreadyBeenAddedToQueue)//ako vec nije u redu za cekanje dodajemo ga
                    toVisitStates.add(new Pair<>(nextToQueue, stateToVisit.Second() + 1));
                }
            }
        }
        return -1;
    }

    public int longestWordLength() {
        //Prvo provjeravamo da li je konacan jezik koji prihvata ovaj DFA
        if (FiniteLanguage.isTheLanguageInfinite(this)) {
            System.out.println("Nije konacan jezik, najduza rijec je beskonacna");
            return -1;
        } else {//Ukoliko jeste,vrsimo obilazak automata
            //red stanja koje treba posjetiti
            Queue<String> toVisitStates = new LinkedList<>();
            //skup stanja koja su posjecena
            Set<String> visitedStates = new HashSet<>();
            //hes-tabela koja cuva par stanje-duzina puta
            Hashtable<String, Integer> pathLength = new Hashtable<>();
            pathLength.put(this.startState, 0);
            toVisitStates.add(this.startState);
            while (!toVisitStates.isEmpty()) {//dok red stanja koje treba posjetit se ne isprazni
                var stateToVisit = toVisitStates.remove();//uzimamo sledece stanje iz reda
                for (Character sym : this.alphabet) {//za svaki simbol u alfabetu
                    if (sym != DFA.eps) {//ako je simbol razlicit o epsilona
                        //uzimamo tranziciju iz trenutnog stanja koje posmatramo i simbola iz alfabeta
                        var nextToQueue = this.deltaFunction.get(new Pair<>(stateToVisit, sym));
                        //posjecujemo to stanje
                        visitedStates.add(stateToVisit);
                        if (!visitedStates.contains(nextToQueue)) {//ako stanje vec nije posjeceno
                            toVisitStates.add(nextToQueue);//dodajemo ga u red za posjecivanje
                            pathLength.put(nextToQueue, pathLength.get(stateToVisit) + 1);//i postavljamo duzinu puta do njega
                        } else {//u suprotnom gledamo da li vec postavljena vrijednost je manja od ove duzine puta do datog stanja
                            if (pathLength.get(stateToVisit) + 1 >= pathLength.get(nextToQueue)) {//ako jeste
                                pathLength.put(nextToQueue, pathLength.get(stateToVisit) + 1);//postavljamo ovu duzinu kao duzu u hes tabelu
                            }
                        }
                    }
                }
            }
            int longestPath = 0;
            for (var element : pathLength.entrySet()) {//konacno gledamo koji je to najduzi put do stanja
                if (this.finalStates.contains(element.getKey()))//naravno to stanje mora ujedno biti i finalno
                    if (element.getValue() > longestPath) longestPath = element.getValue();

            }
            return longestPath;//vracamo rezulatat
        }
    }

    public void removeInaccesbileStates() {
        //Da odredimo novi skup stanja uklanjajuci nedostizna
        Set<String> finalSet = new HashSet<>();
        finalSet.add(this.startState);
        //algoritam je baziran na BFS-u i potreban nam je red
        Queue<String> trenutniPrelaz = new LinkedList<>();
        //dodajemo pocetno stanje jer znamo da je ono na pocetku jedino sto posto dostizno
        trenutniPrelaz.add(this.startState);
        do {
            //sledece stanje cije cu susjede posjetiti trazeci neposjeceno stanje
            String stateToVisit = trenutniPrelaz.remove();
            for (Character symbol : this.alphabet) {
                var nextState = this.deltaFunction.get(new Pair<>(stateToVisit, symbol));
                if (!trenutniPrelaz.contains(nextState) && !finalSet.contains(nextState)) {
                    finalSet.add(nextState);
                    trenutniPrelaz.add(nextState);
                }
            }

            //algoritam ce ici dok se vise ne moze prosiriti skup odnosno dok smo posjetili sva moguca dostizna stanja
            //tj dok se red ne isprazni
        } while (!trenutniPrelaz.isEmpty());
        HashSet<String> inaccesibleStates = new HashSet<>();
        //Trazimo sva stanja koja nismo uspjeli dostici, koja su zapravo nedostizna
        for (String state : this.states) {
            if (!finalSet.contains(state)) {
                inaccesibleStates.add(state);

            }

        }
        //uklanjamo ta stanja iz trenutnog skupa stanja
        this.states.removeAll(inaccesibleStates);
    }

    private void defineNewDFAAttributes(HashSet<HashSet<String>> equivalenceStates) {
        Set<String> newStates = new HashSet<>();//novi skup stanja
        Set<String> newFStates = new HashSet<>();//novi skup finalnih stanja
        String newStartState = "";//novo startno stanje
        Hashtable<Pair<String, Character>, String> newDeltaFunction = new Hashtable<>();//nova delta funkcija
        int globalCounter = 0;//Pomocni brojac za imenovanje novih stanja
        //prolazimo kroz sve klase ekvivalencije
        for (HashSet<String> equivalenceClass : equivalenceStates) {
            Set<String> temp = new HashSet<>(equivalenceClass);
            //za svaku klasu ekvivalencije gledamo da li sadrzi u sebi bar jedno finalno stanje
            temp.retainAll(this.finalStates);
            //uzimamo novo ime stanja
            String newName = Integer.toString(globalCounter);
            newStates.add(newName);
            //ako je velicina skupa koji je dobijen presjekom klase ekvivalencije i finalnih stanja trenutnog automata
            //to znaci da je bar jedno stanje u klasi ekvivalencije finalno i da ova klasa treba se zamijeniti stanjem koje
            //ce takodje biti finalno
            if (temp.size() > 0) newFStates.add(newName);
            //ako u sebi sadrzi pocetno stanje, ova klasa ce biti zamijenjena stanjem koje je pocetno u minimizovanom automatu
            if (equivalenceClass.contains(this.startState)) newStartState = newName;
            globalCounter++;
        }
        //definisemo novu deltu za minimizovani automat kao i njegove ostale parametre
        this.defineNewDeltaFunction(equivalenceStates, newDeltaFunction);
        this.states = newStates;
        this.finalStates = newFStates;
        this.startState = newStartState;
        this.deltaFunction = newDeltaFunction;
        this.initializeEpsilonTransitions();
    }

    private void defineNewDeltaFunction(HashSet<HashSet<String>> equivalenceStates, Hashtable<Pair<String, Character>, String> newDeltaFunction) {
        //da pratimo imena stanja
        int globalCounter = 0;
        for (var equivalenceClass : equivalenceStates) {
            //uzimamo nekog predstavnika klase ekvivalencije jer znamo da ce sva stanja iz date klase ekvivalencije
            //za isti simbol u istu klasu ekvivalencije izvrsiti tranziciju
            String pivot = equivalenceClass.iterator().next();
            for (Character symbol : this.alphabet) {
                //nalazimo u koje stanje prelazi pivot za dati simbol
                String stanje = this.deltaFunction.get(new Pair<>(pivot, symbol));
                int br = 0;
                //i trazimo kojoj klasi ekvivalencije to stanje pripada
                for (var equivalnceClass2 : equivalenceStates) {
                    if (equivalnceClass2.contains(stanje)) break;
                    br++;
                }
                //dodajemo u deltu prelaz
                newDeltaFunction.put(new Pair<>(Integer.toString(globalCounter), symbol), Integer.toString(br));

            }
            globalCounter++;
        }
    }

    private void defineEquivalencyClasses(HashSet<HashSet<String>> equivalenceStates, int[][] pairMatrix, Object[] stateArray, int matrixDimension) {
        //oprez promijenio i
        //prolazimo kroz citavu matricu i trazimo par cija celija je jednaka 0 jer taj par pripada istoj klasi ekvivalencije
        for (int i = 0; i < matrixDimension; i++) {
            for (int j = 0; j < i; j++) {
                //ako smo ga nasli
                if (pairMatrix[i][j] == 0) {
                    //promjenljiva koja mi prati da li sam dodao par ijednoj klasi ekvivalencije
                    boolean added = false;
                    String first = (String) stateArray[i];
                    String second = (String) stateArray[j];
                    //prolazim kroz sve klase ekvivalencije
                    for (HashSet<String> equivalenceClass : equivalenceStates) {
                        //ukoliko bar jedan od para pripada nekoj klasi to znaci da i drugi pripada i dodajemo oba datoj klasi
                        if (equivalenceClass.contains(first) || equivalenceClass.contains(second)) {
                            equivalenceClass.add(first);
                            equivalenceClass.add(second);
                            added = true;
                            break;
                        }
                    }
                    //ako nismo dodali kreiracemo novu klasu za taj par i dodati u skup klasa ekvivalencije
                    if (!added) {
                        HashSet<String> newEq = new HashSet<>();
                        newEq.add(first);
                        newEq.add(second);
                        equivalenceStates.add(newEq);
                    }
                }
            }

        }
        //Provjeravamo da li smo dodali sva stanja u odredjene klase ekvivalencije
        boolean checker = true;
        for (String state : this.states) {
            for (HashSet<String> equivalenceClass : equivalenceStates)
                if (equivalenceClass.contains(state)) {
                    checker = false;
                    break;
                }
            //ako nismo, kreiramo zasebnu klasu ekvivalncije za to stanje
            if (checker) {
                HashSet<String> selfClass = new HashSet<>();
                selfClass.add(state);
                equivalenceStates.add(selfClass);

            }
            checker = true;

        }

    }

    private void myphillNerodeTheorem(int[][] pairMatrix, Object[] stateArray, int matrixDimension) {
        //pomocna matrica
        int[][] helperMatrix = new int[matrixDimension][matrixDimension];
        do {
            this.copyMatrix(pairMatrix, helperMatrix);
            for (int i = 0; i < matrixDimension; i++) {
                for (int j = 0; j < i; j++) {
                    //pratimo algoritam, ukoliko je celija matrice za neki par jednaka 0
                    if (pairMatrix[i][j] == 0) {
                        //dohvatamo stanja koja odgovaraju tim pozicijama
                        String first = (String) stateArray[i];
                        String second = (String) stateArray[j];
                        //i za svaki simbol u alfabetu provjeravamo da li prelaz tog para, par koji je obiljezen u matrici sa 1
                        for (Character symbol : this.alphabet) {
                            String firstTransition = this.deltaFunction.get(new Pair<>(first, symbol));
                            String secondTransition = this.deltaFunction.get(new Pair<>(second, symbol));
                            int row = this.search(stateArray, firstTransition);
                            int column = this.search(stateArray, secondTransition);
                            //ukoliko jeste i sam taj par postaje 1
                            if (pairMatrix[row][column] == 1 || pairMatrix[column][row] == 1) {
                                pairMatrix[i][j] = 1;
                                break;
                            }
                        }
                    }
                }
            }
            //ovo sve radimo dok nam matrice ne budu jednake
        } while (!this.areMatrixesEqual(pairMatrix, helperMatrix));
    }

    //Podrazumijeva inicijalizaciju matrice prema algoritmu
    private void initializeMinimizationMatrix(int[][] pairMatrix, Object[] stateArray, int matrixDimension) {
        for (int i = 0; i < matrixDimension; i++) {
            for (int j = 0; j < i; j++) {
                //Kako su parovi stanja predstavljeni preko matrice, ukoliko je bar jedan iz para jednak finalnom stanju vrijednost tog
                // polja jeste 1
                if ((this.finalStates.contains(stateArray[i]) && !this.finalStates.contains(stateArray[j])) ||
                        (!this.finalStates.contains(stateArray[i]) && this.finalStates.contains(stateArray[j])))
                    pairMatrix[i][j] = 1;
                //u suprotnom je nula
                else pairMatrix[i][j] = 0;
            }
        }
    }
    //postavlja epsilon tranzicije za sva stanja da idu u same sebe
    public void initializeEpsilonTransitions() {
        for (String state : this.states) {
            try {
                this.addTransition(state, eps, state);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
    //provjerava da li je simbol u alfabetu
    private boolean isItInAlphabet(Character symbol) {
        return this.alphabet.contains(symbol);
    }
    //pomocna funkcija za kopiranje matrice
    private void copyMatrix(int[][] original, int[][] copy) {
        for (int i = 0; i < original.length; i++)
            System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
    }
    //pomocna funkcija provjerava da li su dvije funkcije iste po elementima
    private boolean areMatrixesEqual(int[][] original, int[][] copy) {
        boolean checker = true;
        for (int i = 0; i < original.length; i++) {
            for (int j = 0; j < i; j++) {
                if (original[i][j] != copy[i][j]) {
                    checker = false;
                    break;
                }
            }
            if (!checker) break;
        }
        return checker;
    }
    //pretrazuje element u nizu
    private int search(Object[] array, String key) {
        for (int i = 0; i < array.length; i++) {
            if (key.equals(array[i])) return i;
        }
        return -1;
    }


}
