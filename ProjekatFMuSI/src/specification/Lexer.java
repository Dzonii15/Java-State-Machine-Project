package specification;

import nikola.makric.Automata.EpsilonNFA;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Lexer {
    final String path;
    public List<String> specification = null;
    ArrayList<Integer> errorLineNumber = new ArrayList<>();
    //konstruktor koji prima putanju do fajla sa datom specifikacijom
    public Lexer(String p) {
        this.path = p;
        try {
            specification = Files.readAllLines(Paths.get(p));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<List<Token>> LexAnalyze() {
        /*
         * format:
         * tip
         * stanja
         * pocetno
         * finalna
         * alfabet
         * tranzicije
         *  */
        AtomicInteger errorCounter = new AtomicInteger(0);
        List<Token> TypeTokenList = new ArrayList<>(1);
        //Prva linija bi trebala da ima flag za vrstu automata koji se gradi
        String typeOfRL = specification.get(0);
        TypeTokenList.add(new Token("RLR", typeOfRL));
        List<List<Token>> listOfTokens = new ArrayList<>();
        listOfTokens.add(TypeTokenList);
        //ovisno koju smo reprezentaciju procitali pozivamo odgovarajucu funkciju
        switch (typeOfRL) {
            case "DFA" -> listOfTokens.addAll(this.analyzeDFA(errorCounter));
            case "NFA" -> listOfTokens.addAll(this.analyzeENFA(errorCounter));
            case "RE" -> listOfTokens.addAll(this.analyzeRE(errorCounter));
            default -> {this.increaseErrorCounter(errorCounter);errorLineNumber.add(0);}
        }
        if (errorCounter.get() > 0)
            return null;
        return listOfTokens;
    }

    private List<List<Token>> analyzeENFA(AtomicInteger errorCounter) {
        List<List<Token>> listOfTokens = new ArrayList<>();
        //Koristimo istu funkciju za inicijalizaciju kao kod DKA jer isti je format
        this.analyzeUpTillTransitions(listOfTokens, errorCounter);
        int globalCounter = 5;
        //Dalje gledamo tranzicije koje sad kao odrediste primaju skup stanja
        List<Token> transitionTokenList = new ArrayList<>();
        for (; globalCounter < specification.size(); globalCounter++) {
            String trueTransition = "";
            //Ista pretpostavka da su tranzicije i stringovi koje korisnik hoce provjeriti razdvojeni enterom
            if (specification.get(globalCounter).equals("Stringovi")) {
                globalCounter++;
                break;
            }
            try {
                String transition = specification.get(globalCounter);
                //Dijelimo na tri stringa
                String[] transitionInParts = transition.split("-");
                //Provjeravamo da li odgovara formatu, tj da li sam dobio ulazno stanje, simbol, izlazni skup stanja
                if (transitionInParts.length != 3) {
                    throw new LexerFaultException();
                }
                //Gradimo tranziciju koju cemo proslijediti u token koji nema bjeline
                //Prolazim kroz prva dva dijela ulazno stanje i simbol
                for (int i = 0; i < 2; i++) {
                    //uklanjamo bjeline
                    String transitionPart = transitionInParts[i].trim();
                    //ukoliko je duzina stringa 0 los format
                    if (transitionPart.length() == 0) throw new LexerFaultException();
                    //Provjeravam da na mjesto simbola nije poslat neki string duzine >=2
                    if (i == 1 && transitionPart.length() > 1) {
                        throw new LexerFaultException();
                    }
                    boolean checker;
                    //Provjera da stringovi sadrze samo cifre ili slova, eventualno da provjeravamo $ za oznaku praznog stringa
                    if (i == 0) checker = this.checkIfWordAlphaNum(transitionPart, false);
                    else checker = this.checkIfWordAlphaNum(transitionPart, true);
                    if (!checker) throw new LexerFaultException();
                    trueTransition += transitionPart + "-";
                }
                if (transitionInParts[2].length() == 0)
                    throw new LexerFaultException();
                //dohvatamo stanja iz skupa stanja u koje se prelazi
                var setOfStates = transitionInParts[2].split(",");
                for (int i = 0; i < setOfStates.length; i++) {
                    String trimmedState = setOfStates[i].trim();
                    //Provjeravamo slucaj ,,
                    if (trimmedState.length() == 0) throw new LexerFaultException();
                    boolean checker = this.checkIfWordAlphaNum(trimmedState, false);
                    if (!checker) throw new LexerFaultException();
                    if (i == setOfStates.length - 1) trueTransition += trimmedState;
                    else trueTransition += trimmedState + ",";

                }
            } catch (LexerFaultException e) {
                this.increaseErrorCounter(errorCounter);
                errorLineNumber.add(globalCounter);
            }
            transitionTokenList.add(new Token("transition", trueTransition));
        }
        //Dohvatamo stringove koji ce se iskoristiti za provjeru
        listOfTokens.add(transitionTokenList);
        this.getStringsToCheck(globalCounter, listOfTokens);
        return listOfTokens;
    }

    private void analyzeUpTillTransitions(List<List<Token>> listOfTokens, AtomicInteger errorCounter) {
        //Prvo moramo analizirati stanja koja se nalaze u prvom redu
        List<Token> statesTokenList = new ArrayList<>();
        try {
            statesTokenList = this.lexStates(1);
        } catch (LexerFaultException e) {
            errorLineNumber.add(1);
            this.increaseErrorCounter(errorCounter);
        }
        List<Token> startStateToken = new ArrayList<>();
        String trimmedStartState = specification.get(2).trim();
        try {
           boolean result =  this.checkIfWordAlphaNum(trimmedStartState, false);
           if(!result)throw new LexerFaultException();
            startStateToken.add(new Token("startState", trimmedStartState));
        } catch (LexerFaultException e) {
            errorLineNumber.add(2);
            this.increaseErrorCounter(errorCounter);
        }
        //Finalna stanja
        List<Token> finalStatesTokenList = new ArrayList<>();
        try {
            finalStatesTokenList = this.lexStates(3);
        } catch (LexerFaultException e) {
            errorLineNumber.add(3);
            this.increaseErrorCounter(errorCounter);
        }
        //Cetvrta linija bi bio alfabet
        List<Token> alphabetTokenList = new ArrayList<>();
        try {
            alphabetTokenList = this.analyzeAlphabet(4);
        } catch (LexerFaultException e) {
            errorLineNumber.add(4);
            this.increaseErrorCounter(errorCounter);
        }
        listOfTokens.add(statesTokenList);
        listOfTokens.add(startStateToken);
        listOfTokens.add(finalStatesTokenList);
        listOfTokens.add(alphabetTokenList);
    }

    private List<List<Token>> analyzeRE(AtomicInteger errorCounter) {
        List<List<Token>> listOfTokens = new ArrayList<>();
        //Ako mi je u prvoj liniji oznaka da je rijec o regularnom izrazu, u drugoj ce biti alfabet
        List<Token> alphabetTokenList = new ArrayList<>();
        try {
            alphabetTokenList = this.analyzeAlphabet(1);
        } catch (LexerFaultException e) {
            errorLineNumber.add(1);
            this.increaseErrorCounter(errorCounter);
        }
        //Pravim token od samo regularnog izraza
        List<Token> regexTokenList = new ArrayList<>(1);
        //dohvatam regularni izraz
        String trimmedRegEx = specification.get(2).trim();
        //provjera regularnog izraza
        boolean checker = this.checkRegExpr(trimmedRegEx);
        if (!checker) {
            this.increaseErrorCounter(errorCounter);
            errorLineNumber.add(2);
        }
        regexTokenList.add(new Token("RE", trimmedRegEx));
        listOfTokens.add(alphabetTokenList);
        listOfTokens.add(regexTokenList);
        //Uzimanje stringova za provjeru
        this.getStringsToCheck(4, listOfTokens);
        return listOfTokens;

    }

    private boolean checkRegExpr(String trimmedRegEx) {
        //pored alfabeta ovo su jos dozvoljeni stringovi
        var allowedCharacters = Arrays.asList('(', ')', '*', '|', '.');
        for (Character toCheck : trimmedRegEx.toCharArray()) {
            try {
                //provjeravam da li su unutar regularnog izraza dozvoljeni karakteri
                if (!allowedCharacters.contains(toCheck) && !this.checkIfWordAlphaNum(toCheck.toString(), true)) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }
    //funkcija analizira alfabet date reprezentacije regularnog jezika
    private List<Token> analyzeAlphabet(int indexAt) throws LexerFaultException {
        List<Token> alphabetTokenList = new ArrayList<>();
        //Splitamo da bi dobili simbole alfabeta
        String[] alphabet = specification.get(indexAt).split(",");
        for (var symbol : alphabet) {
            //trimujemo string da bi izdvojili konkretno simbol
            String trimmedSymbol = symbol.trim();
            //provjera da nije slucajno korisnik proslijedio vise od jednog simbola
            if (trimmedSymbol.length() > 1) throw new LexerFaultException();
            //provjera da li je alfanumericki simbol ili mozda prazan string
            boolean checker = this.checkIfWordAlphaNum(trimmedSymbol, true);
            if (!checker) throw new LexerFaultException();
            alphabetTokenList.add(new Token("alphabetSymbol", trimmedSymbol));
        }
        return alphabetTokenList;
    }

    private boolean checkIfWordAlphaNum(String toCheck, boolean checkingAlphabet) throws LexerFaultException {
        //boolean checkingAlphabet ako je true i ako naidjemo na karakter koji nije ni cifra ni slovo onda provjerava da nije simbol
        //za praznu rijec jer to se moze naci kod alfabeta
        //Prolazim kroz niz karaktera
        for (Character character : toCheck.toCharArray()) {
            //Ako karakter nije cifra niti slovo a provjeravam alfabet ucu u unutrasnji if da provjerim da nije simbol $
            if (!Character.isLetterOrDigit(character)) if (checkingAlphabet) {
                return character == EpsilonNFA.eps;
            } else return false;

        }
        return true;
    }

    private void increaseErrorCounter(AtomicInteger errorCounter) {
        errorCounter.set(errorCounter.intValue() + 1);
    }

    private List<List<Token>> analyzeDFA(AtomicInteger errorCounter) {
        List<List<Token>> listOfTokens = new ArrayList<>();
        this.analyzeUpTillTransitions(listOfTokens, errorCounter);
        int globalCounter = 5;


        //Od pete pozicije pa nadalje bi trebale da budu tranzicije
        List<Token> transitionTokenList = new ArrayList<>();
        //for petlja kroz sve te tranzicije
        for (; globalCounter < specification.size(); globalCounter++) {
            //Pretpostavka da po pravilnom formatu tranzicije i stringove za provjeru ce razdvojiti enterom
            if (specification.get(globalCounter).equals("Stringovi")) {
                globalCounter++;
                break;
            }
            //Hocu kada vratim token da imam ispravan format bez nepotrebnih praznina
            String trueTransition = "";
            try {
                //Uzimam jednu tranziciju, prvi korak je da trimujem, tranziciju kakvu jeste jer moguce su bjeline oko samog formata tranzicije
                String transition = specification.get(globalCounter).trim();
                //Splitam na tri dijela
                String[] transitionInParts = transition.split("-");
                //Automatska provjera formata da li sam dobio tri dijela, stanje,simbol i stanje
                if (transitionInParts.length != 3) {
                    throw new LexerFaultException();
                }


                //Sad analiziram sva tri dijela posebno od date tranzicije
                for (int i = 0; i < 3; i++) {
                    //trimam taj dio ukoliko ima i on bjelina
                    String trimmedPart = transitionInParts[i].trim();
                    //Provjeravam da li po foramatu simbol alfabeta je duzine 1
                    if (i == 1 && trimmedPart.length() > 1) {
                        throw new LexerFaultException();

                    }
                    //Provjeravam da li stanje/simbol ispunjava uslov da sadrzi broj ili cifru
                    boolean checker;
                    if (i == 1)
                        checker = this.checkIfWordAlphaNum(trimmedPart, true);
                    else //ako provjeravam za stanje ne gledam za prazan string
                        checker = this.checkIfWordAlphaNum(trimmedPart, false);
                    if (!checker) throw new LexerFaultException();
                    if (i == 2) trueTransition += trimmedPart;
                    else trueTransition += trimmedPart + "-";
                }
            } catch (LexerFaultException e) {
                this.increaseErrorCounter(errorCounter);
                errorLineNumber.add(globalCounter);
            }
            //Konacno kreiram token koji je ispravnog formata
            transitionTokenList.add(new Token("transition", trueTransition));
        }
        //Funkcija dohvata stringove koje korisnik hoce da provjeri da li automat prihvata
        this.getStringsToCheck(globalCounter, listOfTokens);
        listOfTokens.add(transitionTokenList);
        return listOfTokens;

    }
    //provjerava i dohvata stringove koje je korisnik zadao kako bi provjerio da li pripadaju datoj reprezentaciji regularnog jezika
    private void getStringsToCheck(int globalCounter, List<List<Token>> listOfTokens) {
        List<Token> acceptStringTokenList = new ArrayList<>();
        for (; globalCounter < specification.size(); globalCounter++) {
            String trimmedAcceptString = specification.get(globalCounter).trim();
            acceptStringTokenList.add(new Token("acceptString", trimmedAcceptString));
        }
        listOfTokens.add(acceptStringTokenList);
    }

    private List<Token> lexStates(int line) throws LexerFaultException {
        List<Token> TokenListOfStates = new ArrayList<>();
        //Ucitamo niz stanja koja se nalaze na datoj liniji i izvrsimo ekstrakciju pojedinacnih split metodom
        String[] potentialListOfTokens = specification.get(line).split(",");
        //Prolazim kroz dati niz stanja
        for (var state : potentialListOfTokens) {
            //Cistimo stanje od nepotrebnih bjelina
            var trimmedState = state.trim();
            //Provjeravamo da nije unesen prazan string to je u slucaju (,,), kada imamo dva zareza
            if (trimmedState.length() == 0) {
                throw new LexerFaultException();
            }
            //Provjeravamo da li dato stanje u imenu ima ista drugo osim cifara i slova alfabeta
            boolean checker = this.checkIfWordAlphaNum(trimmedState, false);
            if (!checker) throw new LexerFaultException();
            TokenListOfStates.add(new Token("state", trimmedState));
        }
        return TokenListOfStates;


    }


}
