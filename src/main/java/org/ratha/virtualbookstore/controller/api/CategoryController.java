package org.ratha.virtualbookstore.controller.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.ratha.virtualbookstore.DTO.response.ApiResponse;
import org.ratha.virtualbookstore.DTO.response.CategoryResponse;
import org.ratha.virtualbookstore.service.CategoryService;
import org.ratha.virtualbookstore.service.CategoryService.CategoryServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Endpoint for category operations")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        try {
            List<CategoryResponse> newList = categoryService.getCategories();
            ApiResponse<List<CategoryResponse>> response = new ApiResponse<>(200, "Successfully retrieved categories", newList);
            return ResponseEntity.ok(response);
        } catch (CategoryServiceException e) {
            ApiResponse<List<CategoryResponse>> response = new ApiResponse<>(404, e.getMessage(), null);
            return ResponseEntity.status(404).body(response);
        } catch (Exception e) {
            ApiResponse<List<CategoryResponse>> response = new ApiResponse<>(500, "Internal server error", null);
            return ResponseEntity.status(500).body(response);
        }
    }
}
