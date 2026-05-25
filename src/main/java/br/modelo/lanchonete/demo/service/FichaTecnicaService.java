package br.modelo.lanchonete.demo.service;

import br.modelo.lanchonete.demo.dto.request.FichaTecnicaRequestDTO;
import br.modelo.lanchonete.demo.dto.response.FichaTecnicaResponseDTO;
import br.modelo.lanchonete.demo.model.Estoque;
import br.modelo.lanchonete.demo.model.FichaTecnica;
import br.modelo.lanchonete.demo.repository.EstoqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class FichaTecnicaService {

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Transactional
    public List<FichaTecnica> processarFichaTecnica(List<FichaTecnicaRequestDTO> itensFicha) {
        List<FichaTecnica> listaProcessada = new ArrayList<>();
        
        if (itensFicha == null || itensFicha.isEmpty()) {
            return listaProcessada;
        }

        for (FichaTecnicaRequestDTO item : itensFicha) {
            Estoque insumoBanco = estoqueRepository.findById(item.getInsumoId())
                .orElseThrow(() -> new RuntimeException("Insumo com ID " + item.getInsumoId() + " não encontrado no estoque"));
            
            FichaTecnica novoItem = new FichaTecnica();
            novoItem.setInsumo(insumoBanco);
            novoItem.setQuantidade(item.getQuantidade());
            novoItem.setUnidade(item.getUnidade());
            
            listaProcessada.add(novoItem);
        }
        
        return listaProcessada;
    }
    
    // 🔥 MÉTODO ADICIONADO: Converte FichaTecnica para ResponseDTO
    public FichaTecnicaResponseDTO convertToFichaTecnica(FichaTecnica ft) {
        if (ft == null) return null;
        
        FichaTecnicaResponseDTO dto = new FichaTecnicaResponseDTO();
        dto.setId(ft.getId());
        dto.setQuantidade(ft.getQuantidade());
        dto.setUnidade(ft.getUnidade());
        
        if (ft.getInsumo() != null) {
            dto.setInsumoId(ft.getInsumo().getId());
            dto.setInsumoNome(ft.getInsumo().getNome());
        }
        
        return dto;
    }
}