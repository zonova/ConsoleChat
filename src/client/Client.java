package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import main.Configs;

public class Client {
	private BufferedReader in;
	private PrintWriter out;
	private Socket socket;

	public Client() {
		Scanner scan = new Scanner(System.in);

		System.out.println("Enter your IP.");
		System.out.println("Format: xxx.xxx.xxx.xxx");

		String ip = scan.nextLine();

		try {
			// ������������ � ������� � �������� ������(in � out) ��� �������� ���������
			socket = new Socket(ip, Configs.PORT);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);

			System.out.println("Enter your username:");
			out.println(scan.nextLine());

			// ��������� ����� ���� �������� ��������� � �������
			Resender resend = new Resender();
			resend.start();

			// ���� ������������ �� ����� "exit" ���������� �� ������ ��, ���
			// ������� �� �������
			String str = "";
			while (!str.equals("exit")) {
				str = scan.nextLine();
				out.println(str);
			}
			resend.setStop();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	private void close() {
		try {
			in.close();
			out.close();
			socket.close();
		} catch (Exception e) {
			System.err.println("Threads close error!");
		}
	}

	private class Resender extends Thread {

		private boolean stoped;
		
		/**
		 * ���������� ��������� ���������
		 */
		public void setStop() {
			stoped = true;
		}

		@Override
		public void run() {
			try {
				while (!stoped) {
					String str = in.readLine();
					System.out.println(str);
				}
			} catch (IOException e) {
				System.err.println("Message error.");
				e.printStackTrace();
			}
		}
	}

}
