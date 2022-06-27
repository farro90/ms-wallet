package com.nttdata.bc19.mswallet.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionWalletCoin extends BaseModel {
    private String phoneSource;
    private String phoneDestiny;
    private double amountSource;
    private double amountDestiny;
}
