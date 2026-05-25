package br.modelo.lanchonete.demo.service;

import br.modelo.lanchonete.demo.dto.request.ItemVendaRequestDTO;
import br.modelo.lanchonete.demo.dto.response.InsumoFaltandoDTO;
import br.modelo.lanchonete.demo.dto.response.VerificacaoEstoqueResponseDTO;
import br.modelo.lanchonete.demo.model.Estoque;
import br.modelo.lanchonete.demo.model.FichaTecnica;
import br.modelo.lanchonete.demo.model.Produto;
import br.modelo.lanchonete.demo.repository.EstoqueRepository;
import br.modelo.lanchonete.demo.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class EstoqueService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private HistoricoService historicoService;

    /**
     * Debita o estoque após uma venda.
     * - Se o produto usa ficha técnica: desconta os insumos da tabela Estoque.
     * - Se o produto é simples: desconta a quantidade diretamente da tabela Produtos.
     */
    @Transactional
    public void debitarEstoque(List<ItemVendaRequestDTO> itensVenda, String usuarioLogado) {
        for (ItemVendaRequestDTO item : itensVenda) {
            Produto produto = produtoRepository.findById(item.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não localizado: " + item.getProdutoId()));

            if (produto.isUsaControleEstoque()
                    && produto.getFichaTecnica() != null
                    && !produto.getFichaTecnica().isEmpty()) {

                // --- Produto com ficha técnica: debita insumos do Estoque ---
                for (FichaTecnica vinculo : produto.getFichaTecnica()) {
                    Estoque insumo = vinculo.getInsumo();
                    if (insumo != null) {
                        int totalAtual = insumo.getQtdAtual() != null ? insumo.getQtdAtual() : 0;
                        double qtdUsada = vinculo.getQuantidade() * item.getQuantidade();
                        int novaQtd = (int) Math.max(0, totalAtual - qtdUsada);

                        insumo.setQtdAtual(novaQtd);
                        estoqueRepository.save(insumo);

                        historicoService.registrar(
                                "Saída",
                                produto.getNome() + " - " + insumo.getNome(),
                                (int) qtdUsada,
                                usuarioLogado
                        );
                    }
                }

            } else {
                // --- Produto simples: debita quantidade da tabela produtos ---
                int qtdAtual = produto.getQuantidade() != null ? produto.getQuantidade() : 0;
                int novaQtd = Math.max(0, qtdAtual - item.getQuantidade());
                produto.setQuantidade(novaQtd); // setQuantidade já chama atualizarStatus()
                produtoRepository.save(produto);

                historicoService.registrar(
                        "Saída",
                        produto.getNome(),
                        item.getQuantidade(),
                        usuarioLogado
                );
            }
        }
    }

    /**
     * Verifica se há estoque suficiente antes de confirmar a venda.
     * - Produto com ficha técnica: verifica insumos.
     * - Produto simples: verifica quantidade em produtos.
     */
    public VerificacaoEstoqueResponseDTO verificarDisponibilidade(List<ItemVendaRequestDTO> itensVenda) {
        VerificacaoEstoqueResponseDTO resultado = new VerificacaoEstoqueResponseDTO();
        resultado.setOk(true);
        resultado.setFaltando(new ArrayList<>());

        // Acumula necessário por insumo (para ficha técnica)
        Map<Long, Double> necessarioPorInsumo = new HashMap<>();

        for (ItemVendaRequestDTO item : itensVenda) {
            Produto produto = produtoRepository.findById(item.getProdutoId()).orElse(null);
            if (produto == null) continue;

            if (produto.isUsaControleEstoque()
                    && produto.getFichaTecnica() != null
                    && !produto.getFichaTecnica().isEmpty()) {

                // Acumula insumos necessários
                for (FichaTecnica vinculo : produto.getFichaTecnica()) {
                    Estoque insumo = vinculo.getInsumo();
                    if (insumo != null) {
                        double qtdNecessaria = vinculo.getQuantidade() * item.getQuantidade();
                        necessarioPorInsumo.merge(insumo.getId(), qtdNecessaria, Double::sum);
                    }
                }

            } else {
                // Produto simples: verifica direto
                int disponivel = produto.getQuantidade() != null ? produto.getQuantidade() : 0;
                if (disponivel < item.getQuantidade()) {
                    resultado.setOk(false);
                    InsumoFaltandoDTO falta = new InsumoFaltandoDTO();
                    falta.setInsumoNome(produto.getNome());
                    falta.setDisponivel(disponivel);
                    falta.setNecessario(item.getQuantidade().doubleValue());
                    falta.setFalta((double) (item.getQuantidade() - disponivel));
                    falta.setUnidade(produto.getUnidadeMedida() != null ? produto.getUnidadeMedida() : "un");
                    resultado.addFaltando(falta);
                }
            }
        }

        // Verifica insumos acumulados (ficha técnica)
        for (Map.Entry<Long, Double> entry : necessarioPorInsumo.entrySet()) {
            Estoque insumo = estoqueRepository.findById(entry.getKey()).orElse(null);
            if (insumo != null) {
                int disponivel = insumo.getQtdAtual() != null ? insumo.getQtdAtual() : 0;
                if (disponivel < entry.getValue()) {
                    resultado.setOk(false);
                    InsumoFaltandoDTO falta = new InsumoFaltandoDTO();
                    falta.setInsumoNome(insumo.getNome());
                    falta.setDisponivel(disponivel);
                    falta.setNecessario(entry.getValue());
                    falta.setFalta(entry.getValue() - disponivel);
                    falta.setUnidade(insumo.getUnidade());
                    resultado.addFaltando(falta);
                }
            }
        }

        return resultado;
    }
}