package io.github.lucianodacunha.api.repository;

import io.github.lucianodacunha.api.entity.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Objeto destinado a manipular os objetos da JPA, mapeando a entidade para
 * o bd.
 */
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Page<Paciente> findAllByAtivoTrue(Pageable pagina);

    @Query("""
        SELECT p.ativo
        FROM Paciente p
        WHERE p.id = :id
        """)
    boolean findAtivoById(Long id);
}
