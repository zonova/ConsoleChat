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

				// Создаём объект Connection и добавляем его в список
				Connection con = new Connection(socket);
				connections.add(con);

				// Инициализирует нить и запускает метод run(),
				// которая выполняется одновременно с остальной программой
				con.start();

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeAll();
		}
	}

	/**
	 * Закрывает все потоки всех соединений а также серверный сокет
	 */
	private void closeAll() {
		try {
			server.close();
			
			// Перебор всех Connection и вызов метода close() для каждого. Блок
			// synchronized {} необходим для правильного доступа к одним данным
			// их разных нитей
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
		 * Инициализирует поля объекта и получает имя пользователя
		 * 
		 * @param socket
		 *            сокет, полученный из server.accept()
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
				// Отправляем всем клиентам сообщение о том, что зашёл новый пользователь
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
					
					// Отправляем всем клиентам очередное сообщение
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
	
				// Если больше не осталось соединений, закрываем всё, что есть и
				// завершаем работу сервера
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