package com.project.HR.Connect.repository;

import com.project.HR.Connect.entitie.IdentityCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdentityCardRepository extends JpaRepository<IdentityCard, Integer> {
}
