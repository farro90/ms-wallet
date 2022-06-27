package com.nttdata.bc19.mswallet.request;

import lombok.Data;

@Data
public class RequestBuyCoinRequest {
    private String phoneClientCoin;
    private double coinsToBuy;
}
