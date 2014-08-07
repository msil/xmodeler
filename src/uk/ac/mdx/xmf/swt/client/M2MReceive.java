package uk.ac.mdx.xmf.swt.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * ������Ϣ
 * 
 * 
 */
public class M2MReceive extends Thread {

	private Socket s;

	public M2MReceive(Socket s) {
		this.s = s;
	}

	public void run() {

		try {

			// ����������
			BufferedReader br = new BufferedReader(new InputStreamReader(
					s.getInputStream()));

			// ���ϵĽ�����Ϣ
			while (true) {

				String info = null;

				// ������Ϣ
				if ((info = br.readLine()) != null) {
					System.out.println(info);
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
