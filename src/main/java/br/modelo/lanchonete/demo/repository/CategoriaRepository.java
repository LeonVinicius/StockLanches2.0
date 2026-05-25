package br.modelo.lanchonete.demo.repository;

import br.modelo.lanchonete.demo.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    
    // Método otimizado para o Spring Data fazer a busca direto no MySQL ignorando maiúsculas/minúsculas
    boolean existsByNomeIgnoreCase(String nome);
    
    Optional<Categoria> findByNomeIgnoreCase(String nome);
}