package dev.poncio.todolistmeisters.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity handleException(BusinessException e) {
        // log exception
        return ResponseEntity.status(e.getHttpStatus()).body(new GenericError(e.getHttpStatus(), e.getMessage()));
    }

    @Data
    public class GenericError {

        private String timestamp = LocalDateTime.now().toString();
        private Integer status;
        private String error;

        GenericError(HttpStatus status, String error) {
            this.status = status.value();
            this.error = error;
        }

    }

}