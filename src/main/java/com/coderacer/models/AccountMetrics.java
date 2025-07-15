package com.coderacer.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Subselect;
import org.springframework.data.annotation.Immutable;

import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Immutable
//@Subselect() finish later
public class AccountMetrics {
    @Id
    @Column(unique = true, nullable = false)
    private UUID accountId;

//    @OneToOne
//    private Account account;

    private double avgCpm;
    private double avgAccuracy;
    private double avgCpmWeekly;
    private double avgAccuracyWeekly;
}
