package com.nttdata.bc19.mswallet.request;

import lombok.Data;

@Data
public class TransactionWalletCoinRequest {
    private String phoneSource;
    private String phoneDestiny;
    private double amountSource;
    private double amountDestiny;
}
