package ch.juventus.example.web;

import ch.juventus.example.data.Employee;
import ch.juventus.example.data.EmployeeRepository;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
public class EmployeeController {

    private EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/employees")
    public Resources<Employee> all() {
        return new Resources<>(employeeRepository.findAll());
    }

    @GetMapping("/employees/{id}")
    public Resource<Employee> get(@PathVariable Long id) {
        return new Resource<>(employeeRepository.getOne(id));
    }

    @PostMapping("/employees")
    public ResponseEntity<?> create(@RequestBody Employee requestEmployee) {

        return createEmployeeResource(requestEmployee);
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<?> createOrUpdate(@PathVariable Long id, @RequestBody Employee employee) {

        if (employeeRepository.existsById(id)) {

            employee.setStid(id);
            employeeRepository.save(employee);
            return ResponseEntity.ok().build();

        } else {

            return createEmployeeResource(employee);

        }
    }

    @DeleteMapping("/employees/{id}")
    public void delete(@PathVariable Long id) {
        employeeRepository.deleteById(id);
    }

    private ResponseEntity<?> createEmployeeResource(@RequestBody Employee requestEmployee) {
        Employee persistedEmployee = employeeRepository.save(requestEmployee);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest().path("/{id}")
            .buildAndExpand(persistedEmployee.getStid()).toUri();

        return ResponseEntity.created(location).build();
    }

}
