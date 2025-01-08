package com.project.HR.Connect.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.project.HR.Connect.client.AuthServiceClient;
import com.project.HR.Connect.entitie.Address;
import com.project.HR.Connect.entitie.IdentityCard;
import com.project.HR.Connect.entitie.LoginDetails;
import com.project.HR.Connect.entitie.User;
import com.project.HR.Connect.repository.*;
import com.project.HR.Connect.security.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    LoginDetailsRepository loginDetailsRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    IdentityCardRepository identityCardRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    RequestRepository requestRepository;

    @Autowired
    JWTUtils jwtUtils;

//    @Autowired
//    private AuthServiceClient authServiceClient;


    public List<User> getAll(){
        return userRepository.findAll();
    }

    @Transactional(rollbackFor = Exception.class)
    public Pair<Boolean,String> add(User userIN, LoginDetails loginDetailsIN, Address addressIN, IdentityCard identityCardIN){

        if(userIN == null || loginDetailsIN == null || addressIN == null || identityCardIN == null){
            return Pair.of(false, "You user data is incomplete please complete all fields(user, loginDetails, address, identityCard, departmentName)!");
        }
        try {
            // Step 1: Save address, identity card, and set default vacation/sick days
            userIN.setAddress(addressRepository.save(addressIN));
            userIN.setIdentityCard(identityCardRepository.save(identityCardIN));
            userIN.setVacationDays(24);  // You can customize this default value
            userIN.setSickDays(183);  // You can customize this default value

            // Step 2: Send LoginDetails to auth-service and get the saved LoginDetails with an ID
            //ResponseEntity<LoginDetails> response = authServiceClient.createLoginDetails(loginDetailsIN);

            // Step 3: Set the received LoginDetails in the User entity
            userIN.setLoginDetails(loginDetailsIN);

            // Step 4: Save the User entity with the associated LoginDetails
            User user = userRepository.save(userIN);

            // Convert the saved user to a JSON string (optional step)
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(user);

            return Pair.of(true, json);
        } catch (DataIntegrityViolationException e){
            return Pair.of(false, "User was not added because of a database error." + e);
        } catch (JsonProcessingException e) {
            return Pair.of(false, "User was not added because of a json error: " + e.getMessage());
        } catch (NoSuchElementException e){
            return Pair.of(false, "User was not edited because you can not edit a nonexistent user: " + e.getMessage());
        }
    }


    @Transactional
    public Boolean delete(Integer id){
        try {
            Optional<User> u = userRepository.findById(id);
            if (u.isEmpty()){
                return false;
            }

            User user = u.get();

            LoginDetails loginDetails = user.getLoginDetails();
            Address address = user.getAddress();
            IdentityCard identityCard = user.getIdentityCard();
            requestRepository.deleteRequestsByRequesterId(user.getId());

            userRepository.delete(user);

            if (loginDetails != null) loginDetailsRepository.delete(loginDetails);
            if (address != null )addressRepository.delete(address);
            if (identityCard != null )identityCardRepository.delete(identityCard);

        }catch (DataIntegrityViolationException e){
            return false;
        }
        return true;
    }

    public Long numberOfUsers(){
        return userRepository.countUsers();
    }


    public User getUserByLoginDetailsEmail(String email) {
        return userRepository.findUserByLoginDetailsEmail(email);
    }

    public User updateSelf(String email, Map<String,String> modifiedData) {
        User self = getUserByLoginDetailsEmail(email);
        boolean updated = false;
        if (modifiedData.containsKey("phoneNumber") && modifiedData.get("phoneNumber") != null && !modifiedData.get("phoneNumber").isBlank()){
            self.setPhoneNumber(modifiedData.get("phoneNumber"));
            updated = true;
        }
        if (modifiedData.containsKey("password") && modifiedData.get("password") != null && !modifiedData.get("password").isBlank()){
            self.getLoginDetails().setPassword(passwordEncoder.encode(modifiedData.get("password")));
            updated = true;
        }
        if (updated){
            userRepository.save(self);
        }
        return userRepository.findUserByLoginDetailsEmail(email);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public String getImagePath() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail;
        if(authentication instanceof UsernamePasswordAuthenticationToken) {
            userEmail = authentication.getName();
        } else {
            throw new RuntimeException("No user");
        }
        User currentUser = userRepository.findUserByLoginDetailsEmail(userEmail);
        return currentUser.getProfileImagePath();
    }

    public String getPositionOfUser(String email) {
        User currentUser = userRepository.findUserByLoginDetailsEmail(email);
        return currentUser.getPosition().toString();
    }

//    public String authenticateUser(LoginDetails loginDetails) {
//        // Call auth-service to authenticate and get JWT
//        ResponseEntity<String> response = authServiceClient.authenticateUser(loginDetails);
//        if (response.getStatusCode().is2xxSuccessful()) {
//            return response.getBody();  // JWT token is returned here
//        } else {
//            throw new RuntimeException("Authentication failed.");
//        }
//    }
}

