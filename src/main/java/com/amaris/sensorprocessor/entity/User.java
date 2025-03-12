package com.amaris.sensorprocessor.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("Users")
public class User {

    public User() {}

    public User(Integer idUser, String username, String firstname,
                String lastname, String password, String role, String email) {
        this.idUser = idUser;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.role = role;
        this.email = email;
    }

    @Id
    @Column("id_user")
    private Integer idUser;

    @Column("username")
    private String username;

    @Column("firstname")
    private String firstname;

    @Column("lastname")
    private String lastname;

    @Column("password")
    private String password;

    @Column("role")
    private String role;

    @Column("email")
    private String email;

}
