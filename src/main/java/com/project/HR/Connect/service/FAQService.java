package com.project.HR.Connect.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.project.HR.Connect.entitie.Article;
import com.project.HR.Connect.entitie.FAQ;
import com.project.HR.Connect.repository.FAQRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class FAQService {

    private final String FAQs_FOLDER = "src/main/resources/FAQs";

    @Autowired
    private FAQRepository faqRepository;

    public List<FAQ> getAll(){
        return faqRepository.findAll();
    }

    public Pair<Boolean, String> add(FAQ faq){
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(faqRepository.save(faq));
            return Pair.of(true, json);
        }catch (DataIntegrityViolationException e){
            return Pair.of(false, "FAQ was not added because of a database error: " + e.getCause());
        } catch (JsonProcessingException e) {
            return Pair.of(false, "FAQ was not added because of a json error: " + e.getCause());
        }
    }

    public boolean delete(Integer id){
        try {
            faqRepository.deleteById(id);
        }catch (DataIntegrityViolationException e){
            return false;
        }
        return true;
    }

    public FAQ addFAQFile (MultipartFile file, Integer ID) throws IOException {
        Optional<FAQ> f = faqRepository.findById(ID);
        FAQ faq = f.get();
        String originalFilename = file.getOriginalFilename();

        if(faq.getFaqFilePath() != null) {
            File ff = new File(faq.getFaqFilePath());
            ff.delete();
        }

        assert originalFilename != null;
        String newFileName = faq.getId().toString()
                .concat(originalFilename.substring(originalFilename.lastIndexOf(".")));

        String filePath = FAQs_FOLDER + "/" + newFileName;
        faq.setFaqFilePath(filePath);
        faqRepository.save(faq);
        File fileDirectory = new File(FAQs_FOLDER);
        if(!fileDirectory.exists()) {
            fileDirectory.mkdir();
        }
        Files.copy(file.getInputStream(), Paths.get(filePath));
        return faq;
    }

    public byte[] getFaqFile(Integer ID) throws IOException {
        Optional<FAQ> f = faqRepository.findById(ID);
        FAQ faq = f.get();
        String filePath = faq.getFaqFilePath();
        byte[] files = Files.readAllBytes(new File(filePath).toPath());
        return files;
    }
}
