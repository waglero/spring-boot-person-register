package dev.waglero.validator;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import dev.waglero.api.person.Person;
import dev.waglero.api.person.PersonService;

public class UniquePersonByCpfValidator implements ConstraintValidator<UniquePersonByCpfConstraint, Person> {

    @Autowired
    private PersonService personService;

    @Override
    public void initialize(UniquePersonByCpfConstraint cpf) {
    }

    @Override
    public boolean isValid(Person personFromRequest, ConstraintValidatorContext cxt) {
        Person person = null;
        boolean valid = true;

        try {
            person = this.personService.findPersonByCpf(personFromRequest.getCpf());
            if (person != null && ! person.getId().equals(personFromRequest.getId())) {
                valid = false;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return valid;
    }
}