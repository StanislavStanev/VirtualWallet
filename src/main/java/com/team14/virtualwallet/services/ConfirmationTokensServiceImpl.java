package com.team14.virtualwallet.services;

import com.team14.virtualwallet.exceptions.AccessDeniedException;
import com.team14.virtualwallet.exceptions.EntityNotFoundException;
import com.team14.virtualwallet.models.ConfirmationToken;
import com.team14.virtualwallet.models.User;
import com.team14.virtualwallet.repositories.ConfirmationTokensRepository;
import com.team14.virtualwallet.services.contracts.ConfirmationTokensService;
import com.team14.virtualwallet.services.contracts.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
public class ConfirmationTokensServiceImpl implements ConfirmationTokensService {

    public static final String TOKEN_IS_EXPIRED = "Token is expired. Token validity is 180 seconds";
    public static final long THREE_MINUTES = 3L;
    public static final int TOKEN_TYPE_EMAIL = 1;
    public static final int TOKEN_TYPE_TRANSACTION_VERIFICATION = 2;
    private String TOKEN_DOESNT_EXIST = "This token number is incorrect. If are unsure, please re-send the verification e-mail.";
    private ConfirmationTokensRepository confirmationTokensRepository;

    private UsersService usersService;

    @Autowired
    public ConfirmationTokensServiceImpl(ConfirmationTokensRepository confirmationTokensRepository, UsersService usersService) {
        this.confirmationTokensRepository = confirmationTokensRepository;
        this.usersService = usersService;
    }

    @Override
    public ConfirmationToken createToken(String username, Integer tokenType) {
        User user = usersService.findByUsername(username);
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setTokenType(tokenType);
        confirmationToken.setUser(user);
        confirmationToken.setCreatedDate(LocalDateTime.now());
        //Token type=1 --> Email
        if (tokenType == TOKEN_TYPE_EMAIL) {
            confirmationToken.setConfirmationToken(UUID.randomUUID().toString());
        }

        //Token type=2 --> Large Transaction verification
        if (tokenType == TOKEN_TYPE_TRANSACTION_VERIFICATION) {
            Random random = new Random();
            confirmationToken.setConfirmationToken(String.format("%06d", random.nextInt(999999)));
        }
        confirmationToken.setUsed(false);
        confirmationTokensRepository.save(confirmationToken);
        return confirmationToken;
    }

    @Override
    public void validateEmailToken(String token) {
        ConfirmationToken confirmationToken = this.confirmationTokensRepository.findByConfirmationToken(token);
        checkToken(confirmationToken);
        activateUser(confirmationToken);
        confirmationToken.setUsed(true);
        confirmationToken.setActivatedDate(LocalDateTime.now());
        confirmationTokensRepository.save(confirmationToken);
    }

    @Override
    public void validateTransactionToken(String username, String token) {
        User user = usersService.findByUsername(username);
        ConfirmationToken confirmationToken = this.confirmationTokensRepository.findByUserAndToken(user.getId(), token);
        checkToken(confirmationToken);
        confirmationToken.setUsed(true);
        confirmationToken.setActivatedDate(LocalDateTime.now());
        confirmationTokensRepository.save(confirmationToken);
    }

    private void activateUser(ConfirmationToken confirmationToken) {
        User user = usersService.findByUsername(confirmationToken.getUser().getUsername());
        usersService.activateUser(user.getUsername());
    }

    private void checkToken(ConfirmationToken confirmationToken) {
        if (confirmationToken == null || confirmationToken.isUsed()) {
            throw new EntityNotFoundException(TOKEN_DOESNT_EXIST);
        }

        LocalDateTime now = LocalDateTime.now();
        //TODO What will be expiration time for registration link
        if (confirmationToken.getCreatedDate().compareTo(now.minusMinutes(THREE_MINUTES)) < 0) {
            throw new AccessDeniedException(TOKEN_IS_EXPIRED);
        }
    }


}
