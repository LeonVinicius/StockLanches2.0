package br.modelo.lanchonete.demo.service;

import br.modelo.lanchonete.demo.model.HistoricoLog;
import br.modelo.lanchonete.demo.repository.HistoricoLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoricoService {

    @Autowired
    private HistoricoLogRepository historicoLogRepository;

    public void registrar(String tipo, String nomeProduto, Integer quantidade, String usuarioResponsavel) {
        HistoricoLog log = new HistoricoLog(tipo, nomeProduto, quantidade, usuarioResponsavel);
        historicoLogRepository.save(log);
    }

    public List<HistoricoLog> listar(String tipoFiltro) {
        List<HistoricoLog> todos = historicoLogRepository.findAll();

        // Ordena sempre por ID decrescente (mais recente primeiro), com ou sem filtro
        var stream = todos.stream()
                .sorted(Comparator.comparing(HistoricoLog::getId).reversed());

        if (tipoFiltro != null && !tipoFiltro.isEmpty() && !tipoFiltro.equals("Todos")) {
            stream = stream.filter(log -> log.getTipo().equalsIgnoreCase(tipoFiltro));
        }

        return stream.collect(Collectors.toList());
    }
}