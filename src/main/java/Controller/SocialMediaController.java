package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;

import Service.AccountService;
import Service.MessageService;
import Model.Account;
import Model.Message;


public class SocialMediaController {
    AccountService accountService;
    MessageService messageService;
    
    public SocialMediaController() {
        accountService = new AccountService();
        messageService = new MessageService();
    }

    public Javalin startAPI() {
        Javalin app = Javalin.create();

        app.post("/register", this::registerAccountHandler);
        app.post("/login", this::loginAccountHandler);

        app.post("/messages", this::postMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("messages/{message_id}", this::getMessageByIdHandler);
        app.delete("messages/{message_id}", this::deleteMessageByIdHandler);
        app.patch("/messages/{message_id}", this::patchMessageTextByIdHandler);
        app.get("/accounts/{account_id}/messages", this::getAllMessagesByAccountIdHandler);

        return app;
    }

    /*
     * Inserts an account into the database.
     */
    private void registerAccountHandler(Context ctx) throws JsonProcessingException {
        
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);

        Account registeredAccount = accountService.registerAccount(account);

        if(registeredAccount != null) {
            ctx.json(mapper.writeValueAsString(registeredAccount)); //"the response body should contain a JSON of the Account, including its account_id."
            ctx.status(200);
        } else {
            ctx.status(400);
        }
    }

    /*
     * Logs into associated account, should the username and password match.
     * Doesn't actually do anything, this is basically just a select-where call.
     */
    private void loginAccountHandler(Context ctx) throws JsonProcessingException {
        
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);

        Account loggedInAccount = accountService.loginAccount(account);

        if(loggedInAccount != null) {
            ctx.json(mapper.writeValueAsString(loggedInAccount));
            ctx.status(200);
        } else {
            ctx.status(401);
        }
    }

    /*
     * Inserts the provided message into the database.
     */
    private void postMessageHandler(Context ctx) throws JsonProcessingException {
        
        ObjectMapper mapper = new ObjectMapper();
        Message msg = mapper.readValue(ctx.body(), Message.class);

        Message postedMsg = messageService.postMessage(msg);

        if(postedMsg != null) {
            ctx.json(mapper.writeValueAsString(postedMsg));
            ctx.status(200);
        } else {
            ctx.status(400);
        }
    }
    
    /*
     * Retrieves all messages in the database as an ArrayList.
     */
    private void getAllMessagesHandler(Context ctx) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();

        ArrayList<Message> messageList = messageService.getAllMessages();

        ctx.json(mapper.writeValueAsString(messageList));
        ctx.status(200);
    }

    /*
     * Retrieves the message associated with the provided id.
     */
    private void getMessageByIdHandler(Context ctx) throws JsonProcessingException, NumberFormatException { //NumberFormatException because Integer.parseInt

        ObjectMapper mapper = new ObjectMapper();

        int message_id = Integer.parseInt(ctx.pathParam("message_id"));

        Message msg = messageService.getMessageById(message_id);

        if(msg != null) {
            ctx.json(mapper.writeValueAsString(msg));
        } else { //return empty json if the message comes back null (not in database)
            ctx.json("");
        }
        ctx.status(200); //always 200
    }

    /*
     * Deletes the message with the associated id.
     * Returns an empty Json object if the message didn't exist in the first place,
     * otherwise the Json contains the new message.
     */
    private void deleteMessageByIdHandler(Context ctx) throws JsonProcessingException, NumberFormatException { //NumberFormatException because Integer.parseInt

        ObjectMapper mapper = new ObjectMapper();

        int message_id = Integer.parseInt(ctx.pathParam("message_id"));

        Message msg = messageService.deleteMessageById(message_id);

        if(msg != null) {
            ctx.json(mapper.writeValueAsString(msg));
        } else { //return empty json if the message comes back null (not in database)
            ctx.json("");
        }
        ctx.status(200); //always 200
    }

    /*
     * Replaces the message_text of the message with the associated message_id.
     */
    private void patchMessageTextByIdHandler(Context ctx) throws JsonProcessingException, NumberFormatException { //NumberFormatException because Integer.parseInt

        ObjectMapper mapper = new ObjectMapper();

        int message_id = Integer.parseInt(ctx.pathParam("message_id"));
        String newMessage_text = mapper.readValue(ctx.body(), Message.class).getMessage_text();

        Message msg = messageService.patchMessageTextById(message_id, newMessage_text);

        if(msg != null) {
            ctx.json(mapper.writeValueAsString(msg));
            ctx.status(200);
        } else { 
            ctx.status(400);
        }
    }

    /*
     * Retrieves all messages where posted_by equals the provided account_id.
     * Returns an empty list if there's no messages by the account or the account doesn't exist.
     * There's no outwardly visible difference between these two cases.
     */
    private void getAllMessagesByAccountIdHandler(Context ctx) throws JsonProcessingException, NumberFormatException { //NumberFormatException because Integer.parseInt

        ObjectMapper mapper = new ObjectMapper();

        int account_id = Integer.parseInt(ctx.pathParam("account_id"));

        ArrayList<Message> messageList = messageService.getAllMessagesByAccountId(account_id);

        ctx.json(mapper.writeValueAsString(messageList));
        ctx.status(200); //always 200
    }
    
}