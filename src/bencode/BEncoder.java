package bencode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BEncoder {
	public static byte[] encode(Object object) {
		return new BEncoder().bencode(object);
	}
	
	// activities of encode
	private ByteArrayOutputStream output;

	public BEncoder() {
		output = new ByteArrayOutputStream();
	}

	public byte[] bencode(Object object) {
		try {
			write(object);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BencodeException e) {
			e.printStackTrace();
		}
		return output.toByteArray();
	}

	@SuppressWarnings("unchecked")
	private void write(Object object) throws IOException, BencodeException {

		if (object instanceof Map) {
			writeDict((Map<String, Object>) object);
		} else if (object instanceof List) {
			writeList((List<Object>) object);
		} else if (object instanceof Long) {
			writeNumber((Long) object);
		} else if (object instanceof Integer) {
			writeNumber(((Integer) object).longValue());
		} else if (object instanceof byte[]) {
			writeBytes((byte[]) object);
		} else if (object instanceof String) {
			writeBytes(((String) object).getBytes());
		} else {
			throw new BencodeException("Value must either be integer, string, list or map, was %s : %s",
					object.getClass().getName(), object);
		}

	}

	private void writeDict(Map<String, Object> map) throws IOException, BencodeException {
		output.write('d');
		
		Set<String> key = map.keySet();
		List<String> lKey = new ArrayList<String>(key);
		Collections.sort(lKey);
		for(String k : lKey) {
			Object value = map.get(k);
			write(k);
			write(value);
		}
		output.write('e');
	}

	private void writeList(List<Object> list) throws IOException, BencodeException {
		output.write('l');
		for (Object o : list) {
			write(o);
		}
		output.write('e');

	}

	private void writeNumber(long number) throws IOException {
		byte[] bytes = new Long(number).toString().getBytes("UTF-8");
		output.write('i');
		output.write(bytes);
		output.write('e');
	}

	private void writeBytes(byte[] bytes) throws IOException {
		byte[] bytesLength = new Integer(bytes.length).toString().getBytes("UTF-8");
		output.write(bytesLength);
		output.write(':');
		output.write(bytes);
	}
}
