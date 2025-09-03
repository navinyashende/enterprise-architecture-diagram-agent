package com.enterprise.arch.agent.web;

import com.enterprise.arch.agent.model.AnalysisRequest;
import com.enterprise.arch.agent.model.AnalysisResult;
import com.enterprise.arch.agent.model.DiagramGenerationRequest;
import com.enterprise.arch.agent.model.DiagramResult;
import com.enterprise.arch.agent.service.ArchitectureAnalysisService;
import com.enterprise.arch.agent.service.DiagramGenerationService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * REST API endpoints for architecture analysis and diagram generation.
 * 
 * This resource provides endpoints for:
 * - Analyzing repository architecture
 * - Generating Mermaid diagrams
 * - Managing analysis jobs
 * - Retrieving analysis history
 * 
 * @author Enterprise Architecture Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Path("/api/v1/architecture")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Architecture Analysis", description = "Architecture analysis and diagram generation operations")
public class ArchitectureAnalysisResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArchitectureAnalysisResource.class);

    @Inject
    ArchitectureAnalysisService analysisService;

    @Inject
    DiagramGenerationService diagramService;

    @POST
    @Path("/analyze")
    @Operation(
        summary = "Analyze repository architecture",
        description = "Analyzes a GitLab repository to extract architectural patterns and components"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "202",
            description = "Analysis started successfully",
            content = @Content(schema = @Schema(implementation = AnalysisResult.class))
        ),
        @APIResponse(
            responseCode = "400",
            description = "Invalid request parameters"
        ),
        @APIResponse(
            responseCode = "401",
            description = "Unauthorized - invalid GitLab token"
        ),
        @APIResponse(
            responseCode = "404",
            description = "Repository not found"
        ),
        @APIResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    @Counted(name = "analysis_requests_total", description = "Total number of analysis requests")
    @Timed(name = "analysis_duration", description = "Analysis duration")
    public CompletionStage<Response> analyzeRepository(
            @Valid AnalysisRequest request,
            @HeaderParam("Authorization") String authHeader) {
        
        LOGGER.info("Starting architecture analysis for project: {}", request.getProjectId());
        
        return analysisService.analyzeRepositoryAsync(request, authHeader)
            .thenApply(result -> {
                LOGGER.info("Analysis completed for project: {} with ID: {}", 
                    request.getProjectId(), result.getAnalysisId());
                return Response.accepted(result).build();
            })
            .exceptionally(throwable -> {
                LOGGER.error("Analysis failed for project: {}", request.getProjectId(), throwable);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Analysis failed: " + throwable.getMessage()))
                    .build();
            });
    }

    @POST
    @Path("/diagrams/generate")
    @Operation(
        summary = "Generate architecture diagrams",
        description = "Generates Mermaid diagrams based on repository analysis results"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Diagrams generated successfully",
            content = @Content(schema = @Schema(implementation = DiagramResult.class))
        ),
        @APIResponse(
            responseCode = "400",
            description = "Invalid request parameters"
        ),
        @APIResponse(
            responseCode = "404",
            description = "Analysis not found"
        ),
        @APIResponse(
            responseCode = "500",
            description = "Diagram generation failed"
        )
    })
    @Counted(name = "diagram_generation_requests_total", description = "Total number of diagram generation requests")
    @Timed(name = "diagram_generation_duration", description = "Diagram generation duration")
    public CompletionStage<Response> generateDiagrams(
            @Valid DiagramGenerationRequest request,
            @HeaderParam("Authorization") String authHeader) {
        
        LOGGER.info("Starting diagram generation for analysis: {}", request.getAnalysisId());
        
        return diagramService.generateDiagramsAsync(request, authHeader)
            .thenApply(result -> {
                LOGGER.info("Diagram generation completed for analysis: {} with {} diagrams", 
                    request.getAnalysisId(), result.getDiagrams().size());
                return Response.ok(result).build();
            })
            .exceptionally(throwable -> {
                LOGGER.error("Diagram generation failed for analysis: {}", request.getAnalysisId(), throwable);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Diagram generation failed: " + throwable.getMessage()))
                    .build();
            });
    }

    @GET
    @Path("/analysis/{analysisId}")
    @Operation(
        summary = "Get analysis result",
        description = "Retrieves the result of a previous architecture analysis"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Analysis result retrieved successfully",
            content = @Content(schema = @Schema(implementation = AnalysisResult.class))
        ),
        @APIResponse(
            responseCode = "404",
            description = "Analysis not found"
        )
    })
    public Response getAnalysisResult(
            @Parameter(description = "Analysis ID", required = true)
            @PathParam("analysisId") String analysisId) {
        
        LOGGER.debug("Retrieving analysis result for ID: {}", analysisId);
        
        return analysisService.getAnalysisResult(analysisId)
            .map(result -> Response.ok(result).build())
            .orElse(Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("Analysis not found: " + analysisId))
                .build());
    }

    @GET
    @Path("/diagrams/{analysisId}")
    @Operation(
        summary = "Get generated diagrams",
        description = "Retrieves the diagrams generated for a specific analysis"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Diagrams retrieved successfully",
            content = @Content(schema = @Schema(implementation = DiagramResult.class))
        ),
        @APIResponse(
            responseCode = "404",
            description = "Diagrams not found"
        )
    })
    public Response getDiagrams(
            @Parameter(description = "Analysis ID", required = true)
            @PathParam("analysisId") String analysisId) {
        
        LOGGER.debug("Retrieving diagrams for analysis ID: {}", analysisId);
        
        return diagramService.getDiagramResult(analysisId)
            .map(result -> Response.ok(result).build())
            .orElse(Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("Diagrams not found for analysis: " + analysisId))
                .build());
    }

    @DELETE
    @Path("/analysis/{analysisId}")
    @Operation(
        summary = "Delete analysis result",
        description = "Deletes an analysis result and its associated diagrams"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "204",
            description = "Analysis deleted successfully"
        ),
        @APIResponse(
            responseCode = "404",
            description = "Analysis not found"
        )
    })
    public Response deleteAnalysis(
            @Parameter(description = "Analysis ID", required = true)
            @PathParam("analysisId") String analysisId) {
        
        LOGGER.info("Deleting analysis: {}", analysisId);
        
        boolean deleted = analysisService.deleteAnalysis(analysisId);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("Analysis not found: " + analysisId))
                .build();
        }
    }

    @GET
    @Path("/health")
    @Operation(
        summary = "Health check",
        description = "Checks the health of the architecture analysis service"
    )
    @APIResponse(
        responseCode = "200",
        description = "Service is healthy"
    )
    public Response health() {
        return Response.ok(new HealthResponse("Architecture Analysis Service is healthy")).build();
    }

    /**
     * Error response model
     */
    public static class ErrorResponse {
        private final String error;
        private final long timestamp;

        public ErrorResponse(String error) {
            this.error = error;
            this.timestamp = System.currentTimeMillis();
        }

        public String getError() { return error; }
        public long getTimestamp() { return timestamp; }
    }

    /**
     * Health response model
     */
    public static class HealthResponse {
        private final String status;
        private final long timestamp;

        public HealthResponse(String status) {
            this.status = status;
            this.timestamp = System.currentTimeMillis();
        }

        public String getStatus() { return status; }
        public long getTimestamp() { return timestamp; }
    }
}