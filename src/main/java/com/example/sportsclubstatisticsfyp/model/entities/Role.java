package com.example.sportsclubstatisticsfyp.model.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;



    @Entity
    @Table(name = "roles")
    @Data
    public class Role implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long role_id;



        private String role;

        //The inverse side of the many-to-many relationship with the Customer entity
        //Each Role can be associated with multiple Customers, and each Customer can have multiple Roles.
        //Using a SEt is imortat here so that each customer is unique within the collection, preventing duplicates.
        @ManyToMany(mappedBy = "roles")
        private Set<User> users;

        public Long getRoleID() {
            return role_id;
        }

        public void setRoleID(Long roleID) {
            this.role_id = roleID;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public Set<User> getUsers() {
            return users;
        }

        public void setUsers(Set<User> users) {
            this.users = users;
        }
    }


