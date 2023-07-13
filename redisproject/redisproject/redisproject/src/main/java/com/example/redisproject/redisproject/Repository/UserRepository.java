package com.example.redisproject.redisproject.Repository;

import com.example.redisproject.redisproject.Entity.Userclass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface UserRepository extends JpaRepository<Userclass, Integer> {

    Logger logger = LoggerFactory.getLogger(UserRepository.class);


    @Query(value = "SELECT * FROM userclass WHERE id = ?1", nativeQuery = true)
    Userclass findUserById(int id);

    @Query(value = "SELECT * FROM userclass WHERE id = ?1", nativeQuery = true)
    default Userclass getUserById(int id) {
        logger.info("Fetching user by ID: {}", id);
        return findUserById(id);
    }

    public default Userclass createUser(Userclass user) {
        logger.info("Saving user: {}", user);
        Userclass savedUser = save(user);
        logger.info("User saved successfully: {}", savedUser);
        return savedUser;
    }
    @Transactional
    @Modifying
    @Query("UPDATE Userclass u SET u.name = :name, u.email = :email WHERE u.id = :id")
    int updateUserDetails(@Param("id") int id, @Param("name") String name, @Param("email") String email);



}


