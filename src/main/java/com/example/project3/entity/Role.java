package com.example.project3.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "role")
@Data
public class Role {
    @Id
    @Column(name = "role_id")
    private Integer roleId;
    @Column(name = "name")
    private String name;
    @ManyToMany(mappedBy = "roles")
    @EqualsAndHashCode.Exclude
    private Collection<User> users;

}
