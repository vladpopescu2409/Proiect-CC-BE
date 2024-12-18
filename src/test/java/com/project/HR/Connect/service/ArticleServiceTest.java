package com.project.HR.Connect.service;

import com.project.HR.Connect.entitie.*;
import com.project.HR.Connect.repository.ArticleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Autowired
    ArticleService articleService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @MockBean
    ArticleRepository articleRepository;

    private List<Article> articleList = new ArrayList<>();

    private final long testTime = 1688075801962L;

    @BeforeEach
    public void setup() {
        var tempUser = new User();

        LoginDetails tempLD = new LoginDetails();
        tempLD.setId(10);
        tempLD.setEmail(String.format("user%d@email.ro", 10));
        tempLD.setPassword(passwordEncoder.encode(String.format("user%d", 10)));
        tempLD.setRole("hr");

        var tempAddr = new Address();
        tempAddr.setId(10);
        tempAddr.setCountry(String.format("country%d", 10));
        tempAddr.setCounty(String.format("county%d", 10));
        tempAddr.setCity(String.format("city%d", 10));
        tempAddr.setStreetNumber(String.format("nr%d", 10));
        tempAddr.setFlatNumber(String.format("flat%d", 10));
        tempAddr.setStreet(String.format("street%d", 10));

        var tempIC = new IdentityCard();
        tempIC.setId(10);
        tempIC.setCnp(String.valueOf((10 * 10000)));
        tempIC.setNumber(10);
        tempIC.setSeries(String.format("series%d", 10));
        tempIC.setIssuer(String.format("issuer%d", 10));
        tempIC.setIssuingDate(new Date(testTime - 100000000L * 10));

        tempUser.setId(10);
        tempUser.setFirstName(String.format("first%d", 10));
        tempUser.setLastName(String.format("last%d", 10));
        tempUser.setJoinDate(new Date(testTime - 10));
        tempUser.setPosition(Position.values()[10 % (Position.values().length - 1)]);
        tempUser.setDepartment(Department.values()[10 % (Department.values().length - 1)]);
        tempUser.setPhoneNumber(String.format("phone%d", 10));
        tempUser.setVacationDays(24);
        tempUser.setSickDays(183);
        tempUser.setAddress(tempAddr);
        tempUser.setLoginDetails(tempLD);
        tempUser.setIdentityCard(tempIC);

        for (int i = 0; i < 6; i++) {
            var tempArticle = new Article();

            tempArticle.setId(i);
            tempArticle.setCreatedDate(new Date(testTime - 100000000L * i));
            tempArticle.setContent("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
                    "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation " +
                    "ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit " +
                    "in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat " +
                    "non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
            tempArticle.setContentType("News");
            tempArticle.setTitle(String.format("title%d", i));
            tempArticle.setCreatedBy(tempUser);
        }
    }

    @Test
    void getAllArticles() {
        Mockito.when(articleRepository.findAll()).thenReturn(articleList);

        List<Article> returnedList = articleService.getAllArticles();

        Assertions.assertEquals(returnedList, articleList);
    }
}
