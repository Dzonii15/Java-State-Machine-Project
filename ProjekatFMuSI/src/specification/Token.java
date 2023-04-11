package specification;

public class Token {
    String Type;
    String Value;

    public Token() {
        Value = Type = null;
    }

    public Token(String type, String value) {
        Value = value;
        Type = type;
    }

    public String getType() {
        return Type;
    }

    public String getValue() {
        return Value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this.getClass().getCanonicalName() != obj.getClass().getCanonicalName())
            return false;
        Token objToCompare = (Token) obj;
        if (!objToCompare.Type.equals(this.Type) || !objToCompare.Value.equals(this.Value))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 7 * hash + this.Value.hashCode();
        hash = 7 * hash + this.Type.hashCode();
        return hash;
    }
}
