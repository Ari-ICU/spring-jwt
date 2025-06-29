package org.ratha.virtualbookstore.service.impl;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.ratha.virtualbookstore.DTO.request.CategoryRequest;
import org.ratha.virtualbookstore.DTO.response.CategoryResponse;
import org.ratha.virtualbookstore.model.Category;
import org.ratha.virtualbookstore.repository.CategoryRepository;
import org.ratha.virtualbookstore.repository.NewsRepository;
import org.ratha.virtualbookstore.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private final NewsRepository newsRepository;

    public CategoryServiceImpl(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @Override
    public List<CategoryResponse> getCategories() throws CategoryServiceException {
        try {
            List<Category> categories = categoryRepository.findAll();
            return categories.stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CategoryServiceException("Failed to retrieve categories: " + e.getMessage());
        }
    }

    @Override
    public CategoryResponse findCategoryById(Long id) throws CategoryServiceException {
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new CategoryServiceException("Category not found with ID: " + id));
            return convertToResponseDTO(category);
        } catch (Exception e) {
            throw new CategoryServiceException("Failed to find category: " + e.getMessage());
        }
    }

    @Override
    public CategoryResponse findCategoryByName(String name) throws CategoryServiceException {
        try {
            Category category = categoryRepository.findByName(name)
                    .orElseThrow(() -> new CategoryServiceException("Category not found with name: " + name));
            return convertToResponseDTO(category);
        } catch (Exception e) {
            throw new CategoryServiceException("Failed to find category: " + e.getMessage());
        }
    }

    @Transactional
    @Override
    public CategoryResponse getCategoryById(Long id) throws CategoryServiceException {
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new CategoryServiceException("Category not found with ID: " + id));
            return convertToResponseDTO(category);
        }catch (Exception e) {
            throw new CategoryServiceException("Failed to retrieve category: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public List<CategoryResponse> createCategory(List<CategoryRequest> categoryRequests) throws CategoryServiceException {
        if (categoryRequests == null || categoryRequests.isEmpty()) {
            throw new CategoryServiceException("Category requests cannot be null or empty");
        }

        List<Category> createdCategories = new ArrayList<>();

        for (CategoryRequest request : categoryRequests) {
            String name = request.getName();
            if (name == null || name.trim().isEmpty()) {
                continue; // Skip invalid names
            }

            // Check if category with this name already exists
            boolean exists = categoryRepository.existsByName(name);
            if (exists) {
                // Skip adding this category, it already exists
                continue;
            }

            // Create and save new category
            Category category = new Category();
            category.setName(name);
            createdCategories.add(categoryRepository.save(category));
        }

        return createdCategories.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }


    @Override
    public CategoryResponse updateCategory(Long id, @Valid CategoryRequest categoryRequest) throws CategoryServiceException {
        if (categoryRequest.getName() == null || categoryRequest.getName().isEmpty()) {
            throw new CategoryServiceException("Category name cannot be null or empty");
        }

        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryServiceException("Category not found with ID: " + id));

        existingCategory.setName(categoryRequest.getName());
        // Update other fields as needed

        try {
            Category updatedCategory = categoryRepository.save(existingCategory);
            return convertToResponseDTO(updatedCategory);
        } catch (Exception e) {
            throw new CategoryServiceException("Failed to update category: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public CategoryResponse deleteCategory(Long id) {
        // Find the category by ID
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryServiceException("Category with ID " + id + " not found"));

        // Check for dependent records in the news table
        if (newsRepository.existsByCategoryId(id)) {
            throw new CategoryServiceException("Cannot delete category with ID " + id + " because it is referenced by news records");
        }

        // Convert to CategoryResponse before deleting
        CategoryResponse response = new CategoryResponse(category.getId(), category.getName());

        // Delete the category
        categoryRepository.deleteById(id);

        return response;
    }

    private CategoryResponse convertToResponseDTO(Category category) {
        CategoryResponse dto = new CategoryResponse();
        dto.setId(category.getId());
        dto.setName(category.getName());
        // Set other fields as needed
        return dto;
    }
}