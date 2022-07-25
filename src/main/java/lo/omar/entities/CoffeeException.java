package lo.omar.entities;


public class CoffeeException extends RuntimeException {

    private static final long serialVersionUID = -7740003145832304838L;

    public CoffeeException(String message){
        super(message);
    }
}
