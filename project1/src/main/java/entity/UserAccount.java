package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_accounts")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Role: 1 = Manager, 2 = Staff, 3 = Guest
     */
    @Column(name = "role", nullable = false)
    private int role;

    public UserAccount() {
    }

    public UserAccount(String username, String password, int role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getRoleName() {
        switch (role) {
            case 1:
                return "Manager";
            case 2:
                return "Staff";
            default:
                return "Guest";
        }
    }
}
