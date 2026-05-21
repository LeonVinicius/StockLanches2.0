package br.modelo.lanchonete.demo.repository;

import br.modelo.lanchonete.demo.model.HistoricoLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricoLogRepository extends JpaRepository<HistoricoLog, Long> {
}