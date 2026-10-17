package com.example.Loan_Project.Repository;

import com.example.Loan_Project.Entity.ScoreCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScoreCardRepository extends JpaRepository<ScoreCard, Long> {

    List<ScoreCard> findByAppliedDateGreaterThanEqualAndAppliedDateLessThan(
            LocalDateTime fromDate,
            LocalDateTime toDateExclusive
    );
}
