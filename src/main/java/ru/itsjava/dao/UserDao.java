package ru.itsjava.dao;

import ru.itsjava.domain.User;

public interface UserDao {
    User findByName(String name);
}
