package com.project.HR.Connect.service;

import com.project.HR.Connect.entitie.User;
import com.project.HR.Connect.repository.UserRepository;
import com.project.HR.Connect.security.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class ImageUploadService {

    @Autowired
    JWTUtils jwtUtils;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    public User addProfileImage(String path, MultipartFile file) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail;
        if(authentication instanceof UsernamePasswordAuthenticationToken) {
            userEmail = authentication.getName();
        } else {
            throw new RuntimeException("No user");
        }

        User currentUser = userService.getUserByLoginDetailsEmail(userEmail);
        if(currentUser.getProfileImagePath() != null) {
            File f = new File(currentUser.getProfileImagePath());
            f.delete();
        }

        String originalImageName = file.getOriginalFilename();

        assert originalImageName != null;
        String newImageName = currentUser.getId().toString()
                .concat(originalImageName.substring(originalImageName.lastIndexOf(".")));

        String imagePath = path + "/" + newImageName;
        File imageDirectory = new File(path);
        if(!imageDirectory.exists()) {
            imageDirectory.mkdir();
        }
        Files.copy(file.getInputStream(), Paths.get(imagePath));

        currentUser.setProfileImagePath(imagePath);
        userService.updateUser(currentUser);

        return currentUser;
    }

    public byte[] getImageOfUser() throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail;
        if(authentication instanceof UsernamePasswordAuthenticationToken) {
            userEmail = authentication.getName();
        } else {
            throw new RuntimeException("No user");
        }

        User currentUser = userService.getUserByLoginDetailsEmail(userEmail);

        String imagePath = currentUser.getProfileImagePath();
        System.out.println(imagePath);
        byte[] images = Files.readAllBytes(new File(imagePath).toPath());
        return images;
    }
}
