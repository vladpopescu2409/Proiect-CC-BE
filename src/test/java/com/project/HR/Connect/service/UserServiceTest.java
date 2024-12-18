package com.project.HR.Connect.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.project.HR.Connect.dto.UserDTO;
import com.project.HR.Connect.entitie.*;
import com.project.HR.Connect.repository.*;
import com.project.HR.Connect.security.JWTUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Autowired
    private UserService testedService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    JWTUtils jwtUtils;

    @MockBean
    UserRepository userRepository;

    @MockBean
    LoginDetailsRepository loginDetailsRepository;

    @MockBean
    AddressRepository addressRepository;

    @MockBean
    IdentityCardRepository identityCardRepository;

    @MockBean
    RequestRepository requestRepository;


    private final List<User> userArrayList = new ArrayList<>();

    private final long testTime = 1688075801962L;

    @BeforeEach
    void setup(){
        for (int i =0; i < 11; i++){
            var tempUser = new User();

            var tempLD = new LoginDetails();
            tempLD.setId(i);
            tempLD.setEmail(String.format("user%d@email.ro", i));
            tempLD.setPassword(passwordEncoder.encode(String.format("user%d", i)));
            if (i % 5 == 0){
                tempLD.setRole("admin");
            } else if (i % 3 == 0){
                tempLD.setRole("gr");
            }else {
                tempLD.setRole("employee");
            }

            var tempAddr = new Address();
            tempAddr.setId(i);
            tempAddr.setCountry(String.format("country%d", i));
            tempAddr.setCounty(String.format("county%d", i));
            tempAddr.setCity(String.format("city%d", i));
            tempAddr.setStreetNumber(String.format("nr%d", i));
            tempAddr.setFlatNumber(String.format("flat%d", i));
            tempAddr.setStreet(String.format("street%d", i));

            var tempIC = new IdentityCard();
            tempIC.setId(i);
            tempIC.setCnp(String.valueOf((i * 10000)));
            tempIC.setNumber(i);
            tempIC.setSeries(String.format("series%d", i));
            tempIC.setIssuer(String.format("issuer%d", i));
            tempIC.setIssuingDate(new Date(testTime - 100000000L * i));

            tempUser.setId(i);
            tempUser.setFirstName(String.format("first%d", i));
            tempUser.setLastName(String.format("last%d", i));
            tempUser.setJoinDate(new Date(testTime - i));
            tempUser.setPosition(Position.values()[i % (Position.values().length - 1)]);
            tempUser.setDepartment(Department.values()[i % (Department.values().length - 1)]);
            tempUser.setPhoneNumber(String.format("phone%d", i));
            tempUser.setVacationDays(24);
            tempUser.setSickDays(183);
            tempUser.setAddress(tempAddr);
            tempUser.setLoginDetails(tempLD);
            tempUser.setIdentityCard(tempIC);

            userArrayList.add(tempUser);
        }
    }

    @Test
    void getAll() {
        Mockito.when(userRepository.findAll()).thenReturn(userArrayList);

        List<User> returnedValue = testedService.getAll();

        Assertions.assertEquals(returnedValue, userArrayList);
    }

    @Test
    void add() throws JsonProcessingException {
        Mockito.when(loginDetailsRepository.findById(userArrayList.get(0).getLoginDetails().getId()))
                        .thenReturn(Optional.ofNullable(userArrayList.get(0).getLoginDetails()));

        var testUserDTO = new UserDTO();
        testUserDTO.setUser(userArrayList.get(0));
        testUserDTO.setLoginDetails(userArrayList.get(0).getLoginDetails());
        testUserDTO.getLoginDetails().setPassword(null);
        testUserDTO.setAddress(userArrayList.get(0).getAddress());
        testUserDTO.setIdentityCard(userArrayList.get(0).getIdentityCard());

        Mockito.when(userRepository.save(testUserDTO.getUser()))
                .thenReturn(userArrayList.get(0));
        Mockito.when(identityCardRepository.save(testUserDTO.getIdentityCard()))
                .thenReturn(testUserDTO.getIdentityCard());
        Mockito.when(loginDetailsRepository.save(testUserDTO.getLoginDetails()))
                .thenReturn(userArrayList.get(0).getLoginDetails());
        Mockito.when(addressRepository.save(testUserDTO.getAddress()))
                .thenReturn(testUserDTO.getAddress());

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(userArrayList.get(0));
        var expected =  Pair.of(true, json);
        var res = testedService.add(
                testUserDTO.getUser(),
                testUserDTO.getLoginDetails(),
                testUserDTO.getAddress(),
                testUserDTO.getIdentityCard());
        Assertions.assertEquals(res,expected);

    }

    @Test
    void addNullInputs() {
        var testUserDTO = new UserDTO();
        var expected = Pair.of(false, "You user data is incomplete please complete all fields(user, loginDetails, address, identityCard, departmentName)!");

        var res = testedService.add(
                testUserDTO.getUser(),
                testUserDTO.getLoginDetails(),
                testUserDTO.getAddress(),
                testUserDTO.getIdentityCard());
        Assertions.assertEquals(res,expected);

        testUserDTO.setUser(userArrayList.get(0));

        res = testedService.add(
                testUserDTO.getUser(),
                testUserDTO.getLoginDetails(),
                testUserDTO.getAddress(),
                testUserDTO.getIdentityCard());
        Assertions.assertEquals(res,expected);

        testUserDTO.setLoginDetails(userArrayList.get(0).getLoginDetails());

        res = testedService.add(
                testUserDTO.getUser(),
                testUserDTO.getLoginDetails(),
                testUserDTO.getAddress(),
                testUserDTO.getIdentityCard());
        Assertions.assertEquals(res,expected);

        testUserDTO.setAddress(userArrayList.get(0).getAddress());

        res = testedService.add(
                testUserDTO.getUser(),
                testUserDTO.getLoginDetails(),
                testUserDTO.getAddress(),
                testUserDTO.getIdentityCard());
        Assertions.assertEquals(res,expected);
    }

    @Test
    void numberOfUsers() {
        Mockito.when(userRepository.countUsers()).thenReturn((long) userArrayList.size());
        var expected = (long) userArrayList.size();
        var res = testedService.numberOfUsers();
        Assertions.assertEquals(res,expected);
    }
}