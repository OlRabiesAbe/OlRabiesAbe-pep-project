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
        

        return app;
    }


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

    private void getAllMessagesHandler(Context ctx) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();

        ArrayList<Message> messageList = messageService.getAllMessages();

        ctx.json(mapper.writeValueAsString(messageList));
        ctx.status(200);
    }

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

    
    private void patchMessageTextByIdHandler(Context ctx) throws JsonProcessingException, NumberFormatException { //NumberFormatException because Integer.parseInt

        ObjectMapper mapper = new ObjectMapper();

        int message_id = Integer.parseInt(ctx.pathParam("message_id"));
        /* the below line might not work
           i don't know in what way the message_text is contained in ctx, or if this is a valid way to extract it */
        String newMessage_text = mapper.readValue(ctx.body(), Message.class).getMessage_text();

        Message msg = messageService.patchMessageTextById(message_id, newMessage_text);

        if(msg != null) {
            ctx.json(mapper.writeValueAsString(msg));
            ctx.status(200);
        } else { 
            ctx.status(400);
        }
    }
}