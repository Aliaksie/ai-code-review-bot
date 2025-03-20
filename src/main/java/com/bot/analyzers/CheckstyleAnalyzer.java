package com.bot.analyzers;

import java.io.File;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import com.bot.models.AIException;
import com.bot.models.CodeRecommendation;
import com.bot.models.GitFile;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.Configuration;

@Component
public class CheckstyleAnalyzer implements StaticAnalyzer {

   private final String checkstyleConfig;

   public CheckstyleAnalyzer( @Value( "${checkstyle.config:}" ) String config ) {
      this.checkstyleConfig = config.isEmpty() ? getDefaultConfig() : config;
   }

   @Override
   public List<CodeRecommendation> analyze( final GitFile file ) {
      List<CodeRecommendation> recommendations = new ArrayList<>();
      File tempFile = null;
      try {
         tempFile = File.createTempFile( file.filename(), ".java" );
         Files.writeString( tempFile.toPath(), file.content() );

         // Load Checkstyle configuration
         Configuration config = ConfigurationLoader.loadConfiguration( new InputSource( new StringReader( checkstyleConfig ) ),
               new PropertiesExpander( System.getProperties() ), ConfigurationLoader.IgnoredModulesOptions.OMIT );

         Checker checker = new Checker();
         checker.setModuleClassLoader( Thread.currentThread().getContextClassLoader() );

         AuditEventListener listener = new AuditEventListener( recommendations, file );
         checker.addListener( listener );
         checker.configure( config );

         checker.process( List.of( tempFile ) );

         return recommendations;

      } catch ( Exception e ) {
         throw new AIException( "Checkstyle error: " + e.getMessage() ); // todo!
      } finally {
         //  Delete the temporary file
         if ( tempFile != null && tempFile.exists() ) {
            tempFile.delete();
         }
      }
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
      private final GitFile gitFile;

      public AuditEventListener( List<CodeRecommendation> recommendations, GitFile gitFile ) {
         this.recommendations = recommendations;
         this.gitFile = gitFile;
      }

      @Override
      public void addError( AuditEvent event ) {
         int line = event.getLine();
         String message = event.getMessage();

         recommendations.add(
               new CodeRecommendation( gitFile.filename(), line > 0 ? line : -1, message, gitFile.content(), CodeRecommendation.Type.CHECKSTYLE ) );
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
