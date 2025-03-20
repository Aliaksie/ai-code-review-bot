package com.bot.controllers;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bot.models.AIClientException;
import com.bot.services.PRReviewService;

@RestController
@RequestMapping( "/git" )
public class GitWebhookController {

   final PRReviewService prReviewService;

   public GitWebhookController( PRReviewService prReviewService ) {
      this.prReviewService = prReviewService;
   }

   @PostMapping( "/ai-webhook" )
   public ResponseEntity<String> handleGitHubWebhook( @RequestBody String payload ) {
      try {
         // todo: get from payload(GitHub, AzureRepos, GitLab) prId, repoId, prStatus...
         prReviewService.reviewPR( "", "" );
         return ResponseEntity.ok( "Webhook received" );
      } catch ( IOException e ) {
         throw new AIClientException( "Errot: " + e.getMessage() ); // todo: error handler
      }
   }
}
