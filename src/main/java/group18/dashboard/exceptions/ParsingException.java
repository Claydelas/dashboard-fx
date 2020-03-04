package group18.dashboard.exceptions;

public class ParsingException extends Exception {
    private String message;
    public ParsingException(String m){
        message = m;
    }
    @Override
    public String getMessage() {
        return "Error parsing the file. "+message;
    }
}
