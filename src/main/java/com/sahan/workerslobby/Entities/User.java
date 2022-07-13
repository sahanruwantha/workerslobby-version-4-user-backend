package com.sahan.workerslobby.Entities;


import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Slf4j
@ToString
public class User implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false, unique = true)
    @Getter
    private Long id;

    @Column(nullable = false, updatable = false, unique = true)
    @Getter
    private String userID;
    @Getter
    private String firstName;
    @Getter
    private String lastName;
    @Getter
    private String userName;
    @Getter
    private String password;
    @Getter
    private String email;
    @Getter
    private String profileImageUrl;
    @Getter
    private Date lastLoginDate;
    @Getter
    private Date lastLoginDateDisplay;
    @Getter
    private Date joinDate;
    @Getter
    private String role;
    @Getter
    private String[] authorities;
    @Getter
    private boolean isActive;
    @Getter
    private boolean isNotLocked;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinTable(
            name = "company_user",
            joinColumns = @JoinColumn( name = "userID"),
            inverseJoinColumns = @JoinColumn( name = "companyID")
    )
    @Getter
    private Company company;

}
