package br.modelo.lanchonete.demo.service;

import br.modelo.lanchonete.demo.model.Categoria;
import br.modelo.lanchonete.demo.model.Produto;
import br.modelo.lanchonete.demo.repository.CategoriaRepository;
import br.modelo.lanchonete.demo.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    @Transactional
    public Categoria salvarCategoria(String nome) {
        System.out.println("[LOG SERVICE] Iniciando validações para o nome: " + nome);
        
        if (nome == null || nome.trim().isEmpty()) {
            throw new RuntimeException("O nome da categoria não pode ser vazio.");
        }
        
        String nomeTrimado = nome.trim();
        
        // 🔥 Alterado para a consulta nativa performática do Spring Data
        boolean existe = categoriaRepository.existsByNomeIgnoreCase(nomeTrimado);
        
        if (existe) {
            System.out.println("[LOG SERVICE] AVISO: A categoria '" + nomeTrimado + "' já existe!");
            throw new RuntimeException("Esta categoria já existe!");
        }
        
        System.out.println("[LOG SERVICE] Tudo válido. Enviando comando save() ao Repository...");
        return categoriaRepository.save(new Categoria(nomeTrimado));
    }

    @Transactional
    public void excluirCategoria(Long categoriaId) {
        Optional<Categoria> catOpt = categoriaRepository.findById(categoriaId);
        if (catOpt.isEmpty()) {
            throw new RuntimeException("Categoria não encontrada.");
        }

        String nomeCategoria = catOpt.get().getNome();

        // Reatribui produtos dessa categoria para "Geral"
        List<Produto> produtosAfetados = produtoRepository.findAll().stream()
                .filter(p -> nomeCategoria.equalsIgnoreCase(p.getCategoria()))
                .toList();

        for (Produto prod : produtosAfetados) {
            prod.setCategoria("Geral");
            produtoRepository.save(prod);
        }

        categoriaRepository.deleteById(categoriaId);
    }
}