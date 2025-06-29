package org.ratha.virtualbookstore.controller.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.ratha.virtualbookstore.DTO.request.CategoryRequest;
import org.ratha.virtualbookstore.DTO.response.ApiResponse;
import org.ratha.virtualbookstore.DTO.response.CategoryResponse;
import org.ratha.virtualbookstore.service.CategoryService;
import org.ratha.virtualbookstore.service.CategoryService.CategoryServiceException;
import org.ratha.virtualbookstore.service.impl.CategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Endpoint for category operations")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryServiceImpl categoryServiceImpl;

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

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategory(@PathVariable Long id) {
        try {
            CategoryResponse categoryResponse = categoryService.getCategoryById(id);
            return ResponseEntity.ok(new ApiResponse<>(200, "Successfully retrieved category", categoryResponse));
        }catch (CategoryServiceException ex) {
             return  ResponseEntity.status(404).body(new ApiResponse<>(404, ex.getMessage(), null));
        }catch (Exception e) {
            return  ResponseEntity.status(500).body(new ApiResponse<>(500, e.getMessage(), null));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> createMultiCategory(
            @RequestBody List<CategoryRequest> categoryRequests) {
        try {
            if (categoryRequests == null || categoryRequests.isEmpty()) {
                ApiResponse<List<CategoryResponse>> response = new ApiResponse<>(
                        HttpStatus.BAD_REQUEST.value(), "Category creation list cannot be empty.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            List<CategoryResponse> createdCategories = categoryService.createCategory(categoryRequests);

            ApiResponse<List<CategoryResponse>> response = new ApiResponse<>(
                    HttpStatus.CREATED.value(), "Categories created successfully", createdCategories);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            ApiResponse<List<CategoryResponse>> response = new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@PathVariable Long id, @RequestBody CategoryRequest categoryRequest) {
        try {
            if (categoryRequest.getName() == null || categoryRequest.getName().isEmpty()) {
                ApiResponse<CategoryResponse> response = new ApiResponse<>(
                        HttpStatus.BAD_REQUEST.value(), "Category name cannot be null or empty", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            CategoryResponse updatedCategory = categoryService.updateCategory(id, categoryRequest);
            ApiResponse<CategoryResponse> response = new ApiResponse<>(
                    HttpStatus.OK.value(), "Category updated successfully", updatedCategory);
            return ResponseEntity.ok(response);
        } catch (CategoryServiceException e) {
            ApiResponse<CategoryResponse> response = new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            ApiResponse<CategoryResponse> response = new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> deleteCategory(@PathVariable Long id) {
        if (id <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, "Invalid category ID", null));
        }
        try {
            CategoryResponse deleted = categoryService.deleteCategory(id);
            return ResponseEntity.ok(new ApiResponse<>(200, "Category deleted successfully", deleted));
        } catch (CategoryServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, "Category not found: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Internal server error: " + e.getMessage(), null));
        }
    }
}
