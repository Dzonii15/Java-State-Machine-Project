package nikola.makric.Automata;

import nikola.makric.helperClasses.Pair;

import java.util.*;

public class RegularExpression implements Convertable {
    public static final Character union = '|';//najmanji prioritet
    public static final Character kleeneStar = '*';//najveci
    public static final Character concat = '.';//srednje
    //string koji predstavlja regularni izraz
    private String regularExpression = "";
    //alfabet regularnog izraza
    private Set<Character> alphabet = new HashSet<>();
    //tabela prioriteta operatora
    Hashtable<Character, Pair<Integer, Integer>> priorityOfOperators = new Hashtable<>();

    //Podrazumijevani konstruktor u kome se inicijalizuju prioriteti operatora
    public RegularExpression() {
        //postavka prioriteta
        alphabet.add(EpsilonNFA.eps);
        priorityOfOperators.put('(', new Pair<>(6, 0));
        priorityOfOperators.put(')', new Pair<>(1, -10));
        priorityOfOperators.put('|', new Pair<>(2, 2));
        priorityOfOperators.put('.', new Pair<>(3, 3));
        priorityOfOperators.put('*', new Pair<>(4, 4));
    }

    //funkcija za provjeru prihvatanja ulaznog stringa od strane regularnog izraza
    public boolean acceptString(String input) {
        //vrsimo konverziju izraza i pozivamo njegovu acceptsString funkciju
        DFA equivavelntAutomata = (DFA) ConversionClass.toDFA(this);
        try {
            boolean result = equivavelntAutomata.acceptsString(input);
            return result;
        } catch (Exception e) {
            return false;
        }
    }

    //funkcija koja provjerava ispravnost regularnog izraza, provjerava se na osnovu pretvaranja izraza u postfix
    public boolean assureCorectness() {
        Stack<Character> stack = new Stack<>();
        int rank = 0;
        var stringArray = regularExpression.toCharArray();
        Character next = stringArray[0];
        int i = 1;
        while (next != null) {
            if (!alphabet.contains(next) && next != '*' && next != '|' && next != '.' && next != '(' && next != ')') {
                regularExpression = null;
                return false;
            }
            if (alphabet.contains(next)) {
                rank += 1;
            } else {
                while (!(stack.empty()) && (priorityOfOperators.get(next).First() <= priorityOfOperators.get(stack.peek()).Second())) {
                    Character x = stack.pop();
                    if (x != '*')
                        rank -= 1;
                    if (rank < 1)
                        return false;

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
            if (x != '*')
                rank -= 1;
        }
        if (rank != 1) {
            System.out.println("Nepravilan izraz");
            return false;
        }
        return true;

    }

    //vraca tabelu prioriteta operatora
    public Hashtable<Character, Pair<Integer, Integer>> getPriorityOfOperators() {
        return this.priorityOfOperators;
    }

    //postavlja regularni izraz
    public void setRegularExpression(String re) {
        this.regularExpression = re;
    }

    //geter za regularni izraz
    public String getRegularExpression() {
        return this.regularExpression;
    }
    //postavljanje alfabeta regularnog izraza na osnovu niza

    public void setWholeAlphabet(Character[] alphabet) {
        Collections.addAll(this.alphabet, alphabet);
    }

    //postavljanje alfabeta na osnovu skupa
    public void setAlphabet(Set<Character> alphabet) {
        this.alphabet = alphabet;
    }

    //geter za alfabet
    public Set<Character> getAlphabet() {
        return this.alphabet;
    }

    //provjera da li dati simbol pripada alfabetu
    public boolean isItInAlphabet(Character symbol) {
        return this.alphabet.contains(symbol);
    }


}
