package org.ratha.virtualbookstore.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.ratha.virtualbookstore.DTO.request.NewsRequestDTO;
import org.ratha.virtualbookstore.DTO.response.NewsResponseDTO;
import org.ratha.virtualbookstore.model.Category;
import org.ratha.virtualbookstore.model.News;
import org.ratha.virtualbookstore.repository.CategoryRepository;
import org.ratha.virtualbookstore.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsService {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // Custom exception for service layer
    public static class NewsServiceException extends RuntimeException {
        public NewsServiceException(String message) {
            super(message);
        }
    }

    public List<NewsResponseDTO> getAllNews() {
        try {
            return newsRepository.findAll().stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new NewsServiceException("Failed to retrieve news articles: " + e.getMessage());
        }
    }

    public NewsResponseDTO getNewsById(Long id) {
        try {
            News news = newsRepository.findById(id)
                    .orElseThrow(() -> new NewsServiceException("News article with ID " + id + " not found"));
            return convertToResponseDTO(news);
        } catch (Exception e) {
            throw new NewsServiceException("Error retrieving news article with ID " + id + ": " + e.getMessage());
        }
    }

    @Transactional
    public List<NewsResponseDTO> createMultipleNews(@Valid List<NewsRequestDTO> newsDTOs) {
        if (newsDTOs == null || newsDTOs.isEmpty()) {
            throw new NewsServiceException("News request list cannot be null or empty");
        }

        List<News> newsList = new ArrayList<>();
        for (NewsRequestDTO newsDTO : newsDTOs) {
            // Validate title and content
            if (newsDTO.getTitle() == null || newsDTO.getTitle().trim().isEmpty()) {
                throw new NewsServiceException("Title is required for all news articles");
            }
            if (newsDTO.getContent() == null || newsDTO.getContent().trim().isEmpty()) {
                throw new NewsServiceException("Content is required for all news articles");
            }

            // Check for duplicate title (case-insensitive)
            boolean exists = newsRepository.existsByTitleIgnoreCase(newsDTO.getTitle().trim().toLowerCase());
            if (exists) {
                throw new NewsServiceException("A news article with the title '" + newsDTO.getTitle().trim() + "' already exists");
            }

            News news = new News();
            news.setTitle(newsDTO.getTitle().trim());
            news.setContent(newsDTO.getContent().trim());
            news.setPublishedDate(LocalDateTime.now());

            // Handle category
            if (newsDTO.getCategoryId() != null) {
                Category category = categoryRepository.findById(newsDTO.getCategoryId())
                        .orElseThrow(() -> new NewsServiceException("Category with ID " + newsDTO.getCategoryId() + " not found"));
                news.setCategory(category);
            }

            newsList.add(news);
        }

        try {
            List<News> savedNews = newsRepository.saveAll(newsList);
            return savedNews.stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new NewsServiceException("Failed to create news articles: " + e.getMessage());
        }
    }

    @Transactional
    public NewsResponseDTO updateNews(Long id, @Valid NewsRequestDTO newsDTO) {
        try {
            if (newsDTO.getTitle() == null || newsDTO.getTitle().trim().isEmpty() ||
                    newsDTO.getContent() == null || newsDTO.getContent().trim().isEmpty()) {
                throw new NewsServiceException("Title and content are required");
            }

            News news = newsRepository.findById(id)
                    .orElseThrow(() -> new NewsServiceException("News article with ID " + id + " not found"));

            // Check for duplicate title (case-insensitive), excluding the current news article
            if (!news.getTitle().equalsIgnoreCase(newsDTO.getTitle().trim()) &&
                    newsRepository.existsByTitleIgnoreCase(newsDTO.getTitle().trim().toLowerCase())) {
                throw new NewsServiceException("A news article with the title '" + newsDTO.getTitle().trim() + "' already exists");
            }

            news.setTitle(newsDTO.getTitle().trim());
            news.setContent(newsDTO.getContent().trim());

            // Handle category
            if (newsDTO.getCategoryId() != null) {
                Category category = categoryRepository.findById(newsDTO.getCategoryId())
                        .orElseThrow(() -> new NewsServiceException("Category with ID " + newsDTO.getCategoryId() + " not found"));
                news.setCategory(category);
            } else {
                news.setCategory(null); // Allow removing category
            }

            News updatedNews = newsRepository.save(news);
            return convertToResponseDTO(updatedNews);
        } catch (DataAccessException e) {
            throw new NewsServiceException("Database error updating news article with ID " + id + ": " + e.getMessage());
        }
    }

    public void deleteNews(Long id) {
        try {
            if (!newsRepository.existsById(id)) {
                throw new NewsServiceException("News article with ID " + id + " not found");
            }
            newsRepository.deleteById(id);
        } catch (Exception e) {
            throw new NewsServiceException("Error deleting news article with ID " + id + ": " + e.getMessage());
        }
    }

    private NewsResponseDTO convertToResponseDTO(News news) {
        NewsResponseDTO dto = new NewsResponseDTO();
        dto.setId(news.getId());
        dto.setTitle(news.getTitle());
        dto.setContent(news.getContent());
        dto.setPublishedDate(news.getPublishedDate());

        if (news.getCategory() != null) {
            dto.setCategoryId(news.getCategory().getId());
            dto.setCategoryName(news.getCategory().getName());
        } else {
            dto.setCategoryId(null);
            dto.setCategoryName(null);
        }

        return dto;
    }
}
