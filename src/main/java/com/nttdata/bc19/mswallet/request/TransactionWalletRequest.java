package com.nttdata.bc19.mswallet.request;

import lombok.Data;

@Data
public class TransactionWalletRequest {
    private String phoneSource;
    private String phoneDestiny;
    private double amount;
}
