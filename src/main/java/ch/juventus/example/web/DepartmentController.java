package ch.juventus.example.web;

import ch.juventus.example.data.Department;
import ch.juventus.example.data.DepartmentRepository;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class DepartmentController {
    private DepartmentRepository departmentRepository;

    public DepartmentController(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @GetMapping("/departments")
    public Resources<Department> all() {
        List<Department> departments =
            departmentRepository.findAll().stream()
                .map(d -> addHateoasLinks(d))
                .collect(Collectors.toList());
        return new Resources<>(departments);
    }

    @GetMapping("/departments/{id}")
    public Resource<Department> get(@PathVariable Long id) {
        return new Resource<>(addHateoasLinks(departmentRepository.getOne(id)));
    }

    public Department addHateoasLinks(Department department) {
        // self-link
        department.add(
            linkTo(
                methodOn(DepartmentController.class).get(department.getStid())
            ).withSelfRel());

        // link to employees
        department.getEmployees().forEach(
                e -> department.add(
                    linkTo(
                        methodOn(EmployeeController.class).get(e.getStid())
                    ).withRel("employees"))
        );
        return department;
    }
}
