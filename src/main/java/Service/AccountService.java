package Service;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {
    private AccountDAO accountDAO;

    public AccountService() {
        accountDAO = new AccountDAO();
    }

    public Account registerAccount(Account account) {
        
        if(account.getUsername().length() == 0
                || account.getPassword().length() < 4)
            return null;

        return accountDAO.registerAccount(account);
        //return new Account(2, "username", "password");
    }

    public Account loginAccount(Account account) {

        return accountDAO.loginAccount(account);

    }
}