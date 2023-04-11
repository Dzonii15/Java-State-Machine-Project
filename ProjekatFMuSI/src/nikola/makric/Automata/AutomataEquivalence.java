package nikola.makric.Automata;

import nikola.makric.helperClasses.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class AutomataEquivalence {

    public static boolean areTheyEqual(Convertable object1, Convertable object2) {
        DFA first;
        DFA second;
        //Konvertujem obije reprezentacije u DFA i minimizujem ih
        first = (DFA) ConversionClass.toDFA(object1);
        second = (DFA) ConversionClass.toDFA(object2);
        first.minimizeDFA();
        second.minimizeDFA();
        Set<Character> firstAlphabet = first.getAlphabet();
        Set<Character> secondAlphabet = second.getAlphabet();
        //osnovni preduslov jeste da su im alfabeti jednaki
        if (!firstAlphabet.equals(secondAlphabet)) {
            return false;
        }
        //algoritam radi tako sto pravi parove stanje i uporedo obilazi i jedan i drugi automat
        //prvi par odakle algoritam krece bice par pocetnih stanja oba automata
        Queue<Pair<String, String>> queue = new LinkedList<>();
        queue.add(new Pair<>(first.getStartState(), second.getStartState()));
        Set<Pair<String, String>> checked = new HashSet<>();
        boolean checker = true;
        while (!queue.isEmpty()) {//vrsimo obilazak
            var toCheck = queue.remove();
            checked.add(toCheck);
            for (Character sym : firstAlphabet) {
                //dohvatamo sledeci par na osnovu trenutnog para i simbola koji posmatramo
                var pair = new Pair<>(first.getTransition(toCheck.First(), sym), second.getTransition(toCheck.Second(), sym));
                if(!checked.contains(pair)) {
                    //automati nisu jednaki ukoliko se u paru nadje situacija da je jedno finalno stanje a drugo nije
                    if ((first.isItFinalState(pair.First()) && !second.isItFinalState(pair.Second())) ||
                            (!first.isItFinalState(pair.First()) && second.isItFinalState(pair.Second()))) {
                        checker = false;
                        break;
                    } else {
                        queue.add(pair);
                    }
                }
            }
                if(!checker)
                    break;
            }
        return checker;
        }
    }
