package com.bot.analyzers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import com.bot.models.AIClientException;
import com.bot.models.CodeRecommendation;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.api.*;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;

@Component
public class CheckstyleAnalyzer implements StaticAnalyzer {

   private final String checkstyleConfig;

   public CheckstyleAnalyzer( @Value( "${checkstyle.config:}" ) String config ) {
      this.checkstyleConfig = config.isEmpty() ? getDefaultConfig() : config;
   }

   @Override
   public List<CodeRecommendation> analyze( final String filePath, String fileContent ) {
      List<CodeRecommendation> recommendations = new ArrayList<>();
      File tempFile = null;
      try {
         tempFile = File.createTempFile( "checkstyle_", ".java" );
         Files.writeString( tempFile.toPath(), fileContent );

         // Load Checkstyle configuration
         InputSource inputSource = new InputSource( new StringReader( checkstyleConfig ) );
         Configuration config = ConfigurationLoader.loadConfiguration( inputSource, new PropertiesExpander( System.getProperties() ),
               ConfigurationLoader.IgnoredModulesOptions.OMIT );

         Checker checker = new Checker();
         checker.setModuleClassLoader( Thread.currentThread().getContextClassLoader() );

         AuditEventListener listener = new AuditEventListener( recommendations, filePath );
         checker.addListener( listener );
         checker.configure( config );

         // ✅ Run Checkstyle on the temp file
         checker.process( Collections.singletonList( tempFile ) );

         return recommendations;

      } catch ( Exception e ) {
         throw new AIClientException( "Checkstyle error: " + e.getMessage() ); // todo!
      } finally {
         //  Delete the temporary file
         if ( tempFile != null && tempFile.exists() ) {
            tempFile.delete();
         }
      }
   }

   @Override
   public String type() {
      return "Checkstyle";
   }

   private String getDefaultConfig() {
      return """
                <?xml version="1.0"?>
                <!DOCTYPE module PUBLIC "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
                "https://checkstyle.sourceforge.io/dtds/configuration_1_3.dtd">
                <module name="Checker">
                    <module name="TreeWalker">
                        <module name="UnusedImports"/>
                        <module name="AvoidStarImport"/>
                        <module name="FinalClass"/>
                        <module name="ConstantName"/>
                        <module name="MethodName"/>
                        <module name="Indentation"/>
                        <module name="ParameterName"/>
                        <module name="WhitespaceAround"/>
                    </module>
                </module>
            """;
   }

   static class AuditEventListener implements AuditListener {
      private final List<CodeRecommendation> recommendations;
      private final String filePath;

      public AuditEventListener( List<CodeRecommendation> recommendations, String filePath ) {
         this.recommendations = recommendations;
         this.filePath = filePath;
      }

      @Override
      public void addError( AuditEvent event ) {
         int line = event.getLine();
         String message = event.getMessage();

         recommendations.add( new CodeRecommendation( filePath, line > 0 ? line : -1, message ) );
      }

      @Override
      public void addException( AuditEvent event, Throwable throwable ) {
      }

      @Override
      public void auditStarted( AuditEvent event ) {
      }

      @Override
      public void auditFinished( AuditEvent event ) {
      }

      @Override
      public void fileStarted( AuditEvent event ) {
      }

      @Override
      public void fileFinished( AuditEvent event ) {
      }
   }
}
