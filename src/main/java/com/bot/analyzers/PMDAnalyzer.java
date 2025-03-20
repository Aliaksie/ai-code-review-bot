package com.bot.analyzers;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.rule.RuleSet;
import net.sourceforge.pmd.lang.rule.RuleSetLoader;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.RuleViolation;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.bot.models.AIException;
import com.bot.models.CodeRecommendation;
import com.bot.models.GitFile;

@Component
public class PMDAnalyzer implements StaticAnalyzer {
   private final String pmdRules;

   public PMDAnalyzer( @Value( "${pmd.rules:}" ) String rules ) {
      this.pmdRules = rules.isEmpty() ? getDefaultRules() : rules;
   }

   @Override
   public List<CodeRecommendation> analyze( final GitFile file ) {
      File tempFile = null;
      try {
         List<CodeRecommendation> recommendations = new ArrayList<>();
         tempFile = File.createTempFile( file.filename(), ".java" );
         tempFile.deleteOnExit();  // Ensure the temp file is deleted when the program ends
         try ( FileWriter writer = new FileWriter( tempFile ) ) {
            writer.write( file.content() );
         }

         PMDConfiguration config = new PMDConfiguration();
         // todo!!!!
         config.setDefaultLanguageVersion( JavaLanguageModule.getInstance().getDefaultVersion() );

         // ✅ Load rules dynamically
         RuleSetLoader ruleSetLoader = RuleSetLoader.fromPmdConfig( config );
         RuleSet ruleSet = ruleSetLoader.loadFromString( "pmd_rules", pmdRules );

         try ( PmdAnalysis pmdAnalysis = PmdAnalysis.create( config ) ) {
            // todo!
            pmdAnalysis.files().addFile( tempFile.toPath() );
            pmdAnalysis.addRuleSet( ruleSet );
            Report report = pmdAnalysis.performAnalysisAndCollectReport();

            // ✅ Collect rule violations
            for ( RuleViolation violation : report.getViolations() ) {
               recommendations.add(
                     new CodeRecommendation( file.filename(), violation.getBeginLine(), violation.getDescription(), file.content(),
                           CodeRecommendation.Type.PMD ) );
            }
         }
         return recommendations;
      } catch ( Exception e ) {
         throw new AIException( "PMD analysis failed: " + e.getMessage() );
      } finally {
         if ( tempFile != null && tempFile.exists() ) {
            tempFile.delete();
         }
      }
   }

   private String getDefaultRules() {
      return """
                <ruleset name="Default Rules" xmlns="http://pmd.sourceforge.net/ruleset/2.0.0">
                    <rule ref="category/java/bestpractices.xml"/>
                    <rule ref="category/java/performance.xml"/>
                    <rule ref="category/java/errorprone.xml"/>
                </ruleset>
            """;
   }
}