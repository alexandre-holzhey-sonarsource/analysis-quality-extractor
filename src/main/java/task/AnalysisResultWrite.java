package task;

import com.google.gson.Gson;
import extractor.ApiConnector;
import extractor.ProjectAnalysis;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import model.Component;
import model.ProjectAnalysisQuality;

import static task.AnalysisResultFromFile.OUTPUT_FOLDER_BASE;
import static task.AnalysisResultFromFile.OUTPUT_FOLDER_TARGET;

public class AnalysisResultWrite {
  private static final String PEACH_URL = "https://peach.sonarsource.com";

  private static final List<String> projects = List.of(
    "com.ibm.security.appscan.altoromutual",
    "org.apache.tika:tika",
    // "apps-android-wikipedia",
    "com.github.sworm:spojo-parent",
    "com.google.code.simple-spring-memcached:simple-spring-memcached-parent",
    "com.mpatric:mp3agic",
    "com.puppycrawl.tools:checkstyle",
    "com.qualinsight.plugins.sonarqube:qualinsight-plugins-sonarqube-badges",
    "com.qualinsight.plugins.sonarqube:qualinsight-plugins-sonarqube-smell",
    "com.redhat.lightblue:lightblue-core-pom",
    "com.redhat.lightblue.hook:lightblue-audit-hook",
    "com.sparkjava:spark-core",
    "com.zaxxer:HikariCP",
    "commons-logging:commons-logging",
    "de.lgohlke.selenium:pageobjects",
    "com.appsecco:dvja",
    "es.usc.citius.hipster:hipster-pom",
    "io.fabric8:docker-maven-plugin",
    "io.fabric8:fabric8-maven-plugin-build",
    "org.eclipse.jetty:jetty-project",
    "junit:junit",
    "net.code-story:http",
    "net.masterthought:cucumber-reporting",
    "net.sf.aislib:aislib",
    "net.sf.flatpack:flatpack-parent",
    // "org.nuxeo:nuxeo-ecm",
    "org.activiti:activiti-root",
    "org.activiti:activiti-root",
    "org.apache.abdera:abdera",
    "org.apache.bcel:bcel",
    "org.apache.cayenne:cayenne-parent",
    "org.apache.commons:commons-collections4",
    "org.apache.commons:commons-configuration2",
    "org.apache.commons:commons-exec",
    "org.apache.commons:commons-pool2",
    "org.apache.commons:commons-vfs2-project",
    "org.apache.directory.api:api-parent",
    "org.apache.empire-db:empire-db-parent",
    "org.apache.jackrabbit:jackrabbit",
    "org.apache.maven.enforcer:enforcer",
    "org.apache.maven.release:maven-release",
    "org.apache.myfaces.extensions.cdi:myfaces-extcdi-parent",
    "org.apache.myfaces.tobago:tobago",
    "org.apache.opennlp:opennlp",
    "org.apache.portals.pluto:pluto",
    "org.apache.xbean:xbean",
    "org.assertj:assertj-assertions-generator",
    "org.assertj:assertj-joda-time",
    "org.assertj:assertj-parent-pom",
    "org.codefx.demo:java-x",
    "org.codehaus.sonar-plugins:ebcdic-ascii-converter",
    "org.codehaus.sonar-plugins:sonar-branding-plugin",
    "org.codehaus.sonar-plugins:sonar-build-stability-plugin",
    "org.codehaus.sonar-plugins:sonar-cobertura-plugin",
    "org.codehaus.sonar-plugins:sonar-googleanalytics-plugin",
    "org.codehaus.sonar-plugins:sonar-issue-assign-plugin",
    "org.codehaus.sonar-plugins:sonar-issues-report-plugin",
    "org.codehaus.sonar-plugins:sonar-jira-plugin",
    "org.codehaus.sonar-plugins:sonar-motion-chart-plugin",
    "org.codehaus.sonar-plugins:sonar-openid-plugin",
    "org.codehaus.sonar-plugins:sonar-pitest-plugin",
    "org.codehaus.sonar-plugins:sonar-sonargraph-plugin",
    "org.codehaus.sonar-plugins:sonar-tab-metrics-plugin",
    "org.codehaus.sonar-plugins:sonar-timeline-plugin",
    "org.codehaus.sonar-plugins:sonar-total-quality-plugin",
    "org.codehaus.sonar-plugins:sonar-trac-plugin",
    "org.codehaus.sonar-plugins:sonar-useless-code-tracker-plugin",
    "org.codehaus.sonar-plugins.android:sonar-android-plugin",
    "org.codehaus.sonar-plugins.css:css",
    "org.codehaus.sonar-plugins.jmeter:parent",
    "org.codehaus.sonar-plugins.l10n:sonar-l10n-fr-plugin",
    "org.codehaus.sonar-plugins.l10n:sonar-l10n-ko-plugin",
    "org.codehaus.sonar-plugins.l10n:sonar-l10n-pt-plugin",
    "org.codehaus.sonar-plugins.stylecop:sonar-stylecop-plugin",
    "org.codehaus.sonar-plugins.toxicity-chart:sonar-toxicity-chart-plugin",
    "org.commonjava.googlecode.markdown4j:markdown4j",
    "org.easybatch:easybatch",
    "org.easyrules:easyrules",
    "org.easytesting:fest-util",
    // "org.eclipse.hudson.stapler:stapler-parent",
    "org.grouplens.grapht:grapht",
    "org.jacoco:root",
    "org.jdbdt:jdbdt",
    "org.mobicents.servlet.sip:sip-servlets-bootstrap",
    "org.ops4j.pax:logging",
    "org.polyforms:polyforms",
    "org.sonarsource.clirr:sonar-clirr-plugin",
    "org.sonarsource.clover:sonar-clover-plugin",
    "org.sonarsource.flex:flex",
    "org.sonarsource.ldap:sonar-ldap",
    "org.sonarsource.php:php",
    "org.sonarsource.python:python",
    "org.sonarsource.scanner.ant:ant",
    "org.sonarsource.scanner.api:sonar-scanner-api-parent",
    "org.sonarsource.sonar-findbugs-plugin:sonar-findbugs-plugin",
    "org.sonarsource.sslr-squid-bridge:sslr-squid-bridge",
    "org.sonarsource.sslr:sslr",
    "org.sonarsource.update-center:sonar-update-center",
    "org.sonarsource.widget-lab:sonar-widget-lab-plugin",
    "org.sonarsource.xml:xml",
    "org.symphonyoss.symphony:symphony-java-client",
    "pl.damianszczepanik:silencio",
    "ro.pippo:pippo-parent",
    // "org.springframework:spring",
    // "com.facebook:buck",
    // "com.squareup.okio:okio-parent",
    "com.google.guava:guava-parent",
    "com.google.guava:guava-parent",
    "java10-test",
    "io.spring.initializr:initializr",
    "javax.money:money-api",
    // "net.java.openjdk:jdk9",
    "io.github.jhipster:jhipster-parent",
    "io.github.jhipster.sample:jhipster-sample-application",
    "org.jooq:jooq-parent",
    "org.apache.wicket:wicket-parent",
    "org.codehaus.sonar-plugins.erlang-erlang",
    "org.javamoney:moneta-peach",
    "org.sonarsource.javascript:javascript",
    // "timusus-shuttle",
    "org.sonarsource:sonar-persistit",
    "com.sonarsource.cpp:cpp",
    "org.sonarsource.groovy:groovy",
    "org.sonarsource.java:java",
    "sonar-java-checks-test-sources-peach",
    // "org.sonarsource.sonarqube:sonarqube",
    "org.springframework.samples:spring-petclinic",
    "com.alibaba.spring:spring-velocity-support",
    "org.apache:tomcat7",
    "org.apache:tomcat8",
    "org.apache:tomcat9",
    "wavsep",
    "org.wildfly:wildfly-parent",
    "org.xwiki.commons:xwiki-commons:9.3.1",
    "spring_petclinic_vulnerable_contrast",
    "com.sonarsource.securityexpectedissues:S5131servletjsp",
    "org.springframework.samples:spring-framework-petclinic",
    "com.sonarsource.securityexpectedissues:S5131springmvc-jsp",
    "com.sonarsource.securityexpectedissues:S5131springmvc-thymeleaf",
    "com.sonarsource.securityexpectedissues:S5131servlet",
    "com.sonarsource.securityexpectedissues:S5131springmvc",
    "org.springframework.boot:spring-boot-sample-web-jsp",
    "micronaut-projects:micronaut-core",
    // "micrometer-metrics:micrometer",
    "netflix:conductor",
    "ls1intum:artemis",
    "mesosphere:dcos-commons",
    "org.xwiki.commons:xwiki-commons",
    "netflix-zuul",
    "netflix-eureka",
    "netflix-ribbon",
    "netflix-conductor",
    "netflix-concurrency-limits",
    "netflix-evcache",
    "netflix-Priam",
    "netflix-metacat",
    "netflix-spectator",
    "netflix-mantis",
    "netflix-genie",
    // "rxjava",
    "jadx"
  // "kafka",
  // "org.apache.rocketmq:rocketmq-all"
  );

