package DAO;

import Model.Message;
import Util.ConnectionUtil;

import java.sql.*;

 /*
  *     ### Message=
  *  message_id integer primary key auto_increment,
  *  posted_by integer,
  *  message_text varchar(255),
  *  time_posted_epoch long,
  *  foreign key (posted_by) references Account(account_id)=
  */

public class MessageDAO {
    
    public Message postMessage(Message msg) {

        Connection connection = ConnectionUtil.getConnection();
        try {

            String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, msg.getPosted_by());
            preparedStatement.setString(2, msg.getMessage_text());
            preparedStatement.setLong(3, msg.getTime_posted_epoch());

            preparedStatement.executeUpdate();
            
            //Whole-ass second query to get newly added account. There has gotta be a better way to do this.
            //If i wanted to, i could replace this with a this.loginAccount(account), but that would be a poor solution wouldn't it.
            sql = "SELECT * FROM message WHERE posted_by = ? AND message_text = ? AND time_posted_epoch = ?"; 
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, msg.getPosted_by());
            preparedStatement.setString(2, msg.getMessage_text());
            preparedStatement.setLong(3, msg.getTime_posted_epoch());
            ResultSet rs = preparedStatement.executeQuery();

            if(rs.next()) { //return newly added user.
                Message newMessage = new Message(rs.getInt("message_id"), rs.getInt("posted_by"),
                                                    rs.getString("message_text"), rs.getLong("time_posted_epoch"));
                return newMessage;
            }

        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        
        //return null if the above try{} failed.
        return null;
    }

}
