package com.bot.analyzers;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.lang.LanguageRegistry;
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

import com.bot.models.AIClientException;
import com.bot.models.CodeRecommendation;

@Component
public class PMDAnalyzer implements StaticAnalyzer {
   private final String pmdRules;

   public PMDAnalyzer( @Value( "${pmd.rules:}" ) String rules ) {
      this.pmdRules = rules.isEmpty() ? getDefaultRules() : rules;
   }

   @Override
   public List<CodeRecommendation> analyze( final String filePath, String fileContent ) {
      File tempFile = null;
      try {
         List<CodeRecommendation> recommendations = new ArrayList<>();
         tempFile = File.createTempFile( "pmd_analysis_", ".java" );
         tempFile.deleteOnExit();  // Ensure the temp file is deleted when the program ends
         try ( FileWriter writer = new FileWriter( tempFile ) ) {
            writer.write( fileContent );
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
               recommendations.add( new CodeRecommendation( filePath, violation.getBeginLine(), "[PMD] " + violation.getDescription() ) );
            }
         }
         return recommendations;
      } catch ( Exception e ) {
         throw new AIClientException( "PMD analysis failed: " + e.getMessage() );
      } finally {
         if ( tempFile != null && tempFile.exists() ) {
            tempFile.delete();
         }
      }
   }

   @Override
   public String type() {
      return "PMD";
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