package client;
 
import javax.accessibility.AccessibleContext;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;
 
public class ClientWindow extends JFrame {
  // адрес сервера
  private static final String SERVER_HOST = "localhost";
  // порт
  private static final int SERVER_PORT = 3443;
  // клиентский сокет
  private Socket clientSocket;
  // вход€щее сообщение
  private Scanner inMessage;
  // исход€щее сообщение
  private PrintWriter outMessage;
  // следующие пол€ отвечают за элементы формы
  private JTextField jtfMessage;
  private JTextField jtfName;
  private JTextArea jtaTextAreaMessage;
  // им€ клиента
  private String clientName = "";
  // получаем им€ клиента
  public String getClientName() {
    return this.clientName;
  }
 
  // конструктор
  public ClientWindow() {
    try {
      // подключаемс€ к серверу
      clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
      inMessage = new Scanner(clientSocket.getInputStream());
      outMessage = new PrintWriter(clientSocket.getOutputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }
    // «адаЄм настройки элементов на форме
    setBounds(600, 300, 600, 500);
    setTitle("Client");
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    jtaTextAreaMessage = new JTextArea();
    jtaTextAreaMessage.setEditable(false);
    jtaTextAreaMessage.setLineWrap(true);
    JScrollPane jsp = new JScrollPane(jtaTextAreaMessage);
    add(jsp, BorderLayout.CENTER);
    // label, который будет отражать количество клиентов в чате
    JLabel jlNumberOfClients = new JLabel(" оличество клиентов в чате: ");
    add(jlNumberOfClients, BorderLayout.NORTH);
    JPanel bottomPanel = new JPanel(new BorderLayout());
    add(bottomPanel, BorderLayout.SOUTH);
    JButton jbSendMessage = new JButton("ќтправить");
    bottomPanel.add(jbSendMessage, BorderLayout.EAST);
    jtfMessage = new JTextField("¬ведите ваше сообщение: ");
    bottomPanel.add(jtfMessage, BorderLayout.CENTER);
    jtfName = new JTextField("¬ведите ваше им€: ");
    bottomPanel.add(jtfName, BorderLayout.WEST);
    // обработчик событи€ нажати€ кнопки отправки сообщени€
    jbSendMessage.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // если им€ клиента, и сообщение непустые, то отправл€ем сообщение
        if (!jtfMessage.getText().trim().isEmpty() && !jtfName.getText().trim().isEmpty()) {
          clientName = jtfName.getText();
          sendMsg();
          // фокус на текстовое поле с сообщением
          jtfMessage.grabFocus();
        }
      }
    });
    // при фокусе поле сообщени€ очищаетс€
    jtfMessage.addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        jtfMessage.setText("");
      }
    });
    // при фокусе поле им€ очищаетс€
    jtfName.addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        jtfName.setText("");
      }
    });
    // в отдельном потоке начинаем работу с сервером
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          // бесконечный цикл
          while (true) {
            // если есть вход€щее сообщение
            if (inMessage.hasNext()) {
              // считываем его
              String inMes = inMessage.nextLine();
              String clientsInChat = " лиентов в чате = ";
              if (inMes.indexOf(clientsInChat) == 0) {
                jlNumberOfClients.setText(inMes);
              } else {
                // выводим сообщение
                jtaTextAreaMessage.append(inMes);
                // добавл€ем строку перехода
                jtaTextAreaMessage.append("\n");
              }
            }
          }
        } catch (Exception e) {
          }
      }
    }).start();
    // добавл€ем обработчик событи€ закрыти€ окна клиентского приложени€
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        try {
          // здесь провер€ем, что им€ клиента непустое и не равно значению по умолчанию
          if (!clientName.isEmpty() && clientName != "¬ведите ваше им€: ") {
            outMessage.println(clientName + " вышел из чата!");
          } else {
            outMessage.println("”частник вышел из чата, так и не представившись!");
          }
          // отправл€ем служебное сообщение, которое €вл€етс€ признаком того, что клиент вышел из чата
          outMessage.println("##session##end##");
          outMessage.flush();
          outMessage.close();
          inMessage.close();
          clientSocket.close();
        } catch (IOException exc) {
 
        }
      }
    });
    // отображаем форму
    setVisible(true);
  }
 
  // отправка сообщени€
  public void sendMsg() {
    // формируем сообщение дл€ отправки на сервер
    String messageStr = jtfName.getText() + ": " + jtfMessage.getText();
    // отправл€ем сообщение
    outMessage.println(messageStr);
    outMessage.flush();
    jtfMessage.setText("");
  }
}