package com.bot.models;

import java.util.List;

public record SonarIssuesResponse(List<SonarIssue> issues) {

   public record SonarIssue(String component, String message, Integer line) {
   }
}
