package br.modelo.lanchonete.demo.dto.request;

public class ItemVendaRequestDTO {

    private Long produtoId;

    private Integer quantidade;

    public ItemVendaRequestDTO() {
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}