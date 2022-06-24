package com.nttdata.bc19.mswallet.request;

import lombok.Data;

@Data
public class RechargeWalletRequest {
    private String phoneReceiver;
    private double amount;
}
