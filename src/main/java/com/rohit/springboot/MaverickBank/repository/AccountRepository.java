package com.rohit.springboot.MaverickBank.repository;

import com.rohit.springboot.MaverickBank.entities.Account;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface AccountRepository extends JpaRepository<Account,Long> {

    List<Account> findAllByIfsc(String ifsc);

    @Query(value = "SELECT * From accounts WHERE ifsc=?1 AND approved=?2 LIMIT ?3;",nativeQuery = true)
    List<Account> findAllByIfscAndApproved(String ifsc,boolean approved,Integer count);

    //@Query(value = "SELECT * From accounts WHERE approved=?1 OFFSET ?3 ROWS FETCH NEXT ?2 ROWS ONLY;",nativeQuery = true)
    //
    @Query(value = "SELECT * From accounts WHERE approved=?1 LIMIT ?2",nativeQuery = true)
    List<Account> findAllByApproved(boolean b, Integer Count);
}
