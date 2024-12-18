package com.project.HR.Connect.service;

import com.project.HR.Connect.entitie.Request;
import com.project.HR.Connect.entitie.RequestType;
import com.project.HR.Connect.entitie.Status;
import com.project.HR.Connect.entitie.User;
import com.project.HR.Connect.repository.RequestRepository;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.misc.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.lang.Math.abs;

@Service
public class RequestService {
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailSenderService emailSenderService;

    public ArrayList<Request> getAllRequests(){
        return requestRepository.findAll();
    }

    public ArrayList<Request> getAllDeniedRequests(){
        return requestRepository.getAllDeniedRequests();
    }

    public ArrayList<Request> getAllApprovedRequests(){
        return requestRepository.getAllApprovedRequests();
    }

    public ArrayList<Request> getAllInPendingRequests(){
        return requestRepository.getAllInPendingRequests();
    }

    public ArrayList<Request> getAllRequestsByUser(String email){
        User user = userService.getUserByLoginDetailsEmail(email);
        return requestRepository.getByRequesterId(user.getId());
    }
    public Request addRequest(String email, Request request){
        User user = userService.getUserByLoginDetailsEmail(email);
        java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());

        request.setRequester(user);
        request.setRequestDate(date);
        request.setStatus(Status.Pending);

