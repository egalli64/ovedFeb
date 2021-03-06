package pa.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pa.model.Employee;
import pa.model.EmployeeRepository;

@Controller
public class EmployeeCtrl {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeCtrl.class);
    private static final String CODER = "IT_PROG";

    @Autowired
    EmployeeRepository repo;

    @GetMapping("/employees")
    public String allEmployees(Model model) {
        logger.debug("All employees");
        model.addAttribute("employees", repo.findAll());
        return "employees";
    }

    @GetMapping("/hEmployees")
    public String hEmployees(Model model) {
        logger.debug("All employees with character h");
        model.addAttribute("employees", repo.findWhereNameContainsH());
        model.addAttribute("msg", " with an h in their name");
        return "employees";
    }

    @GetMapping("/pagedStClerk")
    public String pagedEmployees( //
            @RequestParam int page, //
            Model model) {
        logger.debug("Paged employees");

        Pageable pageable = PageRequest.of(page, 5, Direction.ASC, "firstName");
        Page<Employee> result = repo.findByJobId("ST_CLERK", pageable);

        model.addAttribute("employees", result);
        model.addAttribute("msg", " paged st_clerks");
        return "employees";
    }

    @GetMapping("/pagedStClerk2")
    public String pagedEmployees2( //
            @RequestParam int page, //
            Model model) {
        logger.debug("Paged employees /2");

        Pageable pageable = PageRequest.of(page, 5, Direction.ASC, "firstName");
        List<Employee> result = repo.findByJobId_("ST_CLERK", pageable);

        model.addAttribute("employees", result);
        model.addAttribute("msg", " paged st_clerks");
        return "employees";
    }

    @GetMapping("/sortedStClerk")
    public String sortedEmployees(Model model) {
        logger.debug("Sorted employees");

        List<Employee> result = repo.findByJobId("ST_CLERK", Sort.by(Direction.ASC, "lastName"));

        model.addAttribute("employees", result);
        model.addAttribute("msg", " st_clerks sorted by last name");
        return "employees";
    }

    @GetMapping("/employee")
    public String find( //
            @RequestParam long id, //
            Model model) {
        logger.debug("Find employee by id: " + id);

        Optional<Employee> employee = repo.findById(id);
        if (employee.isPresent()) {
            model.addAttribute("employee", employee.get());
        } else {
            model.addAttribute("id", id);
        }
        return "employee";
    }

    @GetMapping("/coders")
    public String allCoders(Model model) {
        logger.debug("All coders");
        model.addAttribute("employees", repo.findByJobIdOrderById(CODER));
        return "coders";
    }

    @GetMapping("/coders/order")
    public String orderCoders( //
            @RequestParam String by, //
            Model model) {
        logger.debug("Order coders by " + by);

        List<Employee> coders;
        switch (by) {
        case "First":
            coders = repo.findByJobIdOrderByFirstName(CODER);
            break;
        case "Last":
            coders = repo.findByJobIdOrderByLastName(CODER);
            break;
        default:
            coders = repo.findByJobIdOrderById(CODER);
            break;
        }

        model.addAttribute("employees", coders);
        return "coders";
    }

    @GetMapping("/coders/create")
    public String createCoder( //
            @RequestParam String first, //
            @RequestParam String last, //
            Model model) {
        logger.debug("Create coder");
        try {
            repo.save(new Employee(first, last, CODER));
            model.addAttribute("message", String.format("Coder %s %s correctly created", first, last));
        } catch (DataIntegrityViolationException dive) {
            String message = String.format("Can't create coder %s %s", first, last);
            logger.error(message);
            model.addAttribute("message", message);
        }
        model.addAttribute("employees", repo.findByJobIdOrderById(CODER));
        return "coders";
    }

    @GetMapping("/coders/save")
    public String saveCoder( //
            @RequestParam long id, //
            @RequestParam String first, //
            @RequestParam String last, //
            Model model) {
        logger.debug("Save coder");

        Optional<Employee> opt = repo.findByIdAndJobId(id, CODER);
        if (!opt.isPresent()) {
            model.addAttribute("message", "There is no coder with id " + id);
        } else {
            Employee employee = opt.get();
            if (first != null) {
                employee.setFirstName(first);
            }
            if (last != null) {
                employee.setLastName(last);
            }
            if (first != null || last != null) {
                employee.setDefaultEmail();
            }
            try {
                repo.save(employee);
                model.addAttribute("message", String.format("Coder %s %s correctly saved", first, last));
            } catch (Exception ex) {
                String message = String.format("Can't save coder %s %s", first, last);
                logger.error(message, ex);
                model.addAttribute("message", message);
            }
        }

        model.addAttribute("employees", repo.findByJobIdOrderById(CODER));
        return "coders";
    }

    @GetMapping("/coders/delete")
    public String deleteCoder( //
            @RequestParam long id, //
            Model model) {
        logger.debug("Delete coder " + id);

        Optional<Employee> opt = repo.findByIdAndJobId(id, CODER);
        if (!opt.isPresent()) {
            model.addAttribute("message", "There is no coder with id " + id);
        } else {
            try {
                repo.deleteById(id);
                model.addAttribute("message", String.format("Coder %d removed", id));
            } catch (Exception ex) {
                String message = "Can't delete coder " + id;
                logger.error(message, ex);
                model.addAttribute("message", message);
            }
        }

        model.addAttribute("employees", repo.findByJobIdOrderById(CODER));
        return "coders";
    }
}
