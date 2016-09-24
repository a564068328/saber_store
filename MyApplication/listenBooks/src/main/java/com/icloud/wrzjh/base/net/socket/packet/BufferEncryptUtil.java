package com.icloud.wrzjh.base.net.socket.packet;

import java.util.ArrayList;

public class BufferEncryptUtil {
	public static int[] sendMap = new int[256];
	public static int[] recvMap = new int[256];

	public static void main(String[] args) {
		ArrayList<Integer> init = new ArrayList<Integer>();

		for (int i = 0; i <= 0xFF; i++) {
			init.add(i);
		}

		for (int i = 0; i <= 0xFF; i++) {
			int t1 = (int) (Math.random() * init.size());

			int d1 = init.get(t1);
			init.remove(t1);

			sendMap[i] = d1;

			recvMap[d1] = i;
		}

		for (int i = 0; i <= 0xff; i++) {
			String s = Integer.toHexString(sendMap[i]).toUpperCase();
			if (s.length() == 1) {
				s = "0" + s;
			}

			String is = Integer.toHexString(i).toUpperCase();
			if (is.length() == 1) {
				is = "0" + is;
			}

			System.out.println("sendMap[0x" + is + "] = 0x" + s + " ;");
		}

		System.out.println();

		for (int i = 0; i <= 0xff; i++) {
			String sr = Integer.toHexString(recvMap[i]).toUpperCase();
			if (sr.length() == 1) {
				sr = "0" + sr;
			}

			String is = Integer.toHexString(i).toUpperCase();
			if (is.length() == 1) {
				is = "0" + is;
			}

			System.out.println("recvMap[0x" + is + "] = 0x" + sr + " ;");
		}

	}
}
