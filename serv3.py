#!/usr/bin/env python3
import socket
import threading
import datetime
import time

HOST = '127.0.0.1'
PORT = 33001
ADDR = (HOST, PORT)

# LOG_FILE_NAME = datetime.datetime.now().strftime("%Y-%m-%d") + ".log"
LOG_FILE_NAME = "chat_history.log"
with open(LOG_FILE_NAME, "a"):
    pass

server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server.bind(ADDR)
server.listen()

clients = []
nicknames= []


def broadcast(message):
    for client in clients:
        client.send(message)

def handle(client):
    while True:
        try:
            # message = client.recv(1024)
            # print(message)
            # print("hello")
            # if message.decode('utf-8') == "<done>":
            #     print("jopa")
            #     with open(LOG_FILE_NAME, "r") as log_file:
            #         history = log_file.read()
            #         print(history)
            #     client.send(history.encode("utf-8"))
            # else:
            #     print(f"{message.decode('utf-8')}")
            #     msg = message.decode('utf-8')
            #     broadcast(msg.encode('utf-8'))
            #     print("send")
            #     with open(LOG_FILE_NAME, "a") as log_file:
            #         log_file.write(msg+"\n")
            message = client.recv(1024).decode('utf-8')
            # print(message)
            # print("hello")
            if message == "<done>":
                index = clients.index(client)
                print("ник:" + nicknames[index].decode('utf-8'))
                with open(LOG_FILE_NAME, "r") as log_file:
                    history = log_file.read()
                # print(history)
                client.send(history.encode("utf-8"))

            else:
                # print(f"{message}")
                broadcast(message.encode('utf-8'))
                # print("send")
                with open(LOG_FILE_NAME, "a") as log_file:
                    log_file.write(message)
        except:
            index = clients.index(client)
            clients.remove(client)
            client.close()
            nickname = nicknames[index]
            nicknames.remove(nickname)
            broadcast(f"{nickname.decode('utf-8')} покинул чат!\n".encode('utf-8'))
            with open(LOG_FILE_NAME, "a") as log_file:
                log_file.write(f"{nickname.decode('utf-8')} покинул чат!" + "\n")
            break

def recieve():
    while True:
        client, address = server.accept()
        print(f"Соединен с {str(address)}!")

        client.send("NICK".encode('utf-8'))
        nickname = client.recv(1024)
        # if nickname in nicknames:
        #     broadcast("ERROR".encode('utf-8'))
            # client.send("ERROR".encode('utf-8'))
            # client.close()
            # continue
        time.sleep(1)
        if nickname in nicknames:
            # broadcast("ERROR".encode('utf-8'))
            client.send("ERROR".encode('utf-8'))
            client.close()
            continue

        nicknames.append(nickname)
        clients.append(client)
        name = nickname.decode('utf-8')
        print(f"{nickname.decode('utf-8')} присоединился к чату")
        broadcast(f"{name} присоединился к чату!\n".encode('utf-8'))
        with open(LOG_FILE_NAME, "a") as log_file:
            log_file.write(f"{nickname.decode('utf-8')} присоединился к чату!" + "\n")
        client.send("Соединен с сервером".encode('utf-8'))

        thread = threading.Thread(target=handle, args=(client,))
        thread.start()


print("Сервер запущен...")
recieve()