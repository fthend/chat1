#!/usr/bin/env python3
import socket
import threading
import tkinter
import tkinter.scrolledtext
from tkinter import simpledialog
from tkinter import messagebox

HOST = '127.0.0.1'
PORT = 33001

class Client:

    def __init__(self, host, port):
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.connect((host, port))
        # self.log_file_name = log_file_name

        msg = tkinter.Tk()
        msg.withdraw()
        self.nickname = simpledialog.askstring("Форма авторизации", "Введите имя", parent=msg)

        self.gui_done = False
        self.running = True

        gui_thread = threading.Thread(target=self.gui_loop)
        recieve_thread = threading.Thread(target=self.recieve)

        gui_thread.start()
        recieve_thread.start()


    def gui_loop(self):
        self.win = tkinter.Tk()
        self.win.title("Чат")
        self.win.configure(bg="lightgrey")

        self.chat_label = tkinter.Label(self.win, text="Чат:", bg="lightgrey")
        self.chat_label.config(font=("Arial", 12))
        self.chat_label.pack(padx=20, pady=5)

        self.text_area = tkinter.scrolledtext.ScrolledText(self.win)
        self.text_area.pack(padx=20,pady=5)
        self.text_area.config(state='disabled')

        self.msg_label = tkinter.Label(self.win, text="Сообщение:", bg="lightgrey")
        self.msg_label.config(font=("Arial", 12))
        self.msg_label.pack(padx=20, pady=5)

        self.input_area = tkinter.Text(self.win, height=3)
        self.input_area.pack(padx=20,pady=5)

        self.send_button = tkinter.Button(self.win, text="Отправить", command=self.write)
        self.send_button.config(font=("Arial", 12))
        self.send_button.pack(padx=20,pady=5)

        self.gui_done = True
        self.send_done_message()
        self.win.protocol("WM_DELETE_WINDOW", self.stop)

        self.win.mainloop()

    def send_done_message(self):
        done_message = "<done>"
        self.sock.send(done_message.encode('utf-8'))

    def write(self):
        message = f"{self.nickname}: {self.input_area.get('1.0', 'end')}"
        self.sock.send(message.encode('utf-8'))
        self.input_area.delete('1.0', 'end')

    def stop(self):
        self.running - False
        self.win.destroy()
        self.sock.close()
        exit(0)

    def recieve(self):
        while self.running:
            try:
                message = self.sock.recv(1024)
                # print(message.decode('utf-8'))
                if message.decode('utf-8') == 'NICK':
                    self.sock.send(self.nickname.encode('utf-8'))
                elif message.decode('utf-8') == 'ERROR':
                    self.text_area.config(state='normal')
                    self.text_area.insert('end',
                                          str("Пользователь с таким именем уже есть в чате!\nЗайдите под другим именем!"))
                    self.text_area.yview('end')
                    self.text_area.config(state='disabled')
                    self.send_button.config(state='disabled')
                    self.sock.close()
                    # tkinter.messagebox.showerror("Ошибка", "Произошла ошибка. Приложение будет закрыто.")
                else:
                    if self.gui_done:
                        self.text_area.config(state='normal')
                        self.text_area.insert('end', str(message.decode('utf-8')))
                        self.text_area.yview('end')
                        self.text_area.config(state='disabled')
            except ConnectionAbortedError:
                break
            except:
                self.sock.close()
                break

client = Client(HOST, PORT)