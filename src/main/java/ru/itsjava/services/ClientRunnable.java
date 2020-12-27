package ru.itsjava.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import ru.itsjava.dao.UserDao;
import ru.itsjava.domain.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@RequiredArgsConstructor
public class ClientRunnable implements Runnable, Observer {
    private final Socket socket;
    private final Observable server;
    private final UserDao userDao;
    private User user;

    @SneakyThrows
    @Override
    public void run() {
        BufferedReader socketReader =
                new BufferedReader(new InputStreamReader(socket.getInputStream()));

        if (authorization(socketReader)) {
            server.addObserver(this);
            String socketInputMessage;
            while ((socketInputMessage = socketReader.readLine()) != null) {
                System.out.println(socketInputMessage);
                server.notifyObservers(user.getName() +":" + socketInputMessage);
            }
        }
    }

    @SneakyThrows
    private boolean authorization(BufferedReader socketReader) {
        String socketInputMessage = socketReader.readLine();
        // !auth!login:password
        var loginAndPass = socketInputMessage
                .substring(6).split(":");

        user = userDao.findByName(loginAndPass[0]);
        if (user.getPassword().equals(loginAndPass[1])) {
            notify("Вы успешно авторизовались");
            return true;
        }
        return false;
    }

    @SneakyThrows
    @Override
    public void notify(String message) {
        PrintWriter socketWriter = new PrintWriter(socket.getOutputStream());
        socketWriter.println(message);
        socketWriter.flush();
    }
}