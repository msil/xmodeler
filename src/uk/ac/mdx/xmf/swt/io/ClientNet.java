package uk.ac.mdx.xmf.swt.io;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientNet {
	public static InetAddress localHost() {
		try {
			return InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			return null;
		}
	}

	public static InetAddress address(String address) {
		try {
			return InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			System.out.println("Unknown host: " + address);
			return null;
		}
	}

	public void sendMessage(String message) {
		try {
			Socket s = new Socket(localHost(), 9999);
			// ���ڻ�ȡ����˴���������Ϣ
			BufferedReader buff = new BufferedReader(new InputStreamReader(
					s.getInputStream()));
			// ���ڻ�ȡ�ͻ���׼����Ӧ����Ϣ
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(new ByteArrayInputStream(
							message.getBytes())));
			// ���Ϳͻ���׼���������Ϣ
			PrintWriter printWriter = new PrintWriter(s.getOutputStream(), true);
			printWriter.println("�пͷ�����������!");

			// �ͻ���ʹ���̳߳����ӷ����
			// ExecutorService executorService =
			// Executors.newCachedThreadPool();
			// executorService.execute(new
			// ClientOutNet(printWriter,bufferedReader));
			new ClientOutNet(printWriter, bufferedReader);

			while (true) { // ��ʾ����˵���Ӧ��Ϣ
				String str = buff.readLine();
				if (str != null) {
					System.out.println(str);
				}
				// System.out.println("To Server:");
				Thread.sleep(100);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		try {
			Socket s = new Socket(localHost(), 9999);
			// ���ڻ�ȡ����˴���������Ϣ
			BufferedReader buff = new BufferedReader(new InputStreamReader(
					s.getInputStream()));
			// ���ڻ�ȡ�ͻ���׼����Ӧ����Ϣ
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(System.in));
			// ���Ϳͻ���׼���������Ϣ
			PrintWriter printWriter = new PrintWriter(s.getOutputStream(), true);
			printWriter.println("�пͷ�����������!");

			// �ͻ���ʹ���̳߳����ӷ����
			// ExecutorService executorService =
			// Executors.newCachedThreadPool();
			// executorService.execute(new
			// ClientOutNet(printWriter,bufferedReader));
			new ClientOutNet(printWriter, bufferedReader);

			while (true) { // ��ʾ����˵���Ӧ��Ϣ
				String str = buff.readLine();
				if (str != null) {
					System.out.println(str);
				}
				// System.out.println("To Server:");
				Thread.sleep(100);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

/**
 * Client �����߳� �������˷�����Ϣ
 * 
 * @author ids-user
 */
class ClientOutNet extends Thread {
	private PrintWriter printWriter;
	private BufferedReader bufferedReader;

	ClientOutNet(PrintWriter pw, BufferedReader in) {
		this.printWriter = pw;
		this.bufferedReader = in;
		start();
	}

	public void run() {
		while (true) {
			try {
				String inStr = "";
				inStr = bufferedReader.readLine();
				if (inStr != null && inStr.equals("exit")) {
					printWriter.close();
					bufferedReader.close();
					break;
				} else {
					printWriter.println("From Client: " + inStr);
					printWriter.flush();
				}
				sleep(300);
			} catch (Exception e) {
				printWriter = null;
				bufferedReader = null;
				throw new RuntimeException(e);
			}
		}
	}
}
