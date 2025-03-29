package com.backend.controller;

import com.backend.controller.request.CategoryRequest; // Dùng chung cho create/update
import com.backend.controller.response.CategoryResponse;
import com.backend.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/categories") // Base path cho category
@Tag(name = "Category API v1")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Create New Category", description = "Add a new category.")
    @ApiResponse(responseCode = "201", description = "Category created successfully",
            content = @Content(schema = @Schema(implementation = CategoryResponse.class)))
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request) {
        log.info("Request received to create category: {}", request.getName());
        CategoryResponse createdCategory = categoryService.createCategory(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdCategory.getId())
                .toUri();
        log.info("Category created successfully with ID: {}", createdCategory.getId());
        return ResponseEntity.created(location).body(createdCategory);
    }

    @Operation(summary = "Get Category by ID", description = "Retrieve information for a specific category.")
    @ApiResponse(responseCode = "200", description = "Category found",
            content = @Content(schema = @Schema(implementation = CategoryResponse.class)))
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @PathVariable @Min(value = 1, message = "Category ID must be positive") Long categoryId) {
        log.info("Request received to get category detail for ID: {}", categoryId);
        CategoryResponse category = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "Get All Categories", description = "Retrieve a list of categories.")
    @ApiResponse(responseCode = "200", description = "List of categories retrieved",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryResponse.class))))
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Request received to get all categories, page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        List<CategoryResponse> categories = categoryService.getAllCategories(pageable);
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Update Category Information", description = "Update details for an existing category.")
    @ApiResponse(responseCode = "200", description = "Category updated successfully",
            content = @Content(schema = @Schema(implementation = CategoryResponse.class)))
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable @Min(value = 1, message = "Category ID must be positive") Long categoryId,
            @Valid @RequestBody CategoryRequest request) {
        log.info("Request received to update category ID: {}", categoryId);
        CategoryResponse updatedCategory = categoryService.updateCategory(categoryId, request);
        return ResponseEntity.ok(updatedCategory);
    }

    @Operation(summary = "Delete Category", description = "Remove a category.")
    @ApiResponse(responseCode = "204", description = "Category deleted successfully", content = @Content)
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable @Min(value = 1, message = "Category ID must be positive") Long categoryId) {
        log.info("Request received to delete category ID: {}", categoryId);
        categoryService.deleteCategory(categoryId);
        log.info("Category deleted successfully with ID: {}", categoryId);
        return ResponseEntity.noContent().build();
    }
}