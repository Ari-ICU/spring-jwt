package org.ratha.virtualbookstore.exception;

//import org.ratha.virtualbookstore.DTO.response.ApiResponse;
//import org.ratha.virtualbookstore.service.NewsService.NewsServiceException;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(NewsServiceException.class)
//    public ResponseEntity<ApiResponse<Object>> handleNewsServiceException(NewsServiceException ex) {
//        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex) {
//        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error", null);
//        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//}
