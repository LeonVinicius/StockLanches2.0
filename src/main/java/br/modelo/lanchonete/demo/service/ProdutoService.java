package br.modelo.lanchonete.demo.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.modelo.lanchonete.demo.dto.request.ProdutoRequestDTO;
import br.modelo.lanchonete.demo.dto.response.InsumoResponseDTO;
import br.modelo.lanchonete.demo.dto.response.ProdutoResponseDTO;
import br.modelo.lanchonete.demo.mapper.ProdutoMapper;
import br.modelo.lanchonete.demo.model.FichaTecnica;
import br.modelo.lanchonete.demo.model.Produto;
import br.modelo.lanchonete.demo.repository.ProdutoRepository;

@Service
public class ProdutoService {

    @Autowired 
    private ProdutoRepository produtoRepository;
    
    @Autowired 
    private FichaTecnicaService fichaTecnicaService;
    
    @Autowired
    private ProdutoMapper produtoMapper;

    public List<Produto> listarTodosEntity() {
        return produtoRepository.findAll();
    }
    
    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id).orElse(null);
    }
    
    @Transactional
    public ProdutoResponseDTO salvarProduto(ProdutoRequestDTO requestDTO) {
        Produto produto;
        
        if (requestDTO.getId() != null) {
            produto = buscarPorId(requestDTO.getId());
            if (produto == null) {
                throw new RuntimeException("Produto não encontrado para edição.");
            }
            produtoMapper.updateEntity(produto, requestDTO);
        } else {
            produto = produtoMapper.toEntity(requestDTO);
        }
        
        if (Boolean.TRUE.equals(requestDTO.getUsaControleEstoque()) 
                && requestDTO.getFichaTecnica() != null 
                && !requestDTO.getFichaTecnica().isEmpty()) {
            
            if (produto.getFichaTecnica() != null) {
                produto.getFichaTecnica().clear();
            }
            
            List<FichaTecnica> novaFicha = fichaTecnicaService.processarFichaTecnica(requestDTO.getFichaTecnica());
            produto.setFichaTecnica(novaFicha);
        } else {
            if (produto.getFichaTecnica() != null) {
                produto.getFichaTecnica().clear();
            }
        }
        
        produto.atualizarStatus();
        Produto salvo = produtoRepository.save(produto);
        
        return produtoMapper.toResponseDTO(salvo);
    }
    
    @Transactional
    public void deletarProduto(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new RuntimeException("Produto não encontrado para exclusão.");
        }
        produtoRepository.deleteById(id);
    }

    public List<InsumoResponseDTO> buscarInsumosPorTermo(String termo) {
        return produtoRepository.findAll().stream()
                .filter(p -> isInsumo(p.getCategoria()))
                .filter(p -> p.getNome().toLowerCase().contains(termo.toLowerCase()))
                .map(produtoMapper::toInsumoResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ProdutoResponseDTO> filtrarProdutos(String busca, String categoria, String ordenar) {
        List<Produto> lista = produtoRepository.findAll();
        var stream = lista.stream();

        if (busca != null && !busca.isEmpty()) {
            stream = stream.filter(p -> p.getNome().toLowerCase().contains(busca.toLowerCase()));
        }

        if (categoria != null && !categoria.isEmpty() && !categoria.equals("Todas")) {
            stream = stream.filter(p -> p.getCategoria() != null && p.getCategoria().equalsIgnoreCase(categoria));
        }

        if (ordenar != null) {
            switch (ordenar) {
                case "menor_preco": 
                    stream = stream.sorted(Comparator.comparing(Produto::getPrecoVenda, Comparator.nullsLast(Double::compareTo))); 
                    break;
                case "maior_preco": 
                    stream = stream.sorted(Comparator.comparing(Produto::getPrecoVenda, Comparator.nullsLast(Double::compareTo)).reversed()); 
                    break;
                case "quantidade_baixa": 
                    stream = stream.sorted(Comparator.comparing(Produto::getQuantidade, Comparator.nullsLast(Integer::compareTo))); 
                    break;
                default: 
                    stream = stream.sorted(Comparator.comparing(Produto::getNome, Comparator.nullsLast(String::compareTo))); 
                    break;
            }
        }
        
        return stream.map(produtoMapper::toResponseDTO).collect(Collectors.toList());
    }
    
    private boolean isInsumo(String categoria) {
        if (categoria == null) return false;
        List<String> categoriasInsumo = Arrays.asList("Insumo", "Matéria-Prima", "Ingrediente", "doces");
        return categoriasInsumo.stream().anyMatch(cat -> cat.equalsIgnoreCase(categoria));
    }
}