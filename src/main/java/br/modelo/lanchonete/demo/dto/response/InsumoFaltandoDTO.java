package br.modelo.lanchonete.demo.dto.response;

public class InsumoFaltandoDTO {
    
    private String insumoNome;
    private Integer disponivel;  // 🔥 Mudar para Integer
    private Double necessario;
    private Double falta;
    private String unidade;
    
    public InsumoFaltandoDTO() {}
    
    public String getInsumoNome() {
        return insumoNome;
    }
    
    public void setInsumoNome(String insumoNome) {
        this.insumoNome = insumoNome;
    }
    
    public Integer getDisponivel() {  // 🔥 Mudar para Integer
        return disponivel;
    }
    
    public void setDisponivel(Integer disponivel) {  // 🔥 Mudar para Integer
        this.disponivel = disponivel;
    }
    
    public Double getNecessario() {
        return necessario;
    }
    
    public void setNecessario(Double necessario) {
        this.necessario = necessario;
    }
    
    public Double getFalta() {
        return falta;
    }
    
    public void setFalta(Double falta) {
        this.falta = falta;
    }
    
    public String getUnidade() {
        return unidade;
    }
    
    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }
}