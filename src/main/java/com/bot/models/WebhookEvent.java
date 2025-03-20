package com.bot.models;

public record WebhookEvent(
      String repositoryId,   // Repo name or ID
      String repositoryUrl,  // Repo URL
      int pullRequestId,     // PR ID
      String action,         // Opened, Updated, Merged, etc.
      String sourceBranch,   // Branch being merged
      String targetBranch   // Destination branch
) {
}
