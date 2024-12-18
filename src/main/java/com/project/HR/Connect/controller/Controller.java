package com.project.HR.Connect.controller;

import com.project.HR.Connect.dto.EmailDTO;
import com.project.HR.Connect.entitie.FAQ;
import com.project.HR.Connect.service.DocumentCreatorService;
import com.project.HR.Connect.service.EmailSenderService;
import com.project.HR.Connect.service.FAQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test")
public class Controller {

    @Autowired
    private FAQService faqService;
    @Autowired
    private EmailSenderService emailSenderService;
    @Autowired
    private DocumentCreatorService documentCreatorService;

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    boolean test(){
        return true;
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/faq")
    List<FAQ> getAllFAQ(){
        return faqService.getAll();
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/faq")
    void getAllFAQ(@RequestBody FAQ faq){
        faqService.add(faq);
    }

    @PostMapping("/send-simple-email")
    void sendSimpleEmail(@RequestBody EmailDTO emailDTO){
        emailSenderService.sendEmail(emailDTO);
    }

    @PostMapping("/send-email-with-attachment")
    void sendEmailWithAttachment(@RequestBody EmailDTO emailDTO){
        emailSenderService.sendEmailWithAttachment(emailDTO);
    }

    @PostMapping("/send-welcome-email")
    void sendWelcomeEmail(@RequestParam(name = "emailAddress") String emailAdress,
                          @RequestParam(name = "firstName") String firstName
                          ) {
        emailSenderService.sendWelcomeEmail(emailAdress, firstName);
    }

    @PostMapping("/create-document")
    void createDocument() throws Exception {
        documentCreatorService.createDocumentFromExampleTemplate();
    }
}
