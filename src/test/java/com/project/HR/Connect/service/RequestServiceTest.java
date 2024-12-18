package com.project.HR.Connect.service;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class RequestServiceTest {
    @Autowired
    private RequestService testedService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    JWTUtils jwtUtils;
    @MockBean
    private RequestRepository requestRepository;
    @MockBean
    private UserService userService;
    @MockBean
    private EmailSenderService emailSenderService;

    private ArrayList<Request> requestList;

    @BeforeEach
    void setUp() {
        this.requestList = new ArrayList<>();
        Request requestMedicalLeave = new Request();
        Request requestPaidLeave = new Request();
        Request requestEquipment = new Request();
        Request requestEmployedStatus = new Request();
        Request requestResignation = new Request();

        User user = new User();
        Address address = new Address();
        LoginDetails loginDetails = new LoginDetails();
        IdentityCard identityCard = new IdentityCard();
        user.setAddress(address);
        user.setLoginDetails(loginDetails);
        user.setIdentityCard(identityCard);

        user.getAddress().setId(1000);
        user.getAddress().setCity("Ploiesti");
        user.getAddress().setCountry("Romania");
        user.getAddress().setStreet("Strada Sperantei");
        user.getAddress().setStreetNumber("1");
        user.getAddress().setFlatNumber("11");
        user.getAddress().setCounty("Prahova");

        user.getLoginDetails().setId(1000);
        user.getLoginDetails().setEmail("andreimateescu120@yahoo.com");
        user.getLoginDetails().setRole("employee");

        user.getIdentityCard().setId(1000);
        user.getIdentityCard().setCnp("1111");
        user.getIdentityCard().setIssuer("Sprdiv");
        user.getIdentityCard().setIssuingDate(Date.valueOf("2009-06-06"));
        user.getIdentityCard().setNumber(123456);
        user.getIdentityCard().setSeries("px");

        user.setId(1);
        user.setVacationDays(24);
        user.setSickDays(183);
        user.setFirstName("Andrei");
        user.setLastName("Mateescu");
        user.setPosition(Position.Backend_Developer);
        user.setDepartment(Department.Dev);
        user.setPhoneNumber("0721111111");

        requestMedicalLeave.setId(1000);
        requestMedicalLeave.setRequester(user);
        requestMedicalLeave.setDetails("2023-06-30_2023-07-05");
        requestMedicalLeave.setRequestDate(Date.valueOf("2023-06-06"));
        requestMedicalLeave.setType(RequestType.Medical_leave);
        requestMedicalLeave.setStatus(Status.Pending);
        this.requestList.add(requestMedicalLeave);

        requestPaidLeave.setId(1001);
        requestPaidLeave.setRequester(user);
        requestPaidLeave.setDetails("2023-06-29_2023-07-08");
        requestPaidLeave.setRequestDate(Date.valueOf("2023-06-20"));
        requestPaidLeave.setType(RequestType.Paid_leave);
        requestPaidLeave.setStatus(Status.Pending);
        this.requestList.add(requestPaidLeave);

        requestEmployedStatus.setId(1002);
        requestEmployedStatus.setRequester(user);
        requestEmployedStatus.setDetails("credit la banca");
        requestEmployedStatus.setRequestDate(Date.valueOf("2023-06-20"));
        requestEmployedStatus.setType(RequestType.Employed_status);
        requestEmployedStatus.setStatus(Status.Pending);
        this.requestList.add(requestEmployedStatus);

        requestEquipment.setId(1003);
        requestEquipment.setRequester(user);
        requestEquipment.setDetails("Va rog frumos");
        requestEquipment.setRequestDate(Date.valueOf("2023-06-20"));
        requestEquipment.setType(RequestType.Employed_status);
        requestEquipment.setStatus(Status.Pending);
        this.requestList.add(requestEquipment);

        requestResignation.setId(1004);
        requestResignation.setRequester(user);
        requestResignation.setDetails("Nu mai suport");
        requestResignation.setRequestDate(Date.valueOf("2023-06-20"));
        requestResignation.setType(RequestType.Resignation);
        requestResignation.setStatus(Status.Pending);
        this.requestList.add(requestResignation);
    }

    @Test
    void getAll() {
        Mockito.when(requestRepository.findAll()).thenReturn(requestList);

        List<Request> returnedValue = testedService.getAllRequests();

        Assertions.assertEquals(returnedValue, requestList);
    }

    @Test
    void getAllDenied() {
        Mockito.when(requestRepository.getAllDeniedRequests()).thenReturn(requestList);

        List<Request> returnedValue = testedService.getAllDeniedRequests();

        Assertions.assertEquals(returnedValue, requestList);
    }

    @Test
    void getAllApproved() {
        Mockito.when(requestRepository.getAllApprovedRequests()).thenReturn(requestList);

        List<Request> returnedValue = testedService.getAllApprovedRequests();

        Assertions.assertEquals(returnedValue, requestList);
    }

    @Test
    void getAllInPending() {
        Mockito.when(requestRepository.getAllInPendingRequests()).thenReturn(requestList);

        List<Request> returnedValue = testedService.getAllInPendingRequests();

        Assertions.assertEquals(returnedValue, requestList);
    }

    @Test
    void getAllRequestsByUser() {
        Mockito.when(userService.getUserByLoginDetailsEmail(requestList.get(0).getRequester().getLoginDetails().getEmail()))
                        .thenReturn(requestList.get(0).getRequester());
        Mockito.when(requestRepository.getByRequesterId(requestList.get(1).getRequester().getId()))
                .thenReturn(requestList);

        String email = requestList.get(0).getRequester().getLoginDetails().getEmail();

        List<Request> returnedValue = testedService.getAllRequestsByUser(email);

        Assertions.assertEquals(returnedValue, requestList);
    }

    @Test
    void addRequest() {
        Request newRequest = new Request();

        newRequest.setId(requestList.get(0).getId());
        newRequest.setRequester(requestList.get(0).getRequester());
        newRequest.getRequester().getLoginDetails()
                .setEmail(requestList.get(0).getRequester().getLoginDetails().getEmail());
        newRequest.setDetails(requestList.get(0).getDetails());
        newRequest.setRequestDate(requestList.get(0).getRequestDate());
        newRequest.setType(requestList.get(0).getType());
        newRequest.setStatus(requestList.get(0).getStatus());

        Mockito.when(requestRepository.save(newRequest)).thenReturn(newRequest);
        Request expected = testedService.addRequest(newRequest.getRequester().getLoginDetails().getEmail(),newRequest);

        java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());

        newRequest.setRequester(requestList.get(0).getRequester());
        newRequest.setRequestDate(date);
        newRequest.setStatus(Status.Pending);

        assertThat(expected).isEqualTo(newRequest);
    }

}