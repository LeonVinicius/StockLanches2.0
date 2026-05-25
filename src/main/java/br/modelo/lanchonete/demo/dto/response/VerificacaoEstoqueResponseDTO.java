package br.modelo.lanchonete.demo.dto.response;

import java.util.ArrayList;
import java.util.List;

public class VerificacaoEstoqueResponseDTO {
    
    private boolean ok;
    private List<InsumoFaltandoDTO> faltando = new ArrayList<>();
    
    // Construtor padrão obrigatório para o Jackson (JSON)
    public VerificacaoEstoqueResponseDTO() {
        this.ok = true; // Boa prática: começar como verdadeiro até que falte algo
    }
    
    public VerificacaoEstoqueResponseDTO(boolean ok) {
        this.ok = ok;
        this.faltando = new ArrayList<>();
    }
    
    public boolean isOk() {
        return ok;
    }
    
    public void setOk(boolean ok) {
        this.ok = ok;
    }
    
    public List<InsumoFaltandoDTO> getFaltando() {
        return faltando;
    }
    
    public void setFaltando(List<InsumoFaltandoDTO> faltando) {
        // Proteção extra: se o Jackson ou algum método passar null, reinicializa vazia
        if (faltando == null) {
            this.faltando = new ArrayList<>();
        } else {
            this.faltando = faltando;
        }
    }
    
    public void addFaltando(InsumoFaltandoDTO falta) {
        // Garante que a lista exista antes de adicionar (blindagem absoluta)
        if (this.faltando == null) {
            this.faltando = new ArrayList<>();
        }
        this.faltando.add(falta);
    }
}