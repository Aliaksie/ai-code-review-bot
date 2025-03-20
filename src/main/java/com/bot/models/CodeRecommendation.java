package com.bot.models;

public record CodeRecommendation(String filePath,
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
