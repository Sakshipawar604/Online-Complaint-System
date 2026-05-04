package com.project.smartportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.smartportal.entity.Complaint;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    // For Dashboard Count 
    long countByStatus(String status);

    //  For Filter Feature
    List<Complaint> findByStatus(String status);

}