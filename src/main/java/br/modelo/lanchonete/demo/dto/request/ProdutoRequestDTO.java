package br.modelo.lanchonete.demo.dto.request;

import java.util.ArrayList;
import java.util.List;

public class ProdutoRequestDTO {
    
    private Long id;
    private String nome;
    private String categoria;
    private Double precoVenda;
    private String imagemBase64;
    private Boolean usaControleEstoque = false;
    private String unidadeMedida;  // 🔥 VERIFIQUE SE ESTE CAMPO EXISTE
    private List<FichaTecnicaRequestDTO> fichaTecnica = new ArrayList<>();
    
    // Construtores
    public ProdutoRequestDTO() {}
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    public Double getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(Double precoVenda) { this.precoVenda = precoVenda; }
    
    public String getImagemBase64() { return imagemBase64; }
    public void setImagemBase64(String imagemBase64) { this.imagemBase64 = imagemBase64; }
    
    public Boolean getUsaControleEstoque() { return usaControleEstoque; }
    public void setUsaControleEstoque(Boolean usaControleEstoque) { this.usaControleEstoque = usaControleEstoque; }
    
    public String getUnidadeMedida() { return unidadeMedida; }  // 🔥 VERIFIQUE SE ESTE MÉTODO EXISTE
    public void setUnidadeMedida(String unidadeMedida) { this.unidadeMedida = unidadeMedida; }
    
    public List<FichaTecnicaRequestDTO> getFichaTecnica() { return fichaTecnica; }
    public void setFichaTecnica(List<FichaTecnicaRequestDTO> fichaTecnica) { this.fichaTecnica = fichaTecnica; }
}