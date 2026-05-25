package br.modelo.lanchonete.demo.dto.response;

import java.util.ArrayList;
import java.util.List;

public class ProdutoResponseDTO {
    
    private Long id;
    private String nome;
    private String categoria;
    private Double precoVenda;
    private String imagemBase64;
    private Integer quantidade;
    private String status;
    private Boolean usaControleEstoque;  // 🔥 ADICIONAR ESTE CAMPO
    private String unidadeMedida;        // 🔥 ADICIONAR ESTE CAMPO
    private List<FichaTecnicaResponseDTO> fichaTecnica = new ArrayList<>();
    
    public ProdutoResponseDTO() {}
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getCategoria() {
        return categoria;
    }
    
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    
    public Double getPrecoVenda() {
        return precoVenda;
    }
    
    public void setPrecoVenda(Double precoVenda) {
        this.precoVenda = precoVenda;
    }
    
    public String getImagemBase64() {
        return imagemBase64;
    }
    
    public void setImagemBase64(String imagemBase64) {
        this.imagemBase64 = imagemBase64;
    }
    
    public Integer getQuantidade() {
        return quantidade;
    }
    
    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Boolean getUsaControleEstoque() {  // 🔥 ADICIONAR
        return usaControleEstoque;
    }
    
    public void setUsaControleEstoque(Boolean usaControleEstoque) {  // 🔥 ADICIONAR
        this.usaControleEstoque = usaControleEstoque;
    }
    
    public String getUnidadeMedida() {  // 🔥 ADICIONAR
        return unidadeMedida;
    }
    
    public void setUnidadeMedida(String unidadeMedida) {  // 🔥 ADICIONAR
        this.unidadeMedida = unidadeMedida;
    }
    
    public List<FichaTecnicaResponseDTO> getFichaTecnica() {
        return fichaTecnica;
    }
    
    public void setFichaTecnica(List<FichaTecnicaResponseDTO> fichaTecnica) {
        this.fichaTecnica = fichaTecnica;
    }
}