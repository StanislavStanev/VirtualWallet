package com.team14.virtualwallet.services;

import com.team14.virtualwallet.exceptions.CommunicationErrorException;
import com.team14.virtualwallet.exceptions.NotSufficientFundsException;
import com.team14.virtualwallet.models.dtos.apitopupdto.TopUpDto;
import com.team14.virtualwallet.services.contracts.ApiTopUpRequest;
import com.team14.virtualwallet.utils.ModelFactory;
import org.cloudinary.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class ApiTopUpRequestImpl implements ApiTopUpRequest {
    private static final String API_URL = "http://localhost:8081/payment";
    private static final String API_KEY_HEADER = "x-api-key";
    private static final String API_KEY_VALUE = "a";
    private static final String INSUFFICIENT_FUNDS = "Insufficient Funds";
    private static final int HTTP_STATUS_FORBIDDEN = 403;
    private static final int AMOUNT_MULTIPLIER = 100;
    private static final String COMMUNICATION_ERROR = "Unsuccessful transfer, because of communication error.\nPlease contact support";

    public void sendTopUpRequestToAPI(TopUpDto topUpDto, String key) {
        topUpDto.setIdempotencyKey(key);
        //Set amount*100 because of API specifics. 1.43 EUR should be send as 143.
        topUpDto.setAmount(topUpDto.getAmount().multiply(BigDecimal.valueOf(AMOUNT_MULTIPLIER)));

        RestTemplate restTemplate = ModelFactory.createRestTemplate();

        HttpHeaders headers = ModelFactory.createHttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(API_KEY_HEADER, API_KEY_VALUE);

        JSONObject topUpJsonObject = ModelFactory.createJson(topUpDto);

        HttpEntity<String> request = new HttpEntity<String>(topUpJsonObject.toString(), headers);
        try {
            restTemplate.postForEntity(API_URL, request, String.class);
        } catch (ResourceAccessException e) {
            throw new CommunicationErrorException(COMMUNICATION_ERROR);
        } catch (HttpStatusCodeException code) {
            switch (code.getStatusCode().value()) {
                case HTTP_STATUS_FORBIDDEN:
                    throw new NotSufficientFundsException(INSUFFICIENT_FUNDS);
                default:
                    throw new CommunicationErrorException(COMMUNICATION_ERROR);
            }
        }

        //Set back amount divided by 100 because of API specifics. Now we have 143, which should be converted back to 1.43.
        topUpDto.setAmount(topUpDto.getAmount().divide(BigDecimal.valueOf(AMOUNT_MULTIPLIER)));
    }
}
