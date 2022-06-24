package com.nttdata.bc19.mswallet.exception;

import com.nttdata.bc19.mswallet.util.LogMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@RestController
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger EXCEPTIONLOGGER = LoggerFactory.getLogger("ExceptionLog");
    private final String ERROR = "ERROR";
    private final String VALIDATION = "VALIDATION";
    private String mensaje = "";

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ExceptionResponse> handleAllException(RuntimeException ex, WebRequest request){
        EXCEPTIONLOGGER.error(LogMessage.logMessage.get(ERROR) + ex.getMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ModelNotFoundException.class)
    public final ResponseEntity<ExceptionResponse> handleModelException(ModelNotFoundException ex, WebRequest request){
        EXCEPTIONLOGGER.warn(LogMessage.logMessage.get(VALIDATION) + ex.getMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<ExceptionResponse>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders header, HttpStatus status, WebRequest request){

        ex.getBindingResult().getAllErrors().forEach(e -> {
            mensaje += e.getDefaultMessage().toString() + ";";
        });
        EXCEPTIONLOGGER.warn(LogMessage.logMessage.get(VALIDATION) + mensaje);
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDateTime.now(), mensaje, request.getDescription(false));
        return new ResponseEntity<Object>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
