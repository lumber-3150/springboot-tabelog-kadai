package com.example.nagoyameshi.entity;

import java.sql.Timestamp;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "kana")
    private String kana;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "email_verified_at")
    private String email_verified_at;
    
    @Column(name = "password")
    private String password; 
    
    @Column(name = "postal_code")
    private String postalCode;
        
    @Column(name = "address")
    private String address;
        
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "birthday")
    private LocalDate birthday;  
    
    @Column(name = "remember_token")
    private String remember_token;  
    
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;   
    
    @Column(name = "enabled")
    private Boolean enabled;
    
    @Column(name = "customer_id")
    private String customerId;
    
    @Column(name ="subscription_id")
    private String subscriptionId;
    
    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;
    
    @Column(name = "updated_at", insertable = false, updatable = false)
    private Timestamp updatedAt; 
}
