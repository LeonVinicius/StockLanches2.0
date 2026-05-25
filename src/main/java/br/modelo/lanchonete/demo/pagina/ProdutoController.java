package br.modelo.lanchonete.demo.pagina;

import br.modelo.lanchonete.demo.dto.request.ItemVendaRequestDTO;
import br.modelo.lanchonete.demo.dto.request.ProdutoRequestDTO;
import br.modelo.lanchonete.demo.dto.response.InsumoResponseDTO;
import br.modelo.lanchonete.demo.dto.response.ProdutoResponseDTO;
import br.modelo.lanchonete.demo.dto.response.VerificacaoEstoqueResponseDTO;
import br.modelo.lanchonete.demo.model.Categoria;
import br.modelo.lanchonete.demo.repository.EstoqueRepository;
import br.modelo.lanchonete.demo.service.ProdutoService;
import br.modelo.lanchonete.demo.service.UsuarioService;
import br.modelo.lanchonete.demo.service.CategoriaService;
import br.modelo.lanchonete.demo.service.EstoqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
public class ProdutoController {

    @Autowired 
    private UsuarioService usuarioService;
    
    @Autowired 
    private ProdutoService produtoService;
    
    @Autowired 
    private CategoriaService categoriaService;
    
    @Autowired 
    private EstoqueService estoqueService;
    
    @Autowired 
    private EstoqueRepository estoqueRepository;

    private void setupModel(Model model, String page) {
        model.addAttribute("nomeUsuario", usuarioService.getUsuarioLogado().getUsuario());
        model.addAttribute("emailUsuario", usuarioService.getUsuarioLogado().getEmail());
        model.addAttribute("activePage", page);
        model.addAttribute("categorias", categoriaService.listarTodas());
    }
    
    private boolean isUserLogged() {
        return usuarioService.isUserLogged();
    }

    // ==========================================================================
    // VIEWS (PÁGINAS HTML)
    // ==========================================================================
    
    @GetMapping("/produtos")
    public String listarProdutos(Model model) {
        if (!isUserLogged()) return "redirect:/login";
        setupModel(model, "produtos");
        model.addAttribute("produtos", produtoService.listarTodosEntity());
        return "produtos";
    }
    
    @GetMapping("/adicionar")
    public String adicionar(Model model) {
        if (!isUserLogged()) return "redirect:/login";
        setupModel(model, "adicionar");
        return "adicionar"; 
    }
    
    @PostMapping("/adicionar")
    public String processarAdicionar(ProdutoRequestDTO requestDTO, RedirectAttributes ra) {
        if (!isUserLogged()) return "redirect:/login";
        try {
            produtoService.salvarProduto(requestDTO);
            ra.addFlashAttribute("mensagemSucesso", "Produto cadastrado!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao cadastrar: " + e.getMessage());
        }
        return "redirect:/adicionar";
    }

    // ==========================================================================
    // API DE PRODUTOS
    // ==========================================================================
    
