package com.bot.models;

public record CodeRecommendation(String filePath,
                                 String language,
                                 int line,
                                 String msg,
                                 String content,
                                 Type type) {

   public enum Type {
      CHECKSTYLE,
      FIND_BUGS,
      PMD,
      SONAR_QUBE
   }
}
