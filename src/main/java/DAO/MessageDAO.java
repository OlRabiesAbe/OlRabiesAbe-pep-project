package DAO;

import Model.Message;
import Util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;

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

            preparedStatement.setInt(1, msg.getPosted_by());//editing sql with specifics of this call
            preparedStatement.setString(2, msg.getMessage_text());
            preparedStatement.setLong(3, msg.getTime_posted_epoch());

            preparedStatement.executeUpdate();
            
            //second query to get the newly added message. There has gotta be a better way to do this.
            sql = "SELECT * FROM message WHERE posted_by = ? AND message_text = ? AND time_posted_epoch = ?"; 
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, msg.getPosted_by());
            preparedStatement.setString(2, msg.getMessage_text());
            preparedStatement.setLong(3, msg.getTime_posted_epoch());
            ResultSet rs = preparedStatement.executeQuery();

            if(rs.next()) { //return newly added message.
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

    public ArrayList<Message> getAllMessages() {

        Connection connection = ConnectionUtil.getConnection();

        try {

            String sql = "SELECT * FROM message";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ResultSet rs = preparedStatement.executeQuery();
            ArrayList<Message> messageList = new ArrayList<Message>();

            //moving rows from rs to messageList
            while(rs.next()) {

                int message_id = rs.getInt("message_id");
                int posted_by = rs.getInt("posted_by");
                String message_text = rs.getString("message_text");
                long time_posted_epoch = rs.getLong("time_posted_epoch");

                Message msg = new Message(message_id, posted_by, message_text, time_posted_epoch);
                messageList.add(msg);

            }

            return messageList;

        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        return new ArrayList<Message>();//return empty list if the try catch fails
    }

    public Message getMessageById(int message_id) {

        Connection connection = ConnectionUtil.getConnection();

        try {

            String sql = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, message_id);

            ResultSet rs = preparedStatement.executeQuery();

            if(rs.next()) { //return message.
                Message msg = new Message(rs.getInt("message_id"), rs.getInt("posted_by"),
                                            rs.getString("message_text"), rs.getLong("time_posted_epoch"));
                return msg;
            }

        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;//return null if try catch fails
    }

    public Message deleteMessageById(int message_id) {

        Connection connection = ConnectionUtil.getConnection();

        try {

            /*  We need to get the message before we delete it. this tells us if it's in the database, 
             *  and allows us to return it in the case of successful deletion
            */
            String sql = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, message_id);

            ResultSet rs = preparedStatement.executeQuery();

            Message msg;
            if(rs.next()) { //saving msg
                msg = new Message(rs.getInt("message_id"), rs.getInt("posted_by"),
                                    rs.getString("message_text"), rs.getLong("time_posted_epoch"));
            } else {
                msg = null;
            }

            // the deletion
            sql = "DELETE FROM message WHERE message_id = ?";
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, message_id);

            preparedStatement.executeUpdate(); 
            /*  We could use the int returned by execute update to check whether the delete went through,
             *  but I think it's good enough just returning msg from the SELECT call
            */

            return msg;

        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;//just return null if try catch fails
    }

    
    public Message patchMessageTextById(int message_id, String newMessage_text) {

        Connection connection = ConnectionUtil.getConnection();

        try {

            //  retrieving the message in a whole call to see if it's in the database.
            String sql = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, message_id);

            ResultSet rs = preparedStatement.executeQuery();
            /* if rs is empty, the message_id is invalid and the message isn't in the database.
                therefore, return null. */
            if(!rs.next()) { 
                return null;
            }
        
            //the actual updating is next. it's the easiest part.
            sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, newMessage_text);
            preparedStatement.setInt(2, message_id);

            preparedStatement.executeUpdate();

            //  retrieving the message in a whole third call so we can return it.
            sql = "SELECT * FROM message WHERE message_id = ?";
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, message_id);

            rs = preparedStatement.executeQuery();

            //finish up and return block.
            Message msg;
            if(rs.next()) { //saving the result into a new Message()
                msg = new Message(rs.getInt("message_id"), rs.getInt("posted_by"),
                                    rs.getString("message_text"), rs.getLong("time_posted_epoch"));
            } else {
                msg = null; //this check is neccessary, even though we know the message is in the database at this point?
            }

            // checking whether the retrieved text == the intended newMessage_text. dunno if we even need to do this, but can't hurt right?
            if(msg.getMessage_text().equals(newMessage_text)) {
                return msg;
            } else {
                return null;
            }

        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public ArrayList<Message> getAllMessagesByAccountId(int account_id) {

        Connection connection = ConnectionUtil.getConnection();

        try {

            String sql = "SELECT * FROM message WHERE posted_by = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, account_id);

            ResultSet rs = preparedStatement.executeQuery();
            ArrayList<Message> messageList = new ArrayList<Message>();

            //moving rows from rs to messageList
            while(rs.next()) {

                int message_id = rs.getInt("message_id");
                int posted_by = rs.getInt("posted_by");
                String message_text = rs.getString("message_text");
                long time_posted_epoch = rs.getLong("time_posted_epoch");

                Message msg = new Message(message_id, posted_by, message_text, time_posted_epoch);
                messageList.add(msg);

            }

            return messageList;

        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        return new ArrayList<Message>();//return empty list if the try catch fails
    }
    
}
