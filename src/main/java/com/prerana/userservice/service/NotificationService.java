package com.prerana.userservice.service;

import com.prerana.userservice.entity.UserEntity;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void notify(UserEntity user, String title, String message) {
        // DUMMY IMPLEMENTATION (for demo)
        System.out.println("📢 NOTIFICATION");
        System.out.println("To: " + user.getEmail());
        System.out.println("Title: " + title);
        System.out.println("Message: " + message);
        System.out.println("--------------");
    }
}