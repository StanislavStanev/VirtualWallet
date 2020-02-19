package com.team14.virtualwallet.services.contracts;

import com.team14.virtualwallet.models.ConfirmationToken;

public interface ConfirmationTokensService {

    ConfirmationToken createToken(String username, Integer tokenType);

    void validateEmailToken(String token);

    void validateTransactionToken(String username, String token);
}
