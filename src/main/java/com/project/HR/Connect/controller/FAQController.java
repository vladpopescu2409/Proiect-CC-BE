package com.project.HR.Connect.controller;

import com.project.HR.Connect.entitie.FAQ;
import com.project.HR.Connect.service.FAQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/faq")
public class FAQController {
    @Autowired
    FAQService faqService;

    @GetMapping
    public ResponseEntity<List<FAQ>> getAllFAQ(){
        return ResponseEntity.ok(faqService.getAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('hr')")
    public ResponseEntity<String> createOrUpdateFAQ(@RequestBody FAQ faq){
        var out = faqService.add(faq);
        if (out.getFirst()){
            return ResponseEntity.ok(out.getSecond());
        }else{
            return ResponseEntity.badRequest().body(out.getSecond());
        }
    }

    @DeleteMapping
    @PreAuthorize("hasRole('hr')")
    public ResponseEntity<String> deleteFAQ(@RequestParam Integer id){
        if (faqService.delete(id)){
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/upload-file")
    @PreAuthorize("hasRole('hr')")
    public ResponseEntity<FAQ> addFaqFile(@RequestParam("file") MultipartFile file,
                                                   @RequestParam("id") Integer ID) {
        String filename = file.getOriginalFilename();
        if(filename.substring(filename.lastIndexOf(".")).contains(".pdf")) {
            FAQ faq;
            try {
                faq = faqService.addFAQFile(file, ID);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(faq);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/get-file")
    public ResponseEntity<byte[]> getFaqFile(@RequestParam("id") Integer ID) throws IOException {
        byte[] fileData = faqService.getFaqFile(ID);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(fileData);
    }
}
