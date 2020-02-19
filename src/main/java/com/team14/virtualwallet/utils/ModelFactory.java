package com.team14.virtualwallet.utils;

import com.team14.virtualwallet.models.*;
import com.team14.virtualwallet.models.dtos.apitopupdto.TopUpDto;
import com.team14.virtualwallet.models.dtos.bankcarddto.BankCardRegisterDto;
import com.team14.virtualwallet.models.dtos.bankcarddto.BankCardUpdateDto;
import com.team14.virtualwallet.models.dtos.transactionDtos.TransactionCreateDto;
import com.team14.virtualwallet.models.dtos.userdto.UserWalletDto;
import com.team14.virtualwallet.models.dtos.walletDto.SharedWalletUsersListDto;
import com.team14.virtualwallet.services.ApiTopUpRequestImpl;
import com.team14.virtualwallet.services.contracts.ApiTopUpRequest;
import org.cloudinary.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

public abstract class ModelFactory {

    public static User createUser() {
        return new User();
    }

    public static Transaction createTransaction() {
        return new Transaction();
    }

    public static Wallet createWallet() {
        return new Wallet();
    }

    public static BankCard createBankCard() {
        return new BankCard();
    }

    public static RestTemplate createRestTemplate() {
        return new RestTemplate();
    }

    public static HttpHeaders createHttpHeaders() {
        return new HttpHeaders();
    }

    public static UserProfile createUserProfile() {
        return new UserProfile();
    }

    public static TransactionCreateDto transactionCreateDto() {
        return new TransactionCreateDto();
    }

    public static BankCardRegisterDto bankCardRegisterDto() {
        return new BankCardRegisterDto();
    }

    public static BankCardUpdateDto bankCardUpdateDto() {
        return new BankCardUpdateDto();
    }

    public static Picture createPicture() {
        return new Picture();
    }

    public static JSONObject createJson(TopUpDto topUpDto) {
        return new JSONObject(topUpDto);
    }

    public static UserWalletDto createUserWalletDto() {
        return new UserWalletDto();
    }

    public static SharedWalletUsersListDto createSharedWalletUsersListDto() {
        return new SharedWalletUsersListDto();
    }

    public static ApiTopUpRequest createApiTopUpRequest() {
        return new ApiTopUpRequestImpl();
    }
}
