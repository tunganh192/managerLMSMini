package com.honda.managerlmsmini.service;

import com.honda.managerlmsmini.entity.Course;
import com.honda.managerlmsmini.entity.Student;
import java.util.List;

public interface ExcelService {
    byte[] students(List<Student> students);

    byte[] courses(List<Course> courses);
}
