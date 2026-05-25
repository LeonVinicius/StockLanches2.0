package br.modelo.lanchonete.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ficha_tecnica")
public class FichaTecnica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "insumo_id")
    private Estoque insumo;

    private Double quantidade;
    private String unidade;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Estoque getInsumo() { return insumo; }
    public void setInsumo(Estoque insumo) { this.insumo = insumo; }

    public Double getQuantidade() { return quantidade; }
    public void setQuantidade(Double quantidade) { this.quantidade = quantidade; }

    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }
}