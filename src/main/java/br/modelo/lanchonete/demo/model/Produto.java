package br.modelo.lanchonete.demo.model;

import jakarta.persistence.*;
import jakarta.persistence.FetchType;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String categoria;
    private Integer quantidade;
    private Double precoCusto;
    private Double precoVenda;
    
    @Column(columnDefinition = "TEXT")
    private String descricao;
    
    private Integer estoqueMinimo;
    private String fornecedor;
    private String status;
    
    // 🔥 CORRIGIDO: Adicionada anotação @Column
    @Column(name = "usa_controle_estoque")
    private boolean usaControleEstoque = false; // Toggle para ativar/desativar a ficha

    // 🔥 NOVO CAMPO: Imagem em Base64
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String imagemBase64;

    // Relacionamento com a Ficha Técnica
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "produto_id")
    private List<FichaTecnica> fichaTecnica = new ArrayList<>();

    // Construtores
    public Produto() {}

    // Getters e Setters manuais
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; this.atualizarStatus(); }

    public Double getPrecoCusto() { return precoCusto; }
    public void setPrecoCusto(Double precoCusto) { this.precoCusto = precoCusto; }

    public Double getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(Double precoVenda) { this.precoVenda = precoVenda; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Integer getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(Integer estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; this.atualizarStatus(); }

    public String getFornecedor() { return fornecedor; }
    public void setFornecedor(String fornecedor) { this.fornecedor = fornecedor; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // 🔥 GETTER E SETTER PARA O NOVO CAMPO imagemBase64
    public String getImagemBase64() { return imagemBase64; }
    public void setImagemBase64(String imagemBase64) { this.imagemBase64 = imagemBase64; }
    
 // 🔥 GETTER E SETTER PARA usaControleEstoque
    public boolean isUsaControleEstoque() { return usaControleEstoque; }
    public void setUsaControleEstoque(boolean usaControleEstoque) { this.usaControleEstoque = usaControleEstoque; }

    public List<FichaTecnica> getFichaTecnica() { return fichaTecnica; }
    public void setFichaTecnica(List<FichaTecnica> fichaTecnica) { this.fichaTecnica = fichaTecnica; }

    // Lógica de Status manual
    public void atualizarStatus() {
        if (this.quantidade == null || this.quantidade == 0) {
            this.status = "Esgotado";
        } else if (this.estoqueMinimo != null && this.quantidade <= this.estoqueMinimo) {
            this.status = "Baixo";
        } else {
            this.status = "Normal";
        }
    }
}