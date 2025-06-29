package org.ratha.virtualbookstore.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.ratha.virtualbookstore.DTO.request.CategoryRequest;
import org.ratha.virtualbookstore.DTO.response.CategoryResponse;
import java.util.List;

public interface CategoryService {

    List<CategoryResponse> getCategories();

    CategoryResponse findCategoryById(Long id);

    CategoryResponse findCategoryByName(String name);

    @Transactional
    CategoryResponse getCategoryById(Long id) throws CategoryServiceException;

    List<CategoryResponse> createCategory(List<CategoryRequest> categoryRequests);

    CategoryResponse updateCategory(Long id, @Valid CategoryRequest categoryRequest) throws CategoryServiceException;

    CategoryResponse deleteCategory(Long id);


    // Service layer custom exception
    class CategoryServiceException extends RuntimeException {
        public CategoryServiceException(String message) {
            super(message);
        }
    }
}
