package br.modelo.lanchonete.demo.dto.response;

public class FichaTecnicaResponseDTO {

    private Long id;
    private Long insumoId;
    private String insumoNome;
    private Double quantidade;
    private String unidade;

    public FichaTecnicaResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInsumoId() {
        return insumoId;
    }

    public void setInsumoId(Long insumoId) {
        this.insumoId = insumoId;
    }

    public String getInsumoNome() {
        return insumoNome;
    }

    public void setInsumoNome(String insumoNome) {
        this.insumoNome = insumoNome;
    }

    public Double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }
}