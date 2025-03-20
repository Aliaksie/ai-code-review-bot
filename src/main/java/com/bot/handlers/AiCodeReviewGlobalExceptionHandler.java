package com.bot.handlers;

import java.time.LocalDateTime;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.bot.models.AIClientException;

@RestControllerAdvice
public class AiCodeReviewGlobalExceptionHandler {

   @ExceptionHandler( Exception.class )
   public ResponseEntity<JSONObject> handleGenericException( Exception ex, WebRequest request ) {
      JSONObject errorResponse = getErrorResponse( ex.getMessage(), request, HttpStatus.INTERNAL_SERVER_ERROR );

      return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR ).body( errorResponse );
   }

   @ExceptionHandler( IllegalArgumentException.class )
   public ResponseEntity<JSONObject> handleBadRequest( IllegalArgumentException ex, WebRequest request ) {
      JSONObject errorResponse = getErrorResponse( ex.getMessage(), request, HttpStatus.BAD_REQUEST );

      return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( errorResponse );
   }

   @ExceptionHandler( AIClientException.class )
   public ResponseEntity<JSONObject> handleAIClientError( AIClientException ex, WebRequest request ) {
      JSONObject errorResponse = getErrorResponse( "AI service error: " + ex.getMessage(), request, HttpStatus.SERVICE_UNAVAILABLE );

      return ResponseEntity.status( HttpStatus.SERVICE_UNAVAILABLE ).body( errorResponse );
   }

   private static JSONObject getErrorResponse( String msg, WebRequest request, HttpStatus internalServerError ) {
      return new JSONObject()
            .put( "timestamp", LocalDateTime.now().toString() )
            .put( "message", msg )
            .put( "path", request.getDescription( false ) )
            .put( "status", internalServerError.value() );
   }
}
