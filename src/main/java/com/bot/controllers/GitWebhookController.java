package com.bot.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bot.services.PRReviewService;

@RestController
@RequestMapping( "/git" )
public class GitWebhookController {

   final PRReviewService prReviewService;

   public GitWebhookController( PRReviewService prReviewService ) {
      this.prReviewService = prReviewService;
   }

   @PostMapping( "/ai-webhook" )
   public ResponseEntity<String> handleGitHubWebhook( @RequestHeader Map<String, String> headers, @RequestBody String payload ) {
      prReviewService.reviewPR( headers, payload );
      return ResponseEntity.ok( "Webhook received" );
   }
}
