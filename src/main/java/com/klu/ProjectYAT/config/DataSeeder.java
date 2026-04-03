package com.klu.ProjectYAT.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.klu.ProjectYAT.model.Course;
import com.klu.ProjectYAT.model.User;
import com.klu.ProjectYAT.repository.CourseRepository;
import com.klu.ProjectYAT.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    private static final String REACT_MODULES_JSON = "[\"Getting Started with React\",\"React Components and JSX\",\"State and Props\"]";
    private static final String JS_MODULES_JSON = "[\"JavaScript Fundamentals\",\"Asynchronous JavaScript\",\"DOM and Browser APIs\"]";
    private static final String NODE_MODULES_JSON = "[\"Node.js Runtime Basics\",\"Build REST APIs with Express\",\"MongoDB Integration\"]";

    @Bean
    CommandLineRunner seedData(
            UserRepository userRepository,
            CourseRepository courseRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            userRepository.findByEmail("educator@course.com").orElseGet(() -> {
                User newEducator = new User();
                newEducator.setName("Educator");
                newEducator.setEmail("educator@course.com");
                newEducator.setPassword(passwordEncoder.encode("educator123"));
                newEducator.setRole("educator");
                newEducator.setVerified(true);
                return userRepository.save(newEducator);
            });

            // Remove legacy seed titles from previous versions.
            courseRepository.deleteByTitle("Spring Boot Fundamentals");
            courseRepository.deleteByTitle("React for Beginners");

            Course springCourse = new Course();
            springCourse.setTitle("React Frontend Essentials");
            springCourse.setRegisteredStudents(0);
            springCourse.setModules(REACT_MODULES_JSON);

            Course reactCourse = new Course();
            reactCourse.setTitle("Modern JavaScript for Web Apps");
            reactCourse.setRegisteredStudents(0);
            reactCourse.setModules(JS_MODULES_JSON);

            Course nodeCourse = new Course();
            nodeCourse.setTitle("Node.js & Express Full-Stack APIs");
            nodeCourse.setRegisteredStudents(0);
            nodeCourse.setModules(NODE_MODULES_JSON);

            courseRepository.findByTitle(springCourse.getTitle()).ifPresentOrElse(existing -> {
                existing.setModules(REACT_MODULES_JSON);
                courseRepository.save(existing);
            }, () -> courseRepository.save(springCourse));

            courseRepository.findByTitle(reactCourse.getTitle()).ifPresentOrElse(existing -> {
                existing.setModules(JS_MODULES_JSON);
                courseRepository.save(existing);
            }, () -> courseRepository.save(reactCourse));

            courseRepository.findByTitle(nodeCourse.getTitle()).ifPresentOrElse(existing -> {
                existing.setModules(NODE_MODULES_JSON);
                courseRepository.save(existing);
            }, () -> courseRepository.save(nodeCourse));
        };
    }
}
