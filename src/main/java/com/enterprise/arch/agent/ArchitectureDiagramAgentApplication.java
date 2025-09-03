package com.enterprise.arch.agent;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class for the Enterprise Architecture Diagram Agent.
 * 
 * This AI-powered application automatically generates and maintains enterprise
 * architecture diagrams from codebase analysis using Quarkus, Langchain4j, and Gemini AI.
 * 
 * Key Features:
 * - Intelligent codebase analysis and pattern detection
 * - AI-powered Mermaid diagram generation using Google Gemini
 * - Real-time change detection and incremental updates
 * - GitLab integration with CI/CD pipeline support
 * - Enterprise-grade scalability and monitoring
 * 
 * @author Enterprise Architecture Team
 * @version 1.0.0
 * @since 1.0.0
 */
@QuarkusMain
@ApplicationScoped
public class ArchitectureDiagramAgentApplication implements QuarkusApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArchitectureDiagramAgentApplication.class);

    @ConfigProperty(name = "quarkus.application.name")
    String applicationName;

    @ConfigProperty(name = "quarkus.application.version")
    String applicationVersion;

    @ConfigProperty(name = "quarkus.http.port")
    int httpPort;

    public static void main(String... args) {
        LOGGER.info("Starting Enterprise Architecture Diagram Agent...");
        Quarkus.run(ArchitectureDiagramAgentApplication.class, args);
    }

    @Override
    public int run(String... args) throws Exception {
        LOGGER.info("========================================");
        LOGGER.info("🚀 {} v{}", applicationName, applicationVersion);
        LOGGER.info("========================================");
        LOGGER.info("📊 AI-Powered Architecture Diagram Generator");
        LOGGER.info("🔧 Technology Stack:");
        LOGGER.info("   • Framework: Quarkus (Java 21)");
        LOGGER.info("   • AI Engine: Langchain4j + Google Gemini");
        LOGGER.info("   • Code Analysis: JavaParser, Tree-sitter");
        LOGGER.info("   • Diagram Format: Mermaid.js");
        LOGGER.info("   • Integration: GitLab API v4");
        LOGGER.info("========================================");
        LOGGER.info("🌐 Application running on port: {}", httpPort);
        LOGGER.info("📖 API Documentation: http://localhost:{}/q/swagger-ui", httpPort);
        LOGGER.info("❤️  Health Check: http://localhost:{}/q/health", httpPort);
        LOGGER.info("📊 Metrics: http://localhost:{}/q/metrics", httpPort);
        LOGGER.info("========================================");

        // Keep the application running
        Quarkus.waitForExit();
        return 0;
    }
}