package org.ratha.virtualbookstore.service;

import org.ratha.virtualbookstore.DTO.response.CategoryResponse;
import java.util.List;

public interface CategoryService {

    List<CategoryResponse> getCategories();

    CategoryResponse findCategoryById(Long id);

    CategoryResponse findCategoryByName(String name);

    // Service layer custom exception
    class CategoryServiceException extends RuntimeException {
        public CategoryServiceException(String message) {
            super(message);
        }
    }
}
