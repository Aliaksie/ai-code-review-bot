package com.bot.utility;

import java.util.Map;

public class LanguageDetector {
   private static final Map<String, String> EXTENSION_TO_LANGUAGE = Map.ofEntries(
         Map.entry( "java", "java" ),
         Map.entry( "kt", "kotlin" ),
         Map.entry( "py", "python" ),
         Map.entry( "js", "javascript" ),
         Map.entry( "ts", "typescript" ),
         Map.entry( "cs", "csharp" ),
         Map.entry( "cpp", "cpp" ),
         Map.entry( "c", "c" ),
         Map.entry( "rb", "ruby" ),
         Map.entry( "go", "go" ),
         Map.entry( "rs", "rust" ),
         Map.entry( "scala", "scala" ),
         Map.entry( "php", "php" ),
         Map.entry( "swift", "swift" )
   );

   public static String detect( String filename ) {
      int i = filename.lastIndexOf( '.' );
      if ( i > 0 ) {
         String ext = filename.substring( i + 1 ).toLowerCase();
         return EXTENSION_TO_LANGUAGE.getOrDefault( ext, "unknown" );
      }
      return "unknown";
   }

   private LanguageDetector() {
      // private constructor to prevent instantiation
   }
}
