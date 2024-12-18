package com.project.HR.Connect.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.project.HR.Connect.entitie.Article;
import com.project.HR.Connect.entitie.User;
import com.project.HR.Connect.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {

    private final String ARTICLE_IMAGES_FOLDER = "src/main/resources/article-images";

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    UserService userService;

    public Pair<Boolean,String> addNewArticle(Article articleIN) {
        if(articleIN == null) {
            return Pair.of(false, "You article data is incomplete!");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail;
        if(authentication instanceof UsernamePasswordAuthenticationToken) {
            userEmail = authentication.getName();
        } else {
            throw new RuntimeException("No user");
        }
        User currentUser = userService.getUserByLoginDetailsEmail(userEmail);

        try {
            articleIN.setCreatedBy(currentUser);
            Article newArticle = articleRepository.save(articleIN);

            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(newArticle);
            return Pair.of(true, json);
        } catch (JsonProcessingException e) {
            return Pair.of(false, "Article was not added because of a json error: " + e.getMessage());
        }

    }

    public Article addArticleImage(MultipartFile image, Integer ID) throws IOException {
        Optional<Article> art = articleRepository.findById(ID);
        Article article = art.get();
        String originalImageName = image.getOriginalFilename();

        if(article.getCoverImagePath() != null) {
            File f = new File(article.getCoverImagePath());
            f.delete();
        }

        assert originalImageName != null;
        String newImageName = article.getId().toString()
                .concat(originalImageName.substring(originalImageName.lastIndexOf(".")));

        String imagePath = ARTICLE_IMAGES_FOLDER + "/" + newImageName;
        article.setCoverImagePath(imagePath);
        articleRepository.save(article);
        File imageDirectory = new File(ARTICLE_IMAGES_FOLDER);
        if(!imageDirectory.exists()) {
            imageDirectory.mkdir();
        }
        Files.copy(image.getInputStream(), Paths.get(imagePath));
        return article;
    }

    public byte[] getImageOfArticle(Integer ID) throws IOException {
        Optional<Article> art = articleRepository.findById(ID);
        Article article = art.get();
        String imagePath = article.getCoverImagePath();
        byte[] images = Files.readAllBytes(new File(imagePath).toPath());
        return images;
    }

    public boolean deleteArticle(Integer ID) {
        try {
            Optional<Article> art = articleRepository.findById(ID);
            if (art.isEmpty()) {
                return false;
            }

            Article article = art.get();
            if(article.getCoverImagePath() != null) {
                File file = new File(article.getCoverImagePath());
                file.delete();
            }
            articleRepository.delete(article);
        } catch (DataIntegrityViolationException e) {
            return false;
        }
        return true;
    }

    public Optional<Article> getArticleById(Integer ID) {
        return articleRepository.findById(ID);
    }

    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }
}
