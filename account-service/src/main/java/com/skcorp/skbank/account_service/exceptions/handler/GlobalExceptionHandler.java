package com.skcorp.skbank.account_service.exceptions.handler;

import com.skcorp.skbank.account_service.client.models.BadRequestErrorResponse;
import com.skcorp.skbank.account_service.client.models.UnauthorizedErrorResponse;
import com.skcorp.skbank.account_service.exceptions.JwtException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

     @ExceptionHandler
     private ResponseEntity<BadRequestErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {

         String field = methodArgumentNotValidException
                 .getBindingResult()
                 .getFieldError().getField();
         String message = field + " " +  methodArgumentNotValidException
                 .getBindingResult()
                 .getFieldError().getDefaultMessage();
         BadRequestErrorResponse response = new BadRequestErrorResponse();
         response.setMessage(message);
         response.setSuccess(false);
         return ResponseEntity.badRequest().body(response);
     }

     @ExceptionHandler
     private ResponseEntity<BadRequestErrorResponse> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException constraintViolationException) {
         BadRequestErrorResponse response = new BadRequestErrorResponse();
         response.setSuccess(false);
         response.setMessage(constraintViolationException.getMessage());
         return ResponseEntity.status(409).body(response);
     }

     @ExceptionHandler
     private ResponseEntity<UnauthorizedErrorResponse> handleJwtException(JwtException jwtException) {
         UnauthorizedErrorResponse response = new UnauthorizedErrorResponse();
         response.setMessage(jwtException.getMessage());
         response.setSuccess(false);

         return ResponseEntity.status(401).body(response);
     }
}
