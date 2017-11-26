package bencode;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BDecoder {
	public static Object decode(byte[] data) {
		return decode(new ByteArrayInputStream(data));
	}

	public static Object decode(InputStream is) {
		try {
			return new BDecoder(is).decode();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (BencodeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	// activities of decode
	private PushbackInputStream input;

	/*public BDecoder(byte[] data) {
		this(new ByteArrayInputStream(data));
	}*/

	public BDecoder(InputStream is) {
		this.input = new PushbackInputStream(is, 1);
	}

	private int readByte() throws IOException {
		int val = input.read();
		if (val == -1) {
			throw new EOFException();
		}
		return val;
	}

	private int peek() throws IOException {
		int val = readByte();
		input.unread(val);
		return val;
	}

	public Object decode() throws IOException, BencodeException {
		int val = peek();
		switch (val) {
		case 'd':
			return decodeDict();
		case 'l':
			return decodeList();
		case 'i':
			return decodeNumber();
		default:
			return decodeBytes();
		}
	}

	private Map<String, Object> decodeDict() throws IOException, BencodeException {
		readByte(); // skip d
		HashMap<String, Object> result = new HashMap<String, Object>();
		while (peek() != 'e') {
			String key = new String(decodeBytes());
			Object val = decode();
			if (val == null) {
//				throw new EOFException();
			}
			result.put(key, val);
		}
		readByte(); // read 'e' that we peeked
		return result;
	}

	private List<Object> decodeList() throws IOException, BencodeException {
		readByte();// skip l
		ArrayList<Object> result = new ArrayList<Object>();
		while (peek() != 'e') {
			Object value = decode();
			if (value == null) {
//				throw new EOFException();// TODO: tam thoi khong kiem tra loi
			}
			result.add(value);
		}
		readByte(); // remove 'e' that we peeked
		return result;
	}

	private long decodeNumber() throws IOException, BencodeException {
		int current = readByte(); // skip i
		long result = 0;
		boolean negative = false, readDigit = false;
		while (true) {
			current = readByte();
			if (current == '-' && !negative && !readDigit) {
				negative = true;
			} else if ('0' <= current && current <= '9') {
				readDigit = true;
				result = result * 10 + current - '0';
			} else if (current == 'e') {
				if (readDigit)
					return negative ? -result : result;
				else
					throw new BencodeException("Bencoded integer must contain at least one digit");
			} else
				throw new BencodeException("Unexpected character '%c' when reading bencoded long", current);
		}
	}

	private int readLength() throws BencodeException, IOException {
		boolean readDigit = false;
		int result = 0;
		while (true) {
			int current = readByte();
			if ('0' <= current && current <= '9') {
				readDigit = true;
				result *= 10;
				result += current - '0';
			} else if (current == ':') {
				if (readDigit) {
					return result;
				} else {
					throw new BencodeException("Bencode-length must contain at least one digit");
				}
			} else {
				throw new BencodeException("Unexpected character '%c' when reading bencode-length of string", current);
			}
		}
	}

	private byte[] decodeBytes() throws BencodeException, IOException {
		int length = readLength();

		byte[] result = new byte[length];
		if (length == 0) { // edge case where last value is an empty string
			return null;
		}
		int offset = input.read(result);
		if (offset == -1) {
			throw new EOFException();
		}
		while (offset != length) {
			int more = input.read(result, offset, length - offset);
			if (more == -1) {
				throw new EOFException();
			}
			offset += more;
		}
		return result;
	}


}
