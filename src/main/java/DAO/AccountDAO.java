package DAO;

import Model.Account;
import Util.ConnectionUtil;

import java.sql.*;

public class AccountDAO {
    
    /*
     * @TODO AccountDAO.registerAccount doesn't return the new account. I think it just needs to do a second query to get the added account.
     */
    public Account registerAccount(Account account) {

        Connection connection = ConnectionUtil.getConnection();

        try {

            String sql = "INSERT INTO account (username, password) VALUES (?, ?)" ;
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getPassword());

            preparedStatement.executeUpdate();
            
            //Whole-ass second query to get newly added account. There has gotta be a better way to do this.
            sql = "SELECT * FROM account WHERE username = ? AND password = ?"; 
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getPassword());
            ResultSet rs = preparedStatement.executeQuery();

            if(rs.next()) {
                Account newAccount = new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password"));
                System.out.println(newAccount.toString());
                return newAccount;
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

        return null;
    }

}
