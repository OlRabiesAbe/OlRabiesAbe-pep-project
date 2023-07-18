package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        

        return app;
    }


    private void registerAccountHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        Account account = mapper.readValue(ctx.body(), Account.class);

        Account registeredAccount = accountService.registerAccount(account);

        if(registeredAccount != null){
            ctx.json(mapper.writeValueAsString(registeredAccount)); //"the response body should contain a JSON of the Account, including its account_id."
            ctx.status(200);
        }else{
            ctx.status(400);
        }
    }

    private void loginAccountHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        Account account = mapper.readValue(ctx.body(), Account.class);

        Account loggedInAccount = accountService.loginAccount(account);

        if(loggedInAccount != null){
            ctx.json(mapper.writeValueAsString(loggedInAccount));
            ctx.status(200);
        }else{
            ctx.status(401);
        }
    }

    private void postMessageHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        Message msg = mapper.readValue(ctx.body(), Message.class);

        Message postedMsg = messageService.postMessage(msg);

        if(postedMsg != null){
            ctx.json(mapper.writeValueAsString(postedMsg));
            ctx.status(200);
        }else{
            ctx.status(400);
        }
    }


}