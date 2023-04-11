package nikola.makric.Generator;
import nikola.makric.Automata.DFA;

public class CodeGenerator {
    //kod koji treba generisati,smjesten u string
    String generatedCode = "";
    int curlyBracketsCounter = 0;
    DFA dfaSpec = null;
    //u konstruktoru primamo dfa na osnovu kojeg cemo kreirati kod
    public CodeGenerator(DFA dfa) {
        this.dfaSpec = dfa;
        generatedCode += "import java.util.ArrayList;\n" +
                "import java.util.function.Consumer;\n  " +
                "public class Automata";
        this.addCurlyBracketBegining();
        String transitonForSymbols = generateTransitionReactions();
        generatedCode += "   public boolean initiateSimulation(String input,Reaction enter,Reaction exit," +transitonForSymbols
                +")";
        this.addCurlyBracketBegining();

    }
    //generise reakcije za svaki simbol
    private String generateTransitionReactions() {
        String stringToGenerate = "";
        var alphabetArray = dfaSpec.getAlphabet().toArray();
        for (int i = 0; i < alphabetArray.length; i++) {
            stringToGenerate += "Reaction transition" + (Character)alphabetArray[i];
            if (!(i == alphabetArray.length - 1))
                stringToGenerate += ",";
        }
        return stringToGenerate;
    }

    public void simulationGenerator() {
        generateBegining();
        //prolazak kroz ulazni string
        this.generatedCode += "   for(Character symbol : input.toCharArray())";
        this.addCurlyBracketBegining();
        //ulazna reakcija
        generatedCode+="    enter.implementReactions(currentState)";
        addSemicolon();
        //implementiraj switch case za stanja
        generateStateSwitchCase();
        //izlazna reakcija
        generatedCode+="    exit.implementReactions(currentState)";
        addSemicolon();
        generatedCode += "  ";
        //zatvaram za petlju
        addCurlyBracketEnd();
        //generisati switch-case za provjeru ispravnosti stringa
        generateSwitchCase();
        generatedCode += " ";
        //zatvaram za funkciju
        addCurlyBracketEnd();
        generateMainFunction();
        //zatvaram za klasu
        addCurlyBracketEnd();
        generateReactionClass();
        addCurlyBracketEnd();
        printGeneratedCode();
        System.out.println(curlyBracketsCounter);
    }
    //switch case za provjeru da li stanje u koje smo dosli jeste finalno
    public void generateSwitchCase()
    {
        generatedCode+="    boolean checker;\n    switch(currentState)";
        addCurlyBracketBegining();
        for(String state : dfaSpec.getFinalStates())
        {
            generatedCode+="     case \""+state+"\":\n"+
                    "      checker = true;\n"+
                    "     break";
            addSemicolon();
        }
        generatedCode+="     default: \n"+
                "      checker = false";
        addSemicolon();
        generatedCode+="    ";
        addCurlyBracketEnd();
        generatedCode+="    return checker";
        addSemicolon();
    }
    //funkcija dodaje u string main funkciju
    public void generateMainFunction()
    {
        generatedCode+="   public static void main(String[]args)";
        addCurlyBracketBegining();
        generatedCode += " ";
        //zatvaram za funkciju
        addCurlyBracketEnd();
    }
    //generise klasu reakcija, koja u sebi ima listu Consumer-a koji su parametrizovani String atributom
    //ciji objekat ce predstavljati ulancane reakcije
    public void generateReactionClass() {
        generatedCode += "class Reaction";
        addCurlyBracketBegining();
        generatedCode += "  ArrayList<Consumer<String>> list = new ArrayList<>();\n" +
                "  public void addReaction(Consumer<String>cons)";
        addCurlyBracketBegining();
        generatedCode += "   list.add(cons)";
        addSemicolon();
        generatedCode += " ";
        addCurlyBracketEnd();
        generatedCode += "  public void implementReactions(String state)";
        addCurlyBracketBegining();
        generatedCode += "   for(var reaction : list)\n    reaction.accept(state)";
        addSemicolon();
        generatedCode += " ";
        addCurlyBracketEnd();
    }
    //generisemo switch case za sva stanja
    public void generateStateSwitchCase() {
        this.generatedCode += "    switch(currentState)";
        this.addCurlyBracketBegining();
        for(var state : dfaSpec.getStates()) {
            generatedCode+="    case \""+state+"\":\n     ";
            addCurlyBracketBegining();
            for (var symbol : dfaSpec.getAlphabet()) {
                generateSymbolIf(state, symbol);
            }
            generatedCode+="     ";
            addCurlyBracketEnd();
            generatedCode += "   break";
            addSemicolon();
        }
        generatedCode += "   ";
        addCurlyBracketEnd();
    }
    //generisemo if blok za jednu tranziciju
    private void generateSymbolIf(String state, Character symbol) {
        generatedCode += "     if(symbol.equals('" + symbol + "'))";
        addCurlyBracketBegining();
        generatedCode += "      currentState = \"" + dfaSpec.getTransition(state, symbol) + "\"";
        addSemicolon();
        generatedCode+="     transition"+symbol+".implementReactions(currentState)";
        addSemicolon();
        generatedCode += "      ";
        addCurlyBracketEnd();
    }
    //dodaje }
    private void addCurlyBracketEnd() {
        generatedCode += "}\n";
        curlyBracketsCounter--;
    }
    //dodaje {
    private void addCurlyBracketBegining() {
        generatedCode += "{\n";
        curlyBracketsCounter++;
    }

    private void printGeneratedCode() {
        System.out.println(this.generatedCode);
    }
    //funkcija dodaje ;
    private void addSemicolon() {
        this.generatedCode += ";\n";
    }
    //generise pocetak tj dodjelu pocetnog stanja
    private void generateBegining() {
        this.generatedCode += "   String currentState = \"" + dfaSpec.getStartState() + "\"";
        this.addSemicolon();
    }
}

