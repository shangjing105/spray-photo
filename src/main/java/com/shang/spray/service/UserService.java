package com.shang.spray.service;

import com.shang.spray.entity.User;
import org.springframework.stereotype.Service;

/**
 * info:
 * Created by shang on 16/7/26.
 */
@Service
public class UserService extends BaseService<User> {

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