    @GetMapping("/api/produtos/{id}")
    @ResponseBody
    public ResponseEntity<ProdutoResponseDTO> getProdutoParaEdicao(@PathVariable Long id) {
        if (!isUserLogged()) return ResponseEntity.status(403).build();
        
        Optional<ProdutoResponseDTO> produto = produtoService.filtrarProdutos(null, null, null).stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
        
        if (produto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(produto.get());
    }
    
    @PostMapping("/produtos/salvar")
    @ResponseBody
    public ResponseEntity<?> processarSalvarJson(@RequestBody ProdutoRequestDTO requestDTO) {
        if (!isUserLogged()) {
            return ResponseEntity.status(403).body("Usuário não logado");
        }
        
        try {
            ProdutoResponseDTO produto = produtoService.salvarProduto(requestDTO);
            return ResponseEntity.ok(produto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao salvar: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/produtos/{id}")
    @ResponseBody
    public ResponseEntity<?> excluirProduto(@PathVariable Long id) {
        if (!isUserLogged()) {
            return ResponseEntity.status(403).body("Usuário não logado");
        }
        
        try {
            produtoService.deletarProduto(id);
            return ResponseEntity.ok("Produto excluído com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao excluir produto: " + e.getMessage());
        }
    }

    // ==========================================================================
    // API DE CATEGORIAS
    // ==========================================================================
    
 // ==========================================================================
    // API DE CATEGORIAS (MANTENHA APENAS ESTE MÉTODO POST LIMPO E MONITORADO)
    // ==========================================================================
    
    @GetMapping("/api/categorias")
    @ResponseBody
    public ResponseEntity<List<Categoria>> listarCategorias() {
        if (!isUserLogged()) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(categoriaService.listarTodas());
    }

    @PostMapping("/api/categorias")
    @ResponseBody
    public ResponseEntity<?> salvarCategoria(@RequestBody br.modelo.lanchonete.demo.dto.request.CategoriaRequestDTO requestDTO) {
        // 🔍 RASTREADOR DE ENTRADA NO CONSOLE
        System.out.println("\n=============================================");
        System.out.println("[LOG CONSOLE] Rota POST /api/categorias foi acionada com sucesso!");
        
        if (requestDTO == null) {
            System.out.println("[LOG CONSOLE] ERRO CRÍTICO: O objeto RequestDTO chegou NULO.");
            return ResponseEntity.badRequest().body("Payload nulo");
        }
        
        System.out.println("[LOG CONSOLE] Payload mapeado -> Nome recebido: '" + requestDTO.getNome() + "'");
        System.out.println("=============================================\n");
        
        if (!usuarioService.isUserLogged()) {
            System.out.println("[LOG CONSOLE] BLOQUEADO: Sessão de usuário não identificada.");
            return ResponseEntity.status(403).body("Usuário não logado");
        }
        
        try {
            Categoria nova = categoriaService.salvarCategoria(requestDTO.getNome());
            System.out.println("[LOG CONSOLE] SUCESSO NO BANCO: Categoria '" + nova.getNome() + "' gravada com ID: " + nova.getId());
            return ResponseEntity.ok(nova);
        } catch (Exception e) {
            System.out.println("[LOG CONSOLE] EXCEÇÃO REJEITADA NO SERVICE: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // ==========================================================================
    // API DE INSUMOS (BUSCA NA TABELA DE ESTOQUE)
    // ==========================================================================
    
    @GetMapping("/api/insumos/busca")
    @ResponseBody
    public List<InsumoResponseDTO> buscarInsumos(@RequestParam String termo) {
        if (!isUserLogged() || termo == null || termo.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        return estoqueRepository.findAll().stream()
                .filter(i -> i.getNome().toLowerCase().contains(termo.toLowerCase()))
                .map(i -> {
                    InsumoResponseDTO dto = new InsumoResponseDTO();
                    dto.setId(i.getId());
                    dto.setNome(i.getNome());
                    dto.setQuantidade(i.getQtdAtual() != null ? i.getQtdAtual().doubleValue() : 0.0);
                    dto.setUnidade(i.getUnidade());
                    return dto;
                })
                .toList();
    }

    // ==========================================================================
    // API DE ESTOQUE (VENDAS)
    // ==========================================================================
    
    @PostMapping("/api/estoque/debitar")
    @ResponseBody
    public ResponseEntity<?> processarDebitoEstoque(@RequestBody List<ItemVendaRequestDTO> itensVenda) {
        if (!isUserLogged()) {
            return ResponseEntity.status(403).body("Usuário não logado");
        }
        
        try {
            VerificacaoEstoqueResponseDTO verificacao = estoqueService.verificarDisponibilidade(itensVenda);
            if (!verificacao.isOk()) {
                return ResponseEntity.badRequest().body(verificacao);
            }
            
            estoqueService.debitarEstoque(itensVenda, usuarioService.getUsuarioLogado().getUsuario());
            
            Map<String, Object> response = new HashMap<>();
            response.put("ok", true);
            response.put("message", "Estoque atualizado com sucesso!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao abater estoque: " + e.getMessage());
        }
    }
}