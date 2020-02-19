package com.team14.virtualwallet.services.contracts;

import com.team14.virtualwallet.models.dtos.apitopupdto.TopUpDto;

public interface ApiTopUpRequest {

    void sendTopUpRequestToAPI(TopUpDto topUpDto, String key);

}