        return  requestRepository.save(request);
    }

    public boolean respondToRequest(String email,int requestId, boolean isApproved){
        User user = userService.getUserByLoginDetailsEmail(email);
        Request requestFromRepo = requestRepository.getById(requestId);
        java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());

        if(requestFromRepo.getType() == RequestType.Paid_leave){
            //for Paid_leave requestDetails should be of format "YYYY-MM-DD_YYYY_MM_DD" where first date is
            // the start date and the second date is the end date
            String segment[] = requestFromRepo.getDetails().split("_");
            LocalDate d1 = LocalDate.parse(segment[0], DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate d2 = LocalDate.parse(segment[1], DateTimeFormatter.ISO_LOCAL_DATE);
            Duration diff = Duration.between(d1.atStartOfDay(), d2.atStartOfDay());
            long diffDays = diff.toDays();

            if(abs(diffDays)>requestFromRepo.getRequester().getVacationDays()) {
                requestFromRepo.setFinishDate(date);
                requestFromRepo.setStatus(Status.Denied);
                requestFromRepo.setResponder(user);
                requestRepository.save(requestFromRepo);

                return emailSenderService.sendFailEmail(requestFromRepo.getRequester().getLoginDetails().getEmail(),
                        requestFromRepo.getRequester().getFirstName());
            } else requestFromRepo.getRequester()
                    .setVacationDays((int) (requestFromRepo.getRequester().getVacationDays() - diffDays));
        }

        if(requestFromRepo.getType() == RequestType.Medical_leave){
            //for Medical_leave requestDetails should be of format "YYYY-MM-DD_YYYY_MM_DD" where first date is
            // the start date and the second date is the end date
            String segment[] = requestFromRepo.getDetails().split("_");
            LocalDate d1 = LocalDate.parse(segment[0], DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate d2 = LocalDate.parse(segment[1], DateTimeFormatter.ISO_LOCAL_DATE);
            Duration diff = Duration.between(d1.atStartOfDay(), d2.atStartOfDay());
            long diffDays = diff.toDays();

            if(abs(diffDays)>requestFromRepo.getRequester().getSickDays()) {
                requestFromRepo.setFinishDate(date);
                requestFromRepo.setStatus(Status.Denied);
                requestFromRepo.setResponder(user);
                requestRepository.save(requestFromRepo);
                return emailSenderService.sendFailEmail(requestFromRepo.getRequester().getLoginDetails().getEmail(),
                        requestFromRepo.getRequester().getFirstName());
            } else requestFromRepo.getRequester()
                    .setSickDays((int) (requestFromRepo.getRequester().getSickDays() - diffDays));
        }

        if(isApproved) {
            requestFromRepo.setFinishDate(date);
            requestFromRepo.setStatus(Status.Approved);
            requestFromRepo.setResponder(user);
            requestRepository.save(requestFromRepo);

            switch (requestFromRepo.getType()) {
                case Custom_request:
                    return emailSenderService.sendAcceptanceEmail(requestFromRepo.getRequester().getLoginDetails().getEmail(),
                            requestFromRepo.getRequester().getFirstName(), "custom");
                case Equipment_request:
                    return emailSenderService.sendAcceptanceEmail(requestFromRepo.getRequester().getLoginDetails().getEmail(),
                            requestFromRepo.getRequester().getFirstName(), "equipment");
                case Training_request:
                    return emailSenderService.sendAcceptanceEmail(requestFromRepo.getRequester().getLoginDetails().getEmail(),
                            requestFromRepo.getRequester().getFirstName(), "training");
            }
            emailSenderService.sendEmailWithDocument(requestFromRepo, numberOfRequests());
            if(requestFromRepo.getType() == RequestType.Resignation){
                int requesterId = requestFromRepo.getRequester().getId();
                deleteRequestById(requestId);
                userService.delete(requesterId);
            }
            return true;
        }
        else {
            requestFromRepo.setFinishDate(date);
            requestFromRepo.setStatus(Status.Denied);
            requestFromRepo.setResponder(user);
            requestRepository.save(requestFromRepo);
            return false;
        }
    }
    Long numberOfRequests(){
        return requestRepository.countRequests();
    }

    void deleteRequestById(int id){
        requestRepository.deleteById(id);
    }

    public Triple<
            Pair<List<Status>, List<Integer>>,
            Pair<List<RequestType>,List<Integer>>,
            Pair<List<String>,List<Integer>>> getStatistics(){

        var statuses = Arrays.stream(Status.values()).toList();
        var statusesValues = new ArrayList<Integer>(Status.values().length);
        statuses.forEach((value)->statusesValues.add(0));

        var requestTypes = Arrays.stream(RequestType.values()).toList();
        var requestTypesValues = new ArrayList<Integer>(RequestType.values().length);
        requestTypes.forEach((value)-> requestTypesValues.add(0));

        var inOutOfOffice = new ArrayList<String>();
        inOutOfOffice.add("La birou");
        inOutOfOffice.add("In concediu");
        var inOutOfOfficeValues = new ArrayList<Integer>(2);
        inOutOfOfficeValues.ensureCapacity(2);

        inOutOfOfficeValues.add(0, userService.numberOfUsers().intValue());

        HashSet<String> peopleOut = new HashSet<>();
        requestRepository.findAll().forEach((value) ->{
            var statusIndex = statuses.indexOf(value.getStatus());
            statusesValues.set(statusIndex,
                    statusesValues.get(statusIndex) + 1
            );

            var typeIndex = requestTypes.indexOf(value.getType());
            requestTypesValues.set(typeIndex,
                    requestTypesValues.get(typeIndex) + 1
            );

            if (value.getType() == RequestType.Paid_leave || value.getType() == RequestType.Medical_leave){
                Date currentDate = new Date(System.currentTimeMillis());

                var dates = value.getDetails().split("_");

                var startDateComponents = dates[0];
                var startDate = Date.valueOf(startDateComponents);

                var endDateComponents = dates[1];
                var endDate = Date.valueOf(endDateComponents);

                if(currentDate.compareTo(startDate) > 0 && currentDate.compareTo(endDate) < 0){
                    peopleOut.add(value.getRequester().getLastName() + value.getRequester().getFirstName());
                }
            }
        });

        inOutOfOfficeValues.add(1, peopleOut.size());

        if (peopleOut.size() > inOutOfOfficeValues.get(0)) inOutOfOfficeValues.set(1, inOutOfOfficeValues.get(0));

        return new Triple<>(new Pair<>(statuses, statusesValues),
                new Pair<>(requestTypes, requestTypesValues),
                new Pair<>(inOutOfOffice, inOutOfOfficeValues));
    }
}
