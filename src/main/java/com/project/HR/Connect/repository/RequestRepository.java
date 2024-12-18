package com.project.HR.Connect.repository;

import com.project.HR.Connect.entitie.FAQ;
import com.project.HR.Connect.entitie.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;

@Repository
public interface RequestRepository  extends JpaRepository<Request, Integer> {
    @Query("SELECT r FROM request r WHERE r.status = 0")
    ArrayList<Request> getAllInPendingRequests();
    @Query("SELECT r FROM request r WHERE r.status = 1")
    ArrayList<Request> getAllApprovedRequests();
    @Query("SELECT r FROM request r WHERE r.status = 2")
    ArrayList<Request> getAllDeniedRequests();
    ArrayList<Request> findAll();
    Request save(Request request);
    Request getById(int id);
    ArrayList<Request> getByRequesterId(int id);
    @Query("SELECT COUNT(id) FROM request id")
    Long countRequests();
    void deleteRequestsByRequesterId(int id);
}
