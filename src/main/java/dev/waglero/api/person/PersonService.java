package dev.waglero.api.person;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    public Iterable<Person> findAll() {
        return personRepository.findAll();
    }

    public Optional<Person> findById(Integer id) {
        return personRepository.findById(id);
    }

    public Person save(Person person) {
        return personRepository.save(person);
    }

    public void deleteById(Integer id) {
        personRepository.deleteById(id);
    }

    public Person findPersonByCpf(String cpf) {
        return this.personRepository.findPersonByCpf(cpf);
    }
}