  public static void main(String[] args) throws IOException {
    ApiConnector apiConnector = new ApiConnector(PEACH_URL);
    ProjectAnalysis projectAnalysis = new ProjectAnalysis(apiConnector, null);

    boolean base = false;

    String outputFolder;
    if (base) {
      outputFolder = OUTPUT_FOLDER_BASE;
    } else {
      outputFolder = OUTPUT_FOLDER_TARGET;
    }

    List<Component> baseComponents = apiConnector.getProjects(projects);

    Gson gson = new Gson();
    Files.createDirectories(Paths.get(outputFolder));

    ExecutorService executor = Executors.newFixedThreadPool(10);
    for (Component baseComponent : baseComponents) {
      executor.submit(() -> {
        try {
          ProjectAnalysisQuality paq = projectAnalysis.toAnalysisQuality(baseComponent);
          ProjectAnalysisQuality paqExtracted = projectAnalysis.extractResult(paq);
          try (FileWriter fileWriter = new FileWriter(outputFolder + paqExtracted.getBaseComponent().getKey().replaceAll(":", "_"))) {
            gson.toJson(paqExtracted, fileWriter);
          }
        } catch (Exception e) {
          System.err.println("[ERROR] Fail to download " + baseComponent + ": " + e.getMessage());
        }
      });

    }
    executor.shutdown();
  }
}
