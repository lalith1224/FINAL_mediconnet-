package com.mediconnect.repository;

import com.mediconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<User> findByRole(User.Role role);
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND (u.firstName LIKE %:search% OR u.lastName LIKE %:search% OR u.email LIKE %:search%)")
    List<User> findByRoleAndSearch(@Param("role") User.Role role, @Param("search") String search);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") User.Role role);
}
