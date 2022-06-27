package com.nttdata.bc19.mswallet.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionCoin extends BaseModel{
    private String idRequestBuyCoin;
    private RequestBuyCoin requestBuyCoin;
    private String idClientCoinSale;
    private ClientCoin clientCoinSale;
}
