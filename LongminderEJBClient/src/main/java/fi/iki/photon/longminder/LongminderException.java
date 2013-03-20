package fi.iki.photon.longminder;

public class LongminderException extends Exception {
    private static final long serialVersionUID = 1L;
    
    String message;
    
    public LongminderException(String message) {
        this.message = message;
    }
    public String toString() { return message; }
    
}
