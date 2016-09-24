package com.icloud.wrzjh.base.net.socket.packet;

public class RC4Http {

	public static byte[] key;
	private static String shareKey = "0391591aafc5db68b08787645b837b4f";

	public static void RC4Base(byte[] input, int start, int end) {
		int x = 0;
		int y = 0;
		byte key[] = initKey(shareKey);
		int xorIndex;
		for (int i = start; i < end; i++) {
			x = (x + 1) & 0xff;
			y = ((key[x] & 0xff) + y) & 0xff;
			byte tmp = key[x];
			key[x] = key[y];
			key[y] = tmp;
			xorIndex = ((key[x] & 0xff) + (key[y] & 0xff)) & 0xff;
			input[i] = (byte) (input[i] ^ key[xorIndex]);
		}
	}

	private static byte[] initKey(String aKey) {
		if (key == null) {
			byte[] b_key = aKey.getBytes();
			key = new byte[256];

			for (int i = 0; i < 256; i++) {
				key[i] = (byte) i;
			}

			int index1 = 0;
			int index2 = 0;
			if (b_key == null || b_key.length == 0) {
				return null;
			}
			for (int i = 0; i < 256; i++) {
				index2 = ((b_key[index1] & 0xff) + (key[i] & 0xff) + index2) & 0xff;
				byte tmp = key[i];
				key[i] = key[index2];
				key[index2] = tmp;
				index1 = (index1 + 1) % b_key.length;
			}
		}

		return copyOf(key, key.length);
	}

	public static byte[] copyOf(byte[] original, int newLength) {
		byte[] copy = new byte[newLength];
		System.arraycopy(original, 0, copy, 0,
				Math.min(original.length, newLength));
		return copy;
	}
}