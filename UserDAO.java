package com.lankawings.dao;
import com.lankawings.config.DBConnection;
import com.lankawings.model.User;
import com.lankawings.util.PasswordUtil;
import java.sql.*;

public class UserDAO {
    public User authenticate(String username, String password) throws SQLException {
        String sql="SELECT UserID,FullName,Username,Email,Phone,Role FROM Users WHERE Username=? AND PasswordHash=? AND Status='ACTIVE'";
        try(Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql)){
            p.setString(1,username); p.setString(2,PasswordUtil.hash(password));
            try(ResultSet r=p.executeQuery()){ return r.next()?map(r):null; }
        }
    }
    public boolean register(String fullName,String username,String email,String phone,String password) throws SQLException {
        String sql="INSERT INTO Users(FullName,Username,Email,Phone,PasswordHash,Role) VALUES(?,?,?,?,?,'PASSENGER')";
        try(Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql)){
            p.setString(1,fullName); p.setString(2,username); p.setString(3,email); p.setString(4,phone); p.setString(5,PasswordUtil.hash(password));
            return p.executeUpdate()==1;
        }
    }
    public User findById(int id) throws SQLException {
        try(Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement("SELECT UserID,FullName,Username,Email,Phone,Role FROM Users WHERE UserID=?")){
            p.setInt(1,id); try(ResultSet r=p.executeQuery()){return r.next()?map(r):null;}
        }
    }
    public void updateProfile(int id,String fullName,String email,String phone) throws SQLException {
        try(Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement("UPDATE Users SET FullName=?,Email=?,Phone=? WHERE UserID=?")){
            p.setString(1,fullName);p.setString(2,email);p.setString(3,phone);p.setInt(4,id);p.executeUpdate();
        }
    }
    public void updatePassword(int id,String password) throws SQLException {
        try(Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement("UPDATE Users SET PasswordHash=? WHERE UserID=?")){
            p.setString(1,PasswordUtil.hash(password));p.setInt(2,id);p.executeUpdate();
        }
    }
    private User map(ResultSet r)throws SQLException{User u=new User();u.setUserId(r.getInt("UserID"));u.setFullName(r.getString("FullName"));u.setUsername(r.getString("Username"));u.setEmail(r.getString("Email"));u.setPhone(r.getString("Phone"));u.setRole(r.getString("Role"));return u;}
}
