package DAO;

import Model.Account;
import Util.ConnectionUtil;

import java.sql.*;

public class AccountDAO {
    
    /*  Registers a new account.
     *  ISSUE: Runs a whole second query to retrieve and return the newly added account. This is the simplest way to do this afaik.
     */
    public Account registerAccount(Account account) {

        Connection connection = ConnectionUtil.getConnection();

        try {

            String sql = "INSERT INTO account (username, password) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, account.getUsername());//editing sql with specifics of this call
            preparedStatement.setString(2, account.getPassword());

            preparedStatement.executeUpdate();
            
            //An entire second query to get the newly added account. There has gotta be a better way to do this.
            //If i wanted to, i could replace this with a this.loginAccount(account), but that would be a poor solution wouldn't it.
            sql = "SELECT * FROM account WHERE username = ? AND password = ?"; 
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getPassword());
            ResultSet rs = preparedStatement.executeQuery();

            if(rs.next()) { //return newly added user.
                Account newAccount = new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password"));
                return newAccount;
            }

        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        
        //return null if the above try{} failed.
        return null;
    }

    public Account loginAccount(Account account) {

        Connection connection = ConnectionUtil.getConnection();

        try {

            String sql = "SELECT * FROM account WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, account.getUsername());//editing sql with specifics of this call
            preparedStatement.setString(2, account.getPassword());

            ResultSet rs = preparedStatement.executeQuery();

            if(rs.next()) { //return newly added user.
                Account newAccount = new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password"));
                return newAccount;
            }

        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        //return null if the above try{} failed.
        return null;
    }
}
