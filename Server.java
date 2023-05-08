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
  // ����, ������� ����� ������������ ��� ������
  static final int PORT = 3443;
  // ������ ��������, ������� ����� ������������ � �������
  private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
 
  public Server() {
    // ����� �������, ��� ����� �����, ������� ����� ������������ � �������
    // �� ������ � �����
    Socket clientSocket = null;
    // ��������� �����
    ServerSocket serverSocket = null;
    try {
      // ������ ��������� ����� �� ������������ �����
      serverSocket = new ServerSocket(PORT);
      System.out.println("������ �������!");
      // ��������� ����������� ����
      while (true) {
        // ����� ������� ��� ����������� �� �������
        clientSocket = serverSocket.accept();
        // ������ ���������� �������, ������� ����������� � �������
        // this - ��� ��� ������
        ClientHandler client = new ClientHandler(clientSocket, this);
        clients.add(client);
        // ������ ����������� ������� ������������ � ����� ������
        new Thread(client).start();
      }
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    finally {
      try {
        // ��������� �����������
        clientSocket.close();
        System.out.println("������ ����������");
        serverSocket.close();
      }
      catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }
         
  // ���������� ��������� ���� ��������
  public void sendMessageToAllClients(String msg) {
    for (ClientHandler o : clients) {
      o.sendMsg(msg);
    }
 
  }
 
  // ������� ������� �� ��������� ��� ������ �� ����
  public void removeClient(ClientHandler client) {
    clients.remove(client);
  }
 
}