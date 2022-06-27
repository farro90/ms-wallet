package com.nttdata.bc19.mswallet.request;

import com.nttdata.bc19.mswallet.model.DocumentType;
import lombok.Data;

@Data
public class CoinRequest {
    private DocumentType documentType;
    private String documentNumber;
    private String phone;
    private String email;
}
