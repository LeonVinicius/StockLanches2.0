package br.modelo.lanchonete.demo.service;

import br.modelo.lanchonete.demo.model.HistoricoLog;
import br.modelo.lanchonete.demo.repository.HistoricoLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        
       
        if (tipoFiltro != null && !tipoFiltro.isEmpty() && !tipoFiltro.equals("Todos")) {
            return todos.stream()
                    .filter(log -> log.getTipo().equalsIgnoreCase(tipoFiltro))
                    .collect(Collectors.toList());
        }
        
        
        return todos.stream()
                    .sorted((a, b) -> b.getId().compareTo(a.getId()))
                    .collect(Collectors.toList());
    }
}