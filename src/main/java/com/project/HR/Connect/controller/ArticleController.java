package com.project.HR.Connect.controller;

import com.project.HR.Connect.entitie.Article;
import com.project.HR.Connect.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/article")
@CrossOrigin(origins = "http://localhost:4200")
public class ArticleController {

    @Autowired
    ArticleService articleService;

    @PostMapping
    @PreAuthorize("hasRole('hr')")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> addArticle(@RequestBody Article article) {
        var out = articleService.addNewArticle(article);
        if (out.getFirst()) {
            return ResponseEntity.ok(out.getSecond());
        } else {
            return ResponseEntity.badRequest().body(out.getSecond());
        }
    }

    @DeleteMapping
    @PreAuthorize("hasRole('hr')")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> deleteArticle(@RequestParam Integer id) {
        if(articleService.deleteArticle(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/upload-image", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('hr')")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Article> addArticleImage(@RequestParam("image") MultipartFile image,
                                             @RequestParam("id") Integer ID) {
        String imageName = image.getOriginalFilename();
        if(imageName.substring(imageName.lastIndexOf(".")).contains(".png") ||
                imageName.substring(imageName.lastIndexOf(".")).contains(".jpg") ||
                imageName.substring(imageName.lastIndexOf(".")).contains(".jpeg")) {
            Article article;
            try {
                article = articleService.addArticleImage(image, ID);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(article);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/all")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<Article>> getAllArticles() {
        return ResponseEntity.ok(articleService.getAllArticles());
    }

    @GetMapping("/get-article")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Optional<Article>> getArticleById(@RequestParam("id") Integer ID) {
        return ResponseEntity.ok(articleService.getArticleById(ID));
    }

    @GetMapping("/by")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getArticleAuthor(@RequestParam("id") Integer id) {
        Optional<Article> art = articleService.getArticleById(id);
        Article article = art.get();
        return ResponseEntity.ok(article.getCreatedBy().getFirstName() + " " + article.getCreatedBy().getLastName());
    }

    @GetMapping("/author-job")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getArticleAuthorJob(@RequestParam("id") Integer id) {
        Optional<Article> art = articleService.getArticleById(id);
        Article article = art.get();
        return ResponseEntity.ok(article.getCreatedBy().getPosition());
    }

    @GetMapping("/get-image")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<byte[]> getArticleImage(@RequestParam("id") Integer ID) throws IOException {
        byte[] imageData = articleService.getImageOfArticle(ID);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(imageData);
    }
}
