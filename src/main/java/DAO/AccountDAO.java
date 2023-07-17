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

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

        return account;
    }

}
