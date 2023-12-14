package com.rohit.springboot.MaverickBank.repository;

import com.rohit.springboot.MaverickBank.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    User findByEmail(String email);
    User findByPan(String pan);
    User findByPhonenumber(String phonenumber);
    User findByEmailOrPhonenumberOrPan(String email,String phonenumber,String pan);

    Optional<User> findById(Long id);

    User findByEmailOrPhonenumberOrPanOrId(String email,String phonenumber,String pan,long l);

    List<User> findAllByRole(String role);

    @Query(value = "Select * from maverickbank.users where role=?1 Limit ?2",nativeQuery = true)
    List<User> findAllByRoleNCnt(String role,Integer count);
}
