package br.modelo.lanchonete.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "estoque")
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Integer qtdAtual;

    @Column(nullable = false)
    private Integer qtdMinima;

    @Column(nullable = false)
    private String unidade;

    public Estoque() {}

    public Estoque(String nome, Integer qtdAtual, Integer qtdMinima, String unidade) {
        this.nome = nome;
        this.qtdAtual = qtdAtual;
        this.qtdMinima = qtdMinima;
        this.unidade = unidade;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Integer getQtdAtual() { return qtdAtual; }
    public void setQtdAtual(Integer qtdAtual) { this.qtdAtual = qtdAtual; }

    public Integer getQtdMinima() { return qtdMinima; }
    public void setQtdMinima(Integer qtdMinima) { this.qtdMinima = qtdMinima; }

    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }
}