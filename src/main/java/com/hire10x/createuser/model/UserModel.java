package com.hire10x.createuser.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "USERS_TABLE")
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", initialValue = 1000, allocationSize = 1)
    private Long id;


    @Column(nullable = false, unique = true)
    private String userId; // Custom String ID

    @PrePersist
    public void generateUserId() {
        this.userId = this.firstName + String.valueOf(this.id);
    }

    @NotNull(message = "first_name cannot be null")
    @Column(nullable = false)
    private String firstName;

    @NotNull(message = "last_name cannot be null")
    @Column(nullable = false)
    private String lastName;

    @NotNull(message = "password cannot be null")
    @Column(nullable = false)
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no spaces"
    )
    private String password;

    @NotNull(message = "role cannot be null")
    @Column(nullable = false)
    private String role;

    @NotNull(message = "customer_id cannot be null")
    @Column(nullable = false)
    private String customerId;

    private String designation;

    @Column(unique = true)
    @Size(min = 10, max = 10, message = "Phone must be exactly 10 characters long")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be a valid 10-digit number")
    private String phone;

    @Email(message = "email should be valid")
    @Column(unique = true)
    private String email;

    private String middleName;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @Column(nullable = false)
    private String status = "ENABLED";


}
