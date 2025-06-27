package org.ratha.virtualbookstore.controller.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Table;
import org.ratha.virtualbookstore.DTO.request.NewsRequestDTO;
import org.ratha.virtualbookstore.DTO.response.ApiResponse;
import org.ratha.virtualbookstore.DTO.response.NewsResponseDTO;
import org.ratha.virtualbookstore.service.NewsService;
import org.ratha.virtualbookstore.service.NewsService.NewsServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "News", description = "Endpoint for news operations")
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NewsResponseDTO>>> getAllNews() {
        try {
            List<NewsResponseDTO> newsList = newsService.getAllNews();
            return ResponseEntity.ok(new ApiResponse<>(200, "Successfully retrieved news list", newsList));
        } catch (NewsServiceException ex) {
            return ResponseEntity.status(500).body(new ApiResponse<>(500, ex.getMessage(), null));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(new ApiResponse<>(500, "Internal server error", null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NewsResponseDTO>> getNewsById(@PathVariable Long id) {
        try {
            NewsResponseDTO news = newsService.getNewsById(id);
            return ResponseEntity.ok(new ApiResponse<>(200, "Successfully retrieved news", news));
        } catch (NewsServiceException ex) {
            return ResponseEntity.status(404).body(new ApiResponse<>(404, ex.getMessage(), null));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(new ApiResponse<>(500, "Internal server error", null));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<List<NewsResponseDTO>>> createNews(@RequestBody List<NewsRequestDTO> newsDTOs) {
        try {
            List<NewsResponseDTO> createdNews = newsService.createMultipleNews(newsDTOs);
            return ResponseEntity.status(201).body(new ApiResponse<>(201, "News created successfully", createdNews));
        } catch (NewsServiceException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, ex.getMessage(), null));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(new ApiResponse<>(500, "Internal server error", null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NewsResponseDTO>> updateNews(@PathVariable Long id, @RequestBody NewsRequestDTO newsDTO) {
        try {
            NewsResponseDTO updatedNews = newsService.updateNews(id, newsDTO);
            return ResponseEntity.ok(new ApiResponse<>(200, "News updated successfully", updatedNews));
        } catch (NewsServiceException ex) {
            return ResponseEntity.status(404).body(new ApiResponse<>(404, ex.getMessage(), null));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(new ApiResponse<>(500, "Internal server error", null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNews(@PathVariable Long id) {
        try {
            newsService.deleteNews(id);
            return ResponseEntity.ok(new ApiResponse<>(200, "News deleted successfully", null));
        } catch (NewsServiceException ex) {
            return ResponseEntity.status(404).body(new ApiResponse<>(404, ex.getMessage(), null));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(new ApiResponse<>(500, "Internal server error", null));
        }
    }
}
