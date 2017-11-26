package bencode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public class TestBencode {
	@SuppressWarnings("unchecked")
	public static Object testDecode(String filepath) throws FileNotFoundException {
		File file = new File(filepath);
		if(!file.exists()) {
			throw new FileNotFoundException();
		}
		
		FileInputStream fis = new FileInputStream(file);
		Object o = BDecoder.decode(fis);
		if(!(o instanceof Map)) {
			System.out.println("Failed to decode file torrent");
		}
		Map<String, Object> map = (Map<String, Object>) o;
		System.out.println(map);
		return map;
	}
	
	public static void testEncode(Object o) throws IOException {
		byte[] bytes = BEncoder.encode(o);
		
		System.out.println(new String(bytes, "UTF-8"));
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println("Decoded: ");
		Object o = testDecode("d.torrent");
		System.out.println("\n\nEncoded:");
		testEncode(o);
		
	}
}
