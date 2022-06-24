package com.nttdata.bc19.mswallet.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class ExceptionResponse {

    private LocalDateTime date;
    private String message;
    private String detail;
}
