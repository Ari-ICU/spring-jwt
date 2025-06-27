package org.ratha.virtualbookstore.service;

import org.ratha.virtualbookstore.DTO.request.NewsRequestDTO;
import org.ratha.virtualbookstore.DTO.response.NewsResponseDTO;
import org.ratha.virtualbookstore.model.Category;
import org.ratha.virtualbookstore.model.News;
import org.ratha.virtualbookstore.repository.CategoryRepository;
import org.ratha.virtualbookstore.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<NewsResponseDTO> createMultipleNews(List<NewsRequestDTO> newsDTOs) {
        if (newsDTOs == null || newsDTOs.isEmpty()) {
            throw new NewsServiceException("News request list cannot be null or empty");
        }

        List<NewsResponseDTO> savedNewsList = new ArrayList<>();

        for (NewsRequestDTO newsDTO : newsDTOs) {
            // Validate title and content
            if (newsDTO.getTitle() == null || newsDTO.getTitle().trim().isEmpty()) {
                throw new NewsServiceException("Title is required for all news articles");
            }
            if (newsDTO.getContent() == null || newsDTO.getContent().trim().isEmpty()) {
                throw new NewsServiceException("Content is required for all news articles");
            }

            boolean exists = newsRepository.existsByTitle(newsDTO.getTitle().trim());
            if (exists) {
                throw new NewsServiceException("A news article with the title '" + newsDTO.getTitle().trim() + "' already exists");
            }
            News news = new News();
            news.setTitle(newsDTO.getTitle().trim());
            news.setContent(newsDTO.getContent().trim());
            news.setPublishedDate(LocalDateTime.now());

            // Save the news entity
            News savedNews = newsRepository.save(news);
            savedNewsList.add(convertToResponseDTO(savedNews));
        }

        return savedNewsList;
    }


    public NewsResponseDTO updateNews(Long id, NewsRequestDTO newsDTO) {
        try {
            if (newsDTO.getTitle() == null || newsDTO.getContent() == null) {
                throw new NewsServiceException("Title and content are required");
            }

            News news = newsRepository.findById(id)
                    .orElseThrow(() -> new NewsServiceException("News article with ID " + id + " not found"));

            news.setTitle(newsDTO.getTitle());
            news.setContent(newsDTO.getContent());

            News updatedNews = newsRepository.save(news);
            return convertToResponseDTO(updatedNews);
        } catch (Exception e) {
            throw new NewsServiceException("Error updating news article with ID " + id + ": " + e.getMessage());
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
