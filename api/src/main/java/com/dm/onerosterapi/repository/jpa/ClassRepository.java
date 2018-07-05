package com.dm.onerosterapi.repository.jpa;

import com.dm.onerosterapi.model.ClassOfCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassRepository extends JpaRepository<ClassOfCourse, Integer>{

    public ClassOfCourse findByClassId(int classId);

}