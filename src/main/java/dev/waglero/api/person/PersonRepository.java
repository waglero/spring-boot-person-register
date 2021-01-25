package dev.waglero.api.person;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PersonRepository extends JpaRepository<Person, Integer> {
    @Query("SELECT p FROM Person p WHERE p.cpf = ?1")
    public Person findPersonByCpf(String cpf);
}