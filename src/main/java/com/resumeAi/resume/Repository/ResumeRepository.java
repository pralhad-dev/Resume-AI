package com.resumeAi.resume.Repository;

import com.resumeAi.resume.Entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, Long> {


}
