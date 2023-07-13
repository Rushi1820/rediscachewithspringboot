package com.example.redisproject.redisproject.Service;

import com.example.redisproject.redisproject.Entity.Userclass;
import org.springframework.data.redis.core.RedisTemplate;
import com.example.redisproject.redisproject.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.HashOperations;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Service
public class UserService {
    private final UserRepository userRepository;
    private final RedisTemplate<String, Userclass> redisTemplate;
    private final Cache usersCache;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository, RedisTemplate<String, Userclass> redisTemplate, CacheManager cacheManager) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.usersCache = cacheManager.getCache("users");
    }

    @Cacheable(value = "users", key = "'all'")
    public List<Userclass> getAllUsers() {
        logger.info("Fetching all users");

        List<Userclass> allUsers = usersCache.get("all", List.class);
        if (allUsers != null) {
            logger.info("Retrieved all users from cache");
            return allUsers;
          }
            allUsers = userRepository.findAll();
            usersCache.put("all", allUsers);
            logger.info("Retrieved all users from the database and stored in cache");
            return allUsers;

    }

    @Transactional
    public Userclass createUser(Userclass user) {
        Userclass createdUser = userRepository.createUser(user);
        redisTemplate.opsForHash().put("users", String.valueOf(createdUser.getId()), createdUser);
        updateUsersCache(createdUser);
        return createdUser;
    }
    public Userclass updateUserDetails(int id, Userclass user) {
        logger.info("Updating user details for ID {}: {}", id, user);

        int rowsAffected = userRepository.updateUserDetails(id, user.getName(), user.getEmail());
        if (rowsAffected > 0) {
            Userclass updatedUser = new Userclass(id, user.getName(), user.getEmail());
            usersCache.put("user:" + id, updatedUser);
            updateUsersCache(updatedUser);

            logger.info("User details updated and stored in cache: {}", updatedUser);
            return updatedUser;
        } else {
            logger.warn("User not found for ID {}. Update operation failed.", id);
            return null;
        }
    }

    private void updateUsersCache(Userclass updatedUser) {
        List<Userclass> allUsers = usersCache.get("all", List.class);
        if (allUsers != null) {
            for (int i = 0; i < allUsers.size(); i++) {
                Userclass user = allUsers.get(i);
                if (user.getId() == updatedUser.getId()) {
                    allUsers.set(i, updatedUser);
                    break;
                }
            }
            usersCache.put("all", allUsers);
            logger.info("Updated 'all' users cache after user details update");
        }
    }

    @Scheduled(cron = "0 59 23 * * *") // Runs at 11:59 PM every day
    public void evictCacheData() {
        if (usersCache != null) {
            usersCache.clear();
            logger.info("Cache eviction process triggered.");
        } else {
            logger.warn("Cache is not initialized. Cache eviction process skipped.");
        }
    }
}


