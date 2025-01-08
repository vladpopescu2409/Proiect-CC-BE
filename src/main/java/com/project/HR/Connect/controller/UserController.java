package com.project.HR.Connect.controller;

import com.project.HR.Connect.dto.UserDTO;
import com.project.HR.Connect.entitie.User;
import com.project.HR.Connect.security.JWTUtils;
import com.project.HR.Connect.service.EmailSenderService;
import com.project.HR.Connect.service.ImageUploadService;
import com.project.HR.Connect.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final String FOLDER_PATH = "src/main/resources/images";

    @Autowired
    ImageUploadService imageUploadService;

    @Autowired
    UserService userService;

    @Autowired
    JWTUtils jwtUtils;

    @Autowired
    EmailSenderService emailSenderService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<User>> getAllUser(){
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("/self")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<User> getSelfUser(@RequestHeader("Authorization") String authorizationHeader){
        String email = jwtUtils.getEmailFromJwtToken(jwtUtils.normalizeAuthorizationHeader(authorizationHeader));
        User user = userService.getUserByLoginDetailsEmail(email);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/self")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<User> updateSelf(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Map<String, String> modifiedData ){
        String email = jwtUtils.getEmailFromJwtToken(jwtUtils.normalizeAuthorizationHeader(authorizationHeader));
        return ResponseEntity.ok(userService.updateSelf(email, modifiedData));
    }

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<User> getUserByEmail(@RequestParam String email){
        return ResponseEntity.ok(userService.getUserByLoginDetailsEmail(email));
    }

    @PutMapping
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> addUser(@RequestBody UserDTO data){
        User user = data.getUser();
        var loginDetails = data.getLoginDetails();
        var address =  data.getAddress();
        var identityCard = data.getIdentityCard();

        Pair<Boolean, String> out;
        try {
            out = userService.add(user, loginDetails, address, identityCard);
            emailSenderService.sendWelcomeEmail(loginDetails.getEmail(), user.getFirstName());
        }catch (DataIntegrityViolationException e){
            return ResponseEntity.badRequest().body("User insertion failed due to a database error" + e);
        }
        if (out.getFirst()){
            return ResponseEntity.ok(user);
        }else{
            return ResponseEntity.badRequest().body(out.getSecond());
        }
    }

    @DeleteMapping
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> deleteUser(@RequestParam Integer id){
        if(userService.delete(id)){
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/path")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    public String getProfileImagePath() {
        return userService.getImagePath();
    }

    @PostMapping(value = "/upload-image", consumes = "multipart/form-data")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<User> uploadProfileImage(@RequestParam("image") MultipartFile image) {
        String imageName = image.getOriginalFilename();
        if(imageName.substring(imageName.lastIndexOf(".")).contains(".png") ||
                imageName.substring(imageName.lastIndexOf(".")).contains(".jpg") ||
                imageName.substring(imageName.lastIndexOf(".")).contains(".jpeg")) {

            User currentUser;
            try {
                currentUser = imageUploadService.addProfileImage(FOLDER_PATH, image);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(currentUser);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/get-image")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<byte[]> getProfileImageOfUser() throws IOException {
        byte[] imageData = imageUploadService.getImageOfUser();
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(imageData);
    }

    @GetMapping("/position")
    @Operation(summary = "My endpoint", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getPositionOfUser(@RequestHeader("Authorization") String authorizationHeader) {
        String email = jwtUtils.getEmailFromJwtToken(jwtUtils.normalizeAuthorizationHeader(authorizationHeader));
        return ResponseEntity.ok(userService.getPositionOfUser(email));
    }

}

