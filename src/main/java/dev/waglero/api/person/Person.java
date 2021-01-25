package dev.waglero.api.person;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import dev.waglero.validator.UniquePersonByCpfConstraint;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.hibernate.validator.constraints.br.CPF;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Data
@Entity
@Table(
    uniqueConstraints = @UniqueConstraint(columnNames = "cpf")
)
@UniquePersonByCpfConstraint
public class Person implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NotBlank(message = "Campo obrigatório")
    @Size(max = 255, message = "Tamanho máximo: 255")
    private String name;

    @Size(max = 2, message = "Tamanho máximo: 2")
    @Column(length = 2, nullable = false)
    @NotBlank(message = "Campo obrigatório")
    private String gender;

    @Size(max = 255, message = "Tamanho máximo: 255")
    @Column(nullable = false)
    @Email(message = "Email inválido")
    private String email;

    @NotNull(message = "Campo obrigatório")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Past(message = "A data deve ser no passado")
    private Date birthDate;

    @Size(max = 255, message = "Tamanho máximo: 255")
    @Column(nullable = false)
    @NotBlank(message = "Campo obrigatório")
    private String placeOfBirth;

    @Size(max = 255, message = "Tamanho máximo: 255")
    @Column(nullable = false)
    @NotBlank(message = "Campo obrigatório")
    private String nationality;

    @Size(max = 11, message = "Tamanho máximo: 11")
    @Column(length = 11, nullable = false)
    @NotBlank(message = "Campo obrigatório")
    @CPF(message = "CPF inválido")
    private String cpf;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Sao_Paulo")
    private Date createdAt;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Sao_Paulo")
    private Date updatedAt;

    public Integer getId() {
        return this.id;
    }
}