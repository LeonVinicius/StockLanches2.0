package br.modelo.lanchonete.demo.model;

import jakarta.persistence.*;

@Entity
public class FichaTecnica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "insumo_id")
    private Produto insumo; 

    private Double quantidade;
    private String unidade;

    public FichaTecnica() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Produto getInsumo() { return insumo; }
    public void setInsumo(Produto insumo) { this.insumo = insumo; }

    public Double getQuantidade() { return quantidade; }
    public void setQuantidade(Double quantidade) { this.quantidade = quantidade; }

    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }
}