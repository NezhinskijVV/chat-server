package ru.itsjava.services;

import lombok.SneakyThrows;
import ru.itsjava.dao.UserDao;
import ru.itsjava.dao.UserDaoImpl;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerImpl implements Server, Observable {
    private final static int PORT = 8080;
    private final List<Observer> observerList = new ArrayList<>();

    @SneakyThrows
    @Override
    public void start() {
        System.out.println("==SERVER STARTS==");
        ServerSocket serverSocket = new ServerSocket(PORT);
        final UserDao userDao = new UserDaoImpl();

        while (true) {
            Socket socket = serverSocket.accept();

            if (socket != null) {
                System.out.println("Client connected");
                new Thread(new ClientRunnable(socket, this, userDao)).start();
            }
        }
    }

    @Override
    public void addObserver(Observer observer) {
        observerList.add(observer);
    }

    @Override
    public void deleteObserver(Observer observer) {
        observerList.remove(observer);
    }

    @Override
    public void notifyObservers(String message) {
        for (Observer observer : observerList) {
            observer.notify(message);
        }
    }
}
