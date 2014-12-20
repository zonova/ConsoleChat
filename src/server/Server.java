package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import main.Configs;

public class Server {

	
	private List<Connection> connections = 
			Collections.synchronizedList(new ArrayList<Connection>());
	private ServerSocket server;

	public Server() {
		try {
			server = new ServerSocket(Configs.PORT);

			while (true) {
				Socket socket = server.accept();

				// ������ ������ Connection � ��������� ��� � ������
				Connection con = new Connection(socket);
				connections.add(con);

				// �������������� ���� � ��������� ����� run(),
				// ������� ����������� ������������ � ��������� ����������
				con.start();

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeAll();
		}
	}

	/**
	 * ��������� ��� ������ ���� ���������� � ����� ��������� �����
	 */
	private void closeAll() {
		try {
			server.close();
			
			// ������� ���� Connection � ����� ������ close() ��� �������. ����
			// synchronized {} ��������� ��� ����������� ������� � ����� ������
			// �� ������ �����
			synchronized(connections) {
				Iterator<Connection> iter = connections.iterator();
				while(iter.hasNext()) {
					((Connection) iter.next()).close();
				}
			}
		} catch (Exception e) {
			System.err.println("Threads close error!");
		}
	}

	private class Connection extends Thread {
		private BufferedReader in;
		private PrintWriter out;
		private Socket socket;
	
		private String name = "";
	
		/**
		 * �������������� ���� ������� � �������� ��� ������������
		 * 
		 * @param socket
		 *            �����, ���������� �� server.accept()
		 */
		public Connection(Socket socket) {
			this.socket = socket;
	
			try {
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
	
			} catch (IOException e) {
				e.printStackTrace();
				close();
			}
		}
	
		@Override
		public void run() {
			try {
				name = in.readLine();
				// ���������� ���� �������� ��������� � ���, ��� ����� ����� ������������
				synchronized(connections) {
					Iterator<Connection> iter = connections.iterator();
					while(iter.hasNext()) {
						((Connection) iter.next()).out.println(name + " cames now");
					}
				}
				
				String str = "";
				while (true) {
					str = in.readLine();
					if(str.equals("exit")) break;
					
					// ���������� ���� �������� ��������� ���������
					synchronized(connections) {
						Iterator<Connection> iter = connections.iterator();
						while(iter.hasNext()) {
							((Connection) iter.next()).out.println(name + ": " + str);
						}
					}
				}
				
				synchronized(connections) {
					Iterator<Connection> iter = connections.iterator();
					while(iter.hasNext()) {
						((Connection) iter.next()).out.println(name + " has left");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				close();
			}
		}
	
		public void close() {
			try {
				in.close();
				out.close();
				socket.close();
	
				// ���� ������ �� �������� ����������, ��������� ��, ��� ���� �
				// ��������� ������ �������
				connections.remove(this);
				if (connections.size() == 0) {
					Server.this.closeAll();
					System.exit(0);
				}
			} catch (Exception e) {
				System.err.println("Threads close error!");
			}
		}
	}
}