package com.example.register;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}

@Entity
class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double salary;
    private String tech;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getSalary() { return salary; }
    public void setSalary(Double salary) { this.salary = salary; }
    public String getTech() { return tech; }
    public void setTech(String tech) { this.tech = tech; }
}

interface EmployeeRepository extends JpaRepository<Employee, Long> {}

// ========== HTML FORM CONTROLLER ==========
@Controller
class WebController {
    
    @Autowired
    private EmployeeRepository repo;
    
    // Show form page
    @GetMapping("/")
    public String showForm() {
        return "index";
    }
    
    // Save from form
    @PostMapping("/save")
    public String save(@RequestParam String name, 
                      @RequestParam Double salary, 
                      @RequestParam String tech) {
        Employee e = new Employee();
        e.setName(name);
        e.setSalary(salary);
        e.setTech(tech);
        repo.save(e);
        return "redirect:/success";
    }
    
    // Success page
    @GetMapping("/success")
    public String success() {
        return "success";
    }
    
    // DELETE via browser: http://localhost:8080/delete/1
    @GetMapping("/delete/{id}")
    @ResponseBody
    @Transactional
    public String deleteBrowser(@PathVariable Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            repo.flush();
            return "✅ Employee with ID " + id + " deleted from database!";
        }
        return "❌ Employee with ID " + id + " not found!";
    }
    
    // UPDATE via browser: http://localhost:8080/update?id=1&name=John&salary=60000&tech=Java
    @GetMapping("/update")
    @ResponseBody
    @Transactional
    public String updateBrowser(@RequestParam Long id,
                               @RequestParam String name,
                               @RequestParam Double salary,
                               @RequestParam String tech) {
        Employee e = repo.findById(id).orElse(null);
        if (e != null) {
            e.setName(name);
            e.setSalary(salary);
            e.setTech(tech);
            repo.save(e);
            repo.flush();
            return "✅ Employee with ID " + id + " updated in database!";
        }
        return "❌ Employee with ID " + id + " not found!";
    }
}

// ========== REST API CONTROLLER ==========
@RestController
@RequestMapping("/api/employees")
class EmployeeRestController {
    
    @Autowired
    private EmployeeRepository repo;
    
    // ===== GET - Get all employees =====
    // URL: http://localhost:8080/api/employees
    @GetMapping
    public List<Employee> getAllEmployees() {
        return repo.findAll();
    }
    
    // ===== GET - Get employee by ID =====
    // URL: http://localhost:8080/api/employees/1
    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }
    
    // ===== POST - Create new employee =====
    // URL: http://localhost:8080/api/employees
    // Method: POST
    // Body (JSON): {"name":"John","salary":50000,"tech":"Java"}
    @PostMapping
    @Transactional
    public Employee createEmployee(@RequestBody Employee employee) {
        Employee saved = repo.save(employee);
        repo.flush();
        return saved;
    }
    
    // ===== PUT - Update employee =====
    // URL: http://localhost:8080/api/employees/1
    // Method: PUT
    // Body (JSON): {"name":"Jane","salary":60000,"tech":"Python"}
    @PutMapping("/{id}")
    @Transactional
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        Employee e = repo.findById(id).orElse(null);
        if (e != null) {
            e.setName(employee.getName());
            e.setSalary(employee.getSalary());
            e.setTech(employee.getTech());
            repo.save(e);
            repo.flush();
            return e;
        }
        return null;
    }
    
    // ===== DELETE - Delete employee =====
    // URL: http://localhost:8080/api/employees/1
    // Method: DELETE
    @DeleteMapping("/{id}")
    @Transactional
    public String deleteEmployee(@PathVariable Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            repo.flush();
            return "✅ Employee with ID " + id + " deleted from database!";
        }
        return "❌ Employee with ID " + id + " not found!";
    }
}