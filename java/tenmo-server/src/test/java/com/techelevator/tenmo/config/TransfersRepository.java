package com.techelevator.tenmo.config;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("test")
public interface TransfersRepository extends JpaRepository<Transfers, Long> {
    @Query("SELECT t FROM Transfers t where t.accountfrom = :accountid " +
            "OR t.accountto = :accountid")
    List<Transfers> findTransfersForAccount(@Param("accountid") Long accountid);
}
