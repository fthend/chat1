/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
 
public class Server {
  // порт, который будет прослушивать наш сервер
  static final int PORT = 3443;
  // список клиентов, которые будут подключатьс€ к серверу
  private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
 
  public Server() {
    // сокет клиента, это некий поток, который будет подключатьс€ к серверу
    // по адресу и порту
    Socket clientSocket = null;
    // серверный сокет
    ServerSocket serverSocket = null;
    try {
      // создаЄм серверный сокет на определенном порту
      serverSocket = new ServerSocket(PORT);
      System.out.println("—ервер запущен!");
      // запускаем бесконечный цикл
      while (true) {
        // таким образом ждЄм подключений от сервера
        clientSocket = serverSocket.accept();
        // создаЄм обработчик клиента, который подключилс€ к серверу
        // this - это наш сервер
        ClientHandler client = new ClientHandler(clientSocket, this);
        clients.add(client);
        // каждое подключение клиента обрабатываем в новом потоке
        new Thread(client).start();
      }
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    finally {
      try {
        // закрываем подключение
        clientSocket.close();
        System.out.println("—ервер остановлен");
        serverSocket.close();
      }
      catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }
         
  // отправл€ем сообщение всем клиентам
  public void sendMessageToAllClients(String msg) {
    for (ClientHandler o : clients) {
      o.sendMsg(msg);
    }
 
  }
 
  // удал€ем клиента из коллекции при выходе из чата
  public void removeClient(ClientHandler client) {
    clients.remove(client);
  }
 
}