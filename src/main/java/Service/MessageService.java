package Service;

import DAO.MessageDAO;
import Model.Message;

import java.util.ArrayList;

public class MessageService {
    
    private MessageDAO messageDAO;

    public MessageService() {
        messageDAO = new MessageDAO();
    }

    public Message postMessage(Message msg) {

        if(msg.getMessage_text().length() == 0
                || msg.getMessage_text().length() >= 255) 
                return null;

        return messageDAO.postMessage(msg);
    }

    public ArrayList<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }

    public Message getMessageById(int message_id) {
        return messageDAO.getMessageById(message_id);
    }

    public Message deleteMessageById(int message_id) {
        return messageDAO.deleteMessageById(message_id);
    }
}
