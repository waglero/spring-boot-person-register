package dev.waglero.api.person;

import javax.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/persons")
@Slf4j
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping
    public ResponseEntity<Iterable<Person>> findAll() {
        return ResponseEntity.ok(personService.findAll());
    }

    @PostMapping
    public ResponseEntity<Person> create(@Valid @RequestBody Person person) {
        return ResponseEntity.ok(personService.save(person));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable Integer id) {
        Optional<Person> person = personService.findById(id);

        if (! person.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(person.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Person> update(@PathVariable Integer id, @Valid @RequestBody Person person) {
        if (!personService.findById(id).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(personService.save(person));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Integer id) {
        if (!personService.findById(id).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        personService.deleteById(id);

        return ResponseEntity.ok().build();
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = "cpf";
            if (error instanceof FieldError) {
                fieldName = ((FieldError) error).getField();
            }
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Map<String, String> handleDateExceptions(HttpMessageNotReadableException ex) {
        Map<String, String> errors = new HashMap<>();
        if (ex.getCause().getCause() instanceof java.time.format.DateTimeParseException) {
            errors.put("birthDate", "Data inválida");
        }
        return errors;

    }

}