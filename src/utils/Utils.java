package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Utils {
	
	public static boolean compareTwoByteArray(byte[] a, byte[] b) {
		if(a.length != b.length) {
			return false;
		}
		
		for(int i = 0; i < a.length; i++) {
			if(a[i] != b[i]) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Đọc dữ liệu từ file trả về mảng các byte đọc được
	 * @param file : đầu vào
	 * @return : byte[]
	 * @throws IOException
	 */
	public static byte[] readBytesFromFile(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		byte[] data = new byte[(int) file.length()];
		int offset = 0;
		int bytesRead = fis.read(data);
		while((bytesRead > 0)) {
			offset += bytesRead;
			bytesRead = fis.read(data, offset, (int) (file.length() - offset));
		}
		fis.close();
//		System.out.println(new String(data, "UTF-8"));
		return data;
	}


	public static byte[] hash(byte[] in) {
		try {
			byte[] hash = new byte[20];
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(in);
			hash = md.digest();
			return hash;
		} catch (NoSuchAlgorithmException e) {
			System.err.println("SHA-1 algorithm is not available...");
			System.exit(2);
		}
		return null;
	}


	public static byte[] subArray(byte[] src, int offset, int length) {
		if (offset <= src.length && src.length >= offset + length) {
			byte[] des = new byte[length];
			for (int i = offset; i < offset + length; i++) {
				des[i - offset] = src[i];
			}
			return des;
		}
		return null;
	}

	/**
	 * Convert a byte array to a URL encoded string
	 * 
	 * @param in
	 *            byte[]
	 * @return String
	 */
	public static String byteArrayToURLString(byte in[]) {
		byte ch = 0x00;
		int i = 0;
		if (in == null || in.length <= 0)
			return null;

		String pseudo[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
		StringBuffer out = new StringBuffer(in.length * 2);

		while (i < in.length) {
			// First check to see if we need ASCII or HEX
			if ((in[i] >= '0' && in[i] <= '9') || (in[i] >= 'a' && in[i] <= 'z') || (in[i] >= 'A' && in[i] <= 'Z')
					|| in[i] == '$' || in[i] == '-' || in[i] == '_' || in[i] == '.' || in[i] == '!') {
				out.append((char) in[i]);
				i++;
			} else {
				out.append('%');
				ch = (byte) (in[i] & 0xF0); // Strip off high nibble
				ch = (byte) (ch >>> 4); // shift the bits down
				ch = (byte) (ch & 0x0F); // must do this is high order bit is
				// on!
				out.append(pseudo[(int) ch]); // convert the nibble to a
				// String Character
				ch = (byte) (in[i] & 0x0F); // Strip off low nibble
				out.append(pseudo[(int) ch]); // convert the nibble to a
				// String Character
				i++;
			}
		}
		String rslt = new String(out);
		return rslt;

	}

	/**
	 * Táº¡o 1 Ä‘á»‹nh danh ngáº«u nhiÃªn
	 * 
	 * @return Ä‘á»‹nh danh vá»«a táº¡o
	 */
	public static byte[] generateID() {
		byte[] id = new byte[12];

		Random r = new Random(System.currentTimeMillis());
		r.nextBytes(id);
		return ("-TT0001-" + new String(id)).getBytes();
	}
	
	public static byte[] shortToByteArray(short num) {
		return ByteBuffer.allocate(2).putShort(num).array();
	}
	
	public static short byteArrayToShort(byte[] array) {
		return ByteBuffer.wrap(array).getShort();
	}
	
	public static byte[] longToByteArray(long num) {
		return ByteBuffer.allocate(8).putLong(num).array();
	}
	
	public static Long byteArrayToLong(byte[] array) {
		return ByteBuffer.wrap(array).getLong();
	}

	/**
	 * Chuyá»ƒn sá»‘ nguyÃªn kiá»ƒu int sang máº£ng 4 byte
	 * 
	 * @param num
	 * @return
	 */
	public static byte[] intToByteArray(int num) {
		return ByteBuffer.allocate(4).putInt(num).array();
	}

	public static int byteArrayToInt(byte[] array) {
		return ByteBuffer.wrap(array).getInt();
	}

	/**
	 * Ná»‘i 2 máº£ng cÃ¡c byte
	 * 
	 * @param first
	 *            : máº£ng ghÃ©p vÃ o vá»‹ trÃ­ Ä‘áº§u cá»§a káº¿t quáº£
	 * @param after
	 *            : máº£ng ghÃ©p vÃ o pháº§n sau cá»§a káº¿t quáº£
	 * @return : máº£ng káº¿t quáº£ sau khi ghÃ©p
	 */
	public static byte[] concatArray(byte[] first, byte[] after) {
		byte[] dest = new byte[first.length + after.length];
		System.arraycopy(first, 0, dest, 0, first.length);
		System.arraycopy(after, 0, dest, first.length, after.length);
		return dest;
	}
	
	public static byte[] concatArray(byte[]...array) {
		byte[] dest = new byte[0];
		for(int i = 0; i < array.length; i++) {
			dest = Utils.concatArray(dest, array[i]);
		}
		return dest;
	}

	/**
	 * Chuyá»ƒn 1 byte vá»� dáº¡ng hex
	 * @param data : 1 byte
	 * @return chuá»—i dáº¡ng HEX
	 */
	public static String byteToHex(byte data) {
        StringBuffer buf = new StringBuffer();
        buf.append(toHexChar((data >>> 4) & 0x0F));
        buf.append(toHexChar(data & 0x0F));
        return buf.toString();
    }

    /**
     * Chuyá»ƒn 1 máº£ng byte vá»� chuá»—i HEX
     * @param máº£ng byte
     * @return chuá»—i HEX
     */
    public static String bytesToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            buf.append(byteToHex(data[i]) + " ");
        }
        return buf.toString();
    }
    
    /**
     * Láº¥y kÃ½ tá»± HEX cá»§a 1 giÃ¡ trá»‹
     * @param i giÃ¡ trá»‹ 4 bit
     * @return kÃ½ tá»± tÆ°Æ¡ng á»©ng
     */
    public static char toHexChar(int i) {
        if ((0 <= i) && (i <= 9))
            return (char) ('0' + i);
        else
            return (char) ('a' + (i - 10));
    }

}
