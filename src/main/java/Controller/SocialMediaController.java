package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Service.AccountService;
import Service.MessageService;
import Model.Account;
import Model.Message;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
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


}