package com.backend.SafeSt.Repository;

import com.backend.SafeSt.Entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    ArrayList<Report> findAllByLocation_Id(Integer id);

}
