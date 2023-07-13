package com.example.redisproject.redisproject.Controller;

import com.example.redisproject.redisproject.Entity.Userclass;
import com.example.redisproject.redisproject.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/users")
@CrossOrigin("*")
public class UserController {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/getall")
    public List<Userclass> getAllUsers() {
        List<Userclass> users = userService.getAllUsers();
        logger.info("fetching all  user");
        return users;
    }

    @PostMapping("/create")
    public ResponseEntity<Userclass> createUser(@RequestBody Userclass user) {
        logger.info("POST request received: Creating a new user");
        Userclass createdUser = userService.createUser(user);
        logger.info("POST request processed: User created successfully");
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Userclass> updateUserDetails(@PathVariable int id, @RequestBody Userclass user) {
        logger.info("Updating user details for ID {}: {}", id, user);
        Userclass updatedUser = userService.updateUserDetails(id, user);
        if (updatedUser != null) {
            logger.info("User details updated: {}", updatedUser);
            return ResponseEntity.ok(updatedUser);
        } else {
            logger.warn("User not found for ID {}. Update operation failed.", id);
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/evict")
    public ResponseEntity<String> evictCache() {
        logger.info("Eviction process triggered");
        userService.evictCacheData();
        return ResponseEntity.ok("Cache eviction process triggered.");
    }

}


