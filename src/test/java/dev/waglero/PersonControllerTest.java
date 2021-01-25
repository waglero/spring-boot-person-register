package dev.waglero;

import java.util.List;
import java.util.Optional;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import dev.waglero.api.person.Person;
import dev.waglero.api.person.PersonController;
import dev.waglero.api.person.PersonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.CoreMatchers.is;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PersonService service;

    private String uri = "/api/v1/persons";

    @Value("${app.auth.user}")
    private String user;

    @Value("${app.auth.password}")
    private String password;

    @Test
    public void itShouldGetAListOfPersonsSuccessfully() throws Exception {

        Person person = this.makePerson();

        List<Person> allPersons = Arrays.asList(person);

        given(service.findAll()).willReturn(allPersons);

        mvc.perform(get(this.uri).contentType(MediaType.APPLICATION_JSON).header("Authorization", this.getEncodedAuthData()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(person.getId())))
            .andExpect(jsonPath("$[0].name", is(person.getName())))
            .andExpect(jsonPath("$[0].email", is(person.getEmail())))
            .andExpect(jsonPath("$[0].gender", is(person.getGender())))
            .andExpect(jsonPath("$[0].birthDate", is("1992-01-02")))
            .andExpect(jsonPath("$[0].placeOfBirth", is(person.getPlaceOfBirth())))
            .andExpect(jsonPath("$[0].nationality", is(person.getNationality())))
            .andExpect(jsonPath("$[0].cpf", is(person.getCpf())));
    }

    @Test
    public void itShouldGetAnUniquePerson() throws Exception {
        Optional<Person> person = Optional.of(this.makePerson());
        
        given(service.findById(person.get().getId())).willReturn(person);

        Person nonOptionalPerson = person.get();

        mvc.perform(
                get(this.uri + '/' + nonOptionalPerson.getId()).header("Authorization", this.getEncodedAuthData())
            .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk())
            .andExpect(jsonPath("id", is(nonOptionalPerson.getId())))
            .andExpect(jsonPath("name", is(nonOptionalPerson.getName())))
            .andExpect(jsonPath("email", is(nonOptionalPerson.getEmail())))
            .andExpect(jsonPath("gender", is(nonOptionalPerson.getGender())))
            .andExpect(jsonPath("birthDate", is("1992-01-02")))
            .andExpect(jsonPath("placeOfBirth", is(nonOptionalPerson.getPlaceOfBirth())))
            .andExpect(jsonPath("nationality", is(nonOptionalPerson.getNationality())))
            .andExpect(jsonPath("cpf", is(nonOptionalPerson.getCpf())));

    }

    @Test
    public void itShouldValidateFields() throws Exception {
        JSONObject person = new JSONObject();
        person.put("name", null);
        person.put("gender", null);
        person.put("email", "email-invalido");
        person.put("birthDate", null);
        person.put("placeOfBirth", null);
        person.put("nationality", null);
        person.put("cpf", "123456789");

        mvc.perform(post(this.uri).header("Authorization", this.getEncodedAuthData())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(person.toString())
            .characterEncoding("utf-8")
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("email", is("Email inválido")))
            .andExpect(jsonPath("placeOfBirth", is("Campo obrigatório")))
            .andExpect(jsonPath("nationality", is("Campo obrigatório")))
            .andExpect(jsonPath("birthDate", is("Campo obrigatório")))
            .andExpect(jsonPath("gender", is("Campo obrigatório")))
            .andExpect(jsonPath("name", is("Campo obrigatório")))
            .andExpect(jsonPath("cpf", is("CPF inválido")));

    }

    @Test
    public void itShouldCreateAPersonSuccessfully() throws Exception {
        Person personSaved = this.makePerson();
        when(service.save(any(Person.class))).thenReturn(personSaved);

        JSONObject person = new JSONObject();
        person.put("name", "Wagner");
        person.put("gender", "M");
        person.put("email", "wagnerrdds@gmail.com");
        person.put("birthDate", "1992-01-02");
        person.put("placeOfBirth", "Aracaju");
        person.put("nationality", "Brasileiro");
        person.put("cpf", "12735865061");

        mvc.perform(
            post(this.uri).header("Authorization", this.getEncodedAuthData())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(person.toString())
            .characterEncoding("utf-8")
        ).andExpect(status().isOk())
        .andExpect(jsonPath("name", is(person.get("name"))))
        .andExpect(jsonPath("email", is(person.get("email"))))
        .andExpect(jsonPath("gender", is(person.get("gender"))))
        .andExpect(jsonPath("birthDate", is(person.get("birthDate"))))
        .andExpect(jsonPath("placeOfBirth", is(person.get("placeOfBirth"))))
        .andExpect(jsonPath("nationality", is(person.get("nationality"))))
        .andExpect(jsonPath("cpf", is(person.get("cpf"))));
    }

    @Test
    public void itShouldUpdateAPersonSuccessfully() throws Exception {
        Optional<Person> optionalPerson = Optional.of(this.makePerson());
        given(service.findById(optionalPerson.get().getId())).willReturn(optionalPerson);
        when(service.save(any(Person.class))).thenReturn(optionalPerson.get());

        JSONObject person = new JSONObject();
        person.put("name", "Wagner");
        person.put("gender", "M");
        person.put("email", "wagnerrdds@gmail.com");
        person.put("birthDate", "1992-01-02");
        person.put("placeOfBirth", "Aracaju");
        person.put("nationality", "Brasileiro");
        person.put("cpf", "12735865061");

        mvc.perform(put(this.uri + '/' + 1)
            .header("Authorization", this.getEncodedAuthData())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(person.toString())
            .characterEncoding("utf-8")
        ).andExpect(status().isOk())
        .andExpect(jsonPath("name", is(person.get("name"))))
        .andExpect(jsonPath("email", is(person.get("email"))))
        .andExpect(jsonPath("gender", is(person.get("gender"))))
        .andExpect(jsonPath("birthDate", is(person.get("birthDate"))))
        .andExpect(jsonPath("placeOfBirth", is(person.get("placeOfBirth"))))
        .andExpect(jsonPath("nationality", is(person.get("nationality"))))
        .andExpect(jsonPath("cpf", is(person.get("cpf"))));
    }

    @Test
    public void itShouldFailToUpdateWhenAnInvalidIdIsGiven() throws Exception {
        mvc.perform(put(this.uri + '/' + 1).header("Authorization", this.getEncodedAuthData()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void itShouldDeleteAPersonSuccessfully() throws Exception {
        Optional<Person> optionalPerson = Optional.of(this.makePerson());
        given(service.findById(optionalPerson.get().getId())).willReturn(optionalPerson);

        mvc.perform(delete(this.uri + '/' + 1)
            .header("Authorization", this.getEncodedAuthData())
        ).andExpect(status().isOk());
    }

    @Test
    public void itShouldFailToDeleteWhenAnInvalidIdIsGiven() throws Exception {
        mvc.perform(delete(this.uri + '/' + 1)
            .header("Authorization", this.getEncodedAuthData())
        ).andExpect(status().isBadRequest());
    }

    private Person makePerson() {
        Date birthDate = new Date();
        try {
            birthDate = new SimpleDateFormat("yyyy-MM-dd").parse("1992-01-02");
        } catch (Exception e) {
        }

        Person person = new Person();
        
        person.setId(1);
        person.setName("Wagner");
        person.setEmail("wagnerrdds@gmail.com");
        person.setGender("M");
        person.setBirthDate(birthDate);
        person.setPlaceOfBirth("Aracaju");
        person.setNationality("Brasileiro");
        person.setCpf("12735865061");

        return person;
    }

    private String getEncodedAuthData() {
        String authData = this.user + ':' + this.password;
        return "Basic " + Base64.getEncoder().encodeToString(authData.getBytes());
    }

}
