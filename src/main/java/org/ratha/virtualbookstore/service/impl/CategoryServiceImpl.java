package org.ratha.virtualbookstore.service.impl;

import org.ratha.virtualbookstore.DTO.response.CategoryResponse;
import org.ratha.virtualbookstore.model.Category;
import org.ratha.virtualbookstore.repository.CategoryRepository;
import org.ratha.virtualbookstore.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponse> getCategories() {
        try {
            List<Category> categories = categoryRepository.findAll();
            return categories.stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new CategoryServiceException("Failed to retrieve categories: " + ex.getMessage());
        }
    }

    @Override
    public CategoryResponse findCategoryById(Long id) {
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new CategoryServiceException("Category with ID " + id + " not found"));
            return convertToResponseDTO(category);
        } catch (Exception ex) {
            throw new CategoryServiceException("Error retrieving category with ID " + id + ": " + ex.getMessage());
        }
    }

    @Override
    public CategoryResponse findCategoryByName(String name) {
        try {
            Category category = categoryRepository.findByName(name)
                    .orElseThrow(() -> new CategoryServiceException("Category with name '" + name + "' not found"));
            return convertToResponseDTO(category);
        } catch (Exception ex) {
            throw new CategoryServiceException("Error retrieving category by name: " + ex.getMessage());
        }
    }

    private CategoryResponse convertToResponseDTO(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        return response;
    }
}
