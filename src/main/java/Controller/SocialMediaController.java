package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Service.AccountService;
import Model.Account;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    AccountService accountService;
    
    public SocialMediaController() {
        accountService = new AccountService();
    }

    public Javalin startAPI() {
        Javalin app = Javalin.create();

        app.post("/register", this::registerAccountHandler);

        return app;
    }

    /**
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void registerAccountHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        Account account = mapper.readValue(ctx.body(), Account.class);

        Account registeredAccount = accountService.registerAccount(account);

        if(registeredAccount!=null){
            ctx.json(mapper.writeValueAsString(registeredAccount)); //"the response body should contain a JSON of the Account, including its account_id."
            ctx.status(200);
        }else{
            ctx.status(400);
        }
    }


}