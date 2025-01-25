package com.example.demo.service;

import com.example.demo.model.Archive;
import com.example.demo.model.Employee;
import com.example.demo.model.User;
import com.example.demo.repository.ArchiverRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private UserRepository userRepository;
    private ArchiverRepository archiverRepository;

    public UserService(UserRepository userRepository , ArchiverRepository archiverRepository) {
        this.userRepository = userRepository;
        this.archiverRepository = archiverRepository;
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }

    public List<User> getAllUsers(){
        List<User> users = userRepository.findAll();
        return users;
    }

    public User findById(long id){
        User user = userRepository.findById(id);
        return user;
    }

    public User getUserByFirstName(String firstName){
        return userRepository.findByFirstName(firstName);
    }

    public User updateUser(User user){
        User updateUser = userRepository.save(user);
        return updateUser;
    }


    public User addUserToArchive(User user){
        User user1 = userRepository.save(user);
        return user1;
    }


    public List<User> filterUserByDailyLogin(){
        Instant instant = Instant.now();
        Long senodsDay = 86400L;

        List<User> userList = userRepository.findAll();

        userList.stream().filter(filter_user -> {
          Instant registeredAt = filter_user.getRegistered_at().toInstant();
          long differenceSeconds = instant.getEpochSecond() - registeredAt.getEpochSecond();
          return differenceSeconds <= senodsDay;
        }).collect(Collectors.toList());

        return userList;
    }
    public List<User> filterUserByMonthLogin(){
        List<User> userList = userRepository.findAll();
        Instant instant = Instant.now();
        Long secondMonth = 30L * 24 * 60 * 60;


        userList.stream().filter(filter_user -> {
            Instant registeredAt = filter_user.getRegistered_at().toInstant();
            long differenceSeconds = instant.getEpochSecond() - registeredAt.getEpochSecond();
            return differenceSeconds <= secondMonth;
        }).collect(Collectors.toList());

        return userList;
    }





}
