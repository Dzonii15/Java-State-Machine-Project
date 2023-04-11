package nikola.makric.helperClasses;

//Pomocna klasa koja je potrebna da predstavi uredjeni par stanja i simbola
public class Pair<K, T> {
    //String predstavlja stanje
    private K first;
    //String predstavlja ulazni simbol
    private T second;

    //Konstruktor
    public Pair(K first, T second) {
        this.first = first;
        this.second = second;
    }

    //Vraca stanje
    public K First() {
        return this.first;
    }

    //Vraca simbol
    public T Second() {
        return this.second;
    }

    //Postavlja stanje
    public void setFirst(K first) {
        this.first = first;
    }

    //Postavlja simbol
    public void setSecond(T second) {
        this.second = second;
    }

    //equals metoda redefinisana, radi drugih funkcija koji nju koriste
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pair) {
            Pair tmp = (Pair) obj;

            if (this.first.equals(tmp.first) && this.second.equals(tmp.second))
                return true;
        }
        return false;

    }

    @Override

    public int hashCode() {
        int hash = 3;
        hash = 7 * hash + this.first.hashCode();
        hash = 7 * hash + this.second.hashCode();
        return hash;
    }

}
