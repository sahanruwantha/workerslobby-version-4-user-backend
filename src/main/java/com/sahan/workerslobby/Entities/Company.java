package com.sahan.workerslobby.Entities;

import lombok.extern.slf4j.Slf4j;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Slf4j
@ToString
public class Company implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false, unique = true)
    @Getter
    private long companyID;
    @Getter
    private String name;

    @OneToMany(mappedBy = "id")
    private List<User> userList;
}
