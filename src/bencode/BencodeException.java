package bencode;

public class BencodeException  extends Exception {

	private static final long serialVersionUID = 1L;

	public BencodeException(String msg, Object... args){
        super(String.format(msg, args));
    }
}

