package Service;

import DAO.MessageDAO;
import Model.Message;

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
}
