package com.amaris.sensorprocessor.service;

import com.amaris.sensorprocessor.entity.User;
import com.amaris.sensorprocessor.exception.ProblemeUsersException;
import com.amaris.sensorprocessor.repository.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> getAllUsers() {
        return userDao.findAllUsers();
    }

    public int save(User user) {
        if (!userDao.findByUsername(user.getUsername()).isEmpty()) {
            throw new ProblemeUsersException("User already exists");
        }
        return userDao.insertUser(user);
    }

    public int deleteUser(String username) {
        return userDao.deleteByIdOfUser(username);
    }

    public User searchUserByUsername(String username) {
        Optional<User> user = userDao.findByUsername(username);
        if (user.isEmpty()) {
            throw new ProblemeUsersException("User don't exists");
        }
        return user.get();
    }

    public int update(User user) {
        return userDao.updateUser(user);
    }

}
