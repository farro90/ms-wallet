package com.nttdata.bc19.mswallet.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RateCoin extends BaseModel{
    private RateType rateType;
    private double rate;
}
