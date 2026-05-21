package br.modelo.lanchonete.demo.service;

import br.modelo.lanchonete.demo.model.Produto;
import br.modelo.lanchonete.demo.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository; 

    public List<Produto> listarTodos() {
        return produtoRepository.findAll(); 
    }

    public void adicionar(Produto produto) {
        produto.atualizarStatus();
        produtoRepository.save(produto); 
    }

    public void atualizar(Produto produtoEditado) {
        produtoEditado.atualizarStatus();
        produtoRepository.save(produtoEditado); 
    }
    
    public Produto buscarPrimeiroPorNome(String nome) {
        return produtoRepository.findAll().stream()
                .filter(p -> p.getNome().toLowerCase().contains(nome.toLowerCase()))
                .findFirst()
                .orElse(null);
    }
    
    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id).orElse(null);
    }

    public List<Produto> filtrar(String busca, String categoria, String ordenar) {
        List<Produto> lista = produtoRepository.findAll();
        var stream = lista.stream();

        if (busca != null && !busca.isEmpty()) {
            stream = stream.filter(p -> p.getNome().toLowerCase().contains(busca.toLowerCase()));
        }

        if (categoria != null && !categoria.isEmpty() && !categoria.equals("Todas")) {
            stream = stream.filter(p -> p.getCategoria().equalsIgnoreCase(categoria));
        }

        if (ordenar != null) {
            switch (ordenar) {
                case "menor_preco": stream = stream.sorted(Comparator.comparing(Produto::getPrecoVenda)); break;
                case "maior_preco": stream = stream.sorted(Comparator.comparing(Produto::getPrecoVenda).reversed()); break;
                case "quantidade_baixa": stream = stream.sorted(Comparator.comparing(Produto::getQuantidade)); break;
                default: stream = stream.sorted(Comparator.comparing(Produto::getNome)); break;
            }
        }
        return stream.collect(Collectors.toList());
    }

    public void remover(Long id) {
        produtoRepository.deleteById(id);
    }
    public void validarFichaTecnica(Produto produto) {
        if (produto.getFichaTecnica() == null || produto.getFichaTecnica().isEmpty()) {
            throw new RuntimeException("O produto precisa ter pelo menos um ingrediente vinculado!");
        }
    }
}