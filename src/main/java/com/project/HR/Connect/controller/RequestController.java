package com.project.HR.Connect.controller;

import com.project.HR.Connect.entitie.Request;
import com.project.HR.Connect.entitie.RequestType;
import com.project.HR.Connect.entitie.Status;
import com.project.HR.Connect.security.JWTUtils;
import com.project.HR.Connect.service.RequestService;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.misc.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/request")
public class RequestController {
    @Autowired
    RequestService requestService;
    @Autowired
    JWTUtils jwtUtils;

    @GetMapping("/allRequests")
    @PreAuthorize("hasRole('hr')")
    public ArrayList<Request> getAllRequests() {
        return requestService.getAllRequests();
    }

    @GetMapping("/allDeniedRequests")
    @PreAuthorize("hasRole('hr')")
    public ArrayList<Request> getAllDeniedRequests() {
        return requestService.getAllDeniedRequests();
    }

    @GetMapping("/allApprovedRequests")
    @PreAuthorize("hasRole('hr')")
    public ArrayList<Request> getAllApprovedRequests() {
        return requestService.getAllApprovedRequests();
    }

    @GetMapping("/allInPendingRequests")
    @PreAuthorize("hasRole('hr')")
    public ArrayList<Request> getAllActiveRequests() {
        return requestService.getAllInPendingRequests();
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('hr')")
    public Triple<Pair<List<Status>, List<Integer>>, Pair<List<RequestType>,List<Integer>>, Pair<List<String>,List<Integer>>> getStatistics() {
        return requestService.getStatistics();
    }

    @GetMapping("/allRequestsByUser")
    @PreAuthorize("hasRole('employee')")
    public ArrayList<Request> getAllRequestsByUser(@RequestHeader("Authorization") String authorizationHeader) {
        String email = jwtUtils.getEmailFromJwtToken(jwtUtils.normalizeAuthorizationHeader(authorizationHeader));
        return requestService.getAllRequestsByUser(email);
    }

    @PutMapping("/add")
    @PreAuthorize("hasRole('employee')")
    public Request addRequest(@RequestHeader("Authorization") String authorizationHeader,
                              @RequestBody Request request) {
        String email = jwtUtils.getEmailFromJwtToken(jwtUtils.normalizeAuthorizationHeader(authorizationHeader));
        return requestService.addRequest(email, request);
    }

    @PostMapping("/respond-to-request")
    @PreAuthorize("hasRole('hr')")
    public boolean respondToRequest(@RequestHeader("Authorization") String authorizationHeader,
                                    @RequestParam(name = "requestId") int requestId,
                                    @RequestParam(name = "isApproved") boolean isApproved) {
        String email = jwtUtils.getEmailFromJwtToken(jwtUtils.normalizeAuthorizationHeader(authorizationHeader));
        return requestService.respondToRequest(email, requestId, isApproved);
    }

}
