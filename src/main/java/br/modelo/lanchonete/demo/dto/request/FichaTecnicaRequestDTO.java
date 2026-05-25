package br.modelo.lanchonete.demo.dto.request;

public class FichaTecnicaRequestDTO {

    private Long insumoId;

    private Double quantidade;

    private String unidade;

    public FichaTecnicaRequestDTO() {
    }

    public Long getInsumoId() {
        return insumoId;
    }

    public void setInsumoId(Long insumoId) {
        this.insumoId = insumoId;
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