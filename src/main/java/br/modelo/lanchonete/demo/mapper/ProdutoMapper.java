package br.modelo.lanchonete.demo.mapper;

import br.modelo.lanchonete.demo.dto.request.FichaTecnicaRequestDTO;
import br.modelo.lanchonete.demo.dto.request.ProdutoRequestDTO;
import br.modelo.lanchonete.demo.dto.response.FichaTecnicaResponseDTO;
import br.modelo.lanchonete.demo.dto.response.InsumoResponseDTO;
import br.modelo.lanchonete.demo.dto.response.ProdutoResponseDTO;
import br.modelo.lanchonete.demo.model.FichaTecnica;
import br.modelo.lanchonete.demo.model.Produto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProdutoMapper {
    
    public Produto toEntity(ProdutoRequestDTO dto) {
        if (dto == null) return null;
        
        Produto produto = new Produto();
        produto.setId(dto.getId());
        produto.setNome(dto.getNome());
        produto.setCategoria(dto.getCategoria());
        produto.setPrecoVenda(dto.getPrecoVenda());
        produto.setImagemBase64(dto.getImagemBase64());
        
        if (dto.getUsaControleEstoque() != null) {
            produto.setUsaControleEstoque(dto.getUsaControleEstoque());
        }
        
        if (dto.getUnidadeMedida() != null) {
            produto.setUnidadeMedida(dto.getUnidadeMedida());
        }
        
        return produto;
    }
    
    public void updateEntity(Produto produto, ProdutoRequestDTO dto) {
        if (dto == null) return;
        
        produto.setNome(dto.getNome());
        produto.setCategoria(dto.getCategoria());
        produto.setPrecoVenda(dto.getPrecoVenda());
        produto.setImagemBase64(dto.getImagemBase64());
        
        if (dto.getUsaControleEstoque() != null) {
            produto.setUsaControleEstoque(dto.getUsaControleEstoque());
        }
        
        if (dto.getUnidadeMedida() != null) {
            produto.setUnidadeMedida(dto.getUnidadeMedida());
        }
    }
    
    public ProdutoResponseDTO toResponseDTO(Produto produto) {
        if (produto == null) return null;
        
        ProdutoResponseDTO dto = new ProdutoResponseDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setCategoria(produto.getCategoria());
        dto.setPrecoVenda(produto.getPrecoVenda());
        dto.setImagemBase64(produto.getImagemBase64());
        dto.setQuantidade(produto.getQuantidade());
        dto.setStatus(produto.getStatus());
        dto.setUsaControleEstoque(produto.isUsaControleEstoque());
        dto.setUnidadeMedida(produto.getUnidadeMedida());
        
        if (produto.getFichaTecnica() != null && !produto.getFichaTecnica().isEmpty()) {
            List<FichaTecnicaResponseDTO> fichaDTO = produto.getFichaTecnica().stream()
                .map(this::toFichaTecnicaResponseDTO)
                .collect(Collectors.toList());
            dto.setFichaTecnica(fichaDTO);
        }
        
        return dto;
    }
    
    public FichaTecnicaResponseDTO toFichaTecnicaResponseDTO(FichaTecnica ft) {
        if (ft == null) return null;
        
        FichaTecnicaResponseDTO dto = new FichaTecnicaResponseDTO();
        dto.setId(ft.getId());
        dto.setQuantidade(ft.getQuantidade());
        dto.setUnidade(ft.getUnidade());
        
        if (ft.getInsumo() != null) {
            dto.setInsumoId(ft.getInsumo().getId());
            dto.setInsumoNome(ft.getInsumo().getNome());
        }
        
        return dto;
    }
    
    public InsumoResponseDTO toInsumoResponseDTO(Produto produto) {
        if (produto == null) return null;
        
        InsumoResponseDTO dto = new InsumoResponseDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        
        if (produto.getQuantidade() != null) {
            dto.setQuantidade(produto.getQuantidade().doubleValue());
        } else {
            dto.setQuantidade(0.0);
        }
        
        if (produto.getUnidadeMedida() != null) {
            dto.setUnidade(produto.getUnidadeMedida());
        } else {
            dto.setUnidade("un");
        }
        
        return dto;
    }
}