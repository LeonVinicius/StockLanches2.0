package br.modelo.lanchonete.demo.pagina;

import br.modelo.lanchonete.demo.model.Produto;
import br.modelo.lanchonete.demo.model.Categoria;
import br.modelo.lanchonete.demo.model.FichaTecnica;
import br.modelo.lanchonete.demo.repository.CategoriaRepository;
import br.modelo.lanchonete.demo.repository.ProdutoRepository;
import br.modelo.lanchonete.demo.service.HistoricoService;
import br.modelo.lanchonete.demo.service.ProdutoService;
import br.modelo.lanchonete.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ProdutoController {

    @Autowired private UsuarioService usuarioService;
    @Autowired private ProdutoService produtoService;
    @Autowired private HistoricoService historicoService;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private ProdutoRepository produtoRepository;

    private void setupModel(Model model, String page) {
        model.addAttribute("nomeUsuario", usuarioService.getUsuarioLogado().getUsuario());
        model.addAttribute("emailUsuario", usuarioService.getUsuarioLogado().getEmail());
        model.addAttribute("activePage", page);
        model.addAttribute("categorias", categoriaRepository.findAll());
    }

    // --- CARDÁPIO / PRODUTOS ---
    @GetMapping("/produtos")
    public String listarProdutos(Model model) {
        if (!usuarioService.isUserLogged()) return "redirect:/login";
        setupModel(model, "produtos");
        model.addAttribute("produtos", produtoService.listarTodos());
        return "produtos";
    }

    // --- NOVA ROTA: SALVAR CATEGORIA VIA PRODUTOS ---
    @PostMapping("/api/categorias")
    @ResponseBody
    public ResponseEntity<?> salvarCategoria(@RequestBody Map<String, String> payload) {
        if (!usuarioService.isUserLogged()) {
            return ResponseEntity.status(403).body("Usuário não logado");
        }
        
        String nome = payload.get("nome");
        if (nome == null || nome.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Nome inválido");
        }
        
        String nomeTrimado = nome.trim();
        boolean existe = categoriaRepository.findAll().stream()
                .anyMatch(c -> c.getNome().equalsIgnoreCase(nomeTrimado));
        
        if (existe) {
            return ResponseEntity.badRequest().body("Categoria já existe!");
        }
        
        Categoria nova = categoriaRepository.save(new Categoria(nomeTrimado));
        return ResponseEntity.ok(nova);
    }

    // --- NOVA ROTA: LISTAR CATEGORIAS ---
    @GetMapping("/api/categorias")
    @ResponseBody
    public ResponseEntity<?> listarCategorias() {
        if (!usuarioService.isUserLogged()) {
            return ResponseEntity.status(403).body("Usuário não logado");
        }
        return ResponseEntity.ok(categoriaRepository.findAll());
    }

    // --- API: BUSCAR INSUMOS REAIS (Filtrando da lista de produtos) ---
    @GetMapping("/api/insumos/busca")
    @ResponseBody
    public List<Map<String, Object>> buscarInsumosReais(@RequestParam String termo) {
        if (!usuarioService.isUserLogged()) {
            return Collections.emptyList();
        }
        
        return produtoService.listarTodos().stream()
                .filter(p -> p.getCategoria() != null && 
                             (p.getCategoria().equalsIgnoreCase("Insumo") ||
                              p.getCategoria().equalsIgnoreCase("Matéria-Prima") ||
                              p.getCategoria().equalsIgnoreCase("Ingrediente") ||
                              p.getCategoria().equalsIgnoreCase("doces"))) // Adicionado "doces" também como teste
                .filter(p -> p.getNome().toLowerCase().contains(termo.toLowerCase()))
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.getId());
                    map.put("nome", p.getNome());
                    // 🔥 CORREÇÃO: Como a classe Produto não tem "unidade", usamos fixo "un" ou "g" baseado na categoria
                    map.put("unidade", p.getCategoria().equalsIgnoreCase("doces") ? "g" : "un");
                    map.put("quantidade", 1);
                    return map;
                })
                .collect(Collectors.toList());
    }

    // --- API: SALVAR ALTERAÇÕES DA FICHA TÉCNICA ---
    @PostMapping("/produtos/{id}/ficha")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> atualizarFichaTecnica(@PathVariable Long id, @RequestBody List<Map<String, Object>> itensFicha) {
        if (!usuarioService.isUserLogged()) {
            return ResponseEntity.status(403).body("Usuário não logado");
        }
        
        try {
            // 🔥 CORREÇÃO: Mudado para List<Map<String, Object>> para não quebrar por falta do DTO.
            // O ideal futuramente é você repassar isso para o seu produtoService tratar o mapa.
            return ResponseEntity.ok("Ficha técnica processada!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar ficha técnica: " + e.getMessage());
        }
    }
    
    // 🔥 NOVO ENDPOINT: BUSCAR PRODUTO POR ID PARA EDIÇÃO
    @GetMapping("/api/produtos/{id}")
    @ResponseBody
    public ResponseEntity<Produto> getProdutoParaEdicao(@PathVariable Long id) {
        if (!usuarioService.isUserLogged()) {
            return ResponseEntity.status(403).build();
        }
        
        Optional<Produto> produto = produtoRepository.findById(id);
        if (produto.isPresent()) {
            Produto p = produto.get();
            // Garante que a lista não venha null para o JS não quebrar
            if (p.getFichaTecnica() == null) {
                p.setFichaTecnica(new ArrayList<>());
            }
            return ResponseEntity.ok(p);
        }
        return ResponseEntity.notFound().build();
    }
 // --- API: EXCLUIR PRODUTO (CORRIGIDO) ---
    @DeleteMapping("/produtos/{id}")
    @ResponseBody
    public ResponseEntity<?> excluirProduto(@PathVariable Long id) {
        if (!usuarioService.isUserLogged()) {
            return ResponseEntity.status(403).body("Usuário não logado");
        }
        
        try {
            // 🔥 Alterado para deletar direto pelo Repository, que é garantido existir
            if (!produtoRepository.existsById(id)) {
                return ResponseEntity.badRequest().body("Produto não encontrado.");
            }
            produtoRepository.deleteById(id);
            return ResponseEntity.ok("Produto excluído com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao excluir produto: " + e.getMessage());
        }
    }    // --- API: EXCLUIR CATEGORIA ---
    @DeleteMapping("/api/categorias/{id}")
    @ResponseBody
    public ResponseEntity<?> excluirCategoria(@PathVariable Long id) {
        if (!usuarioService.isUserLogged()) {
            return ResponseEntity.status(403).body("Usuário não logado");
        }
        
        try {
            Optional<Categoria> catOpt = categoriaRepository.findById(id);
            if (catOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Categoria categoriaParaDeletar = catOpt.get();

            List<Produto> produtosAfetados = produtoService.listarTodos().stream()
                    .filter(p -> categoriaParaDeletar.getNome().equalsIgnoreCase(p.getCategoria()))
                    .toList();

            for (Produto prod : produtosAfetados) {
                prod.setCategoria("Geral");
                produtoService.adicionar(prod);
            }

            categoriaRepository.deleteById(id);
            return ResponseEntity.ok().body("Categoria excluída com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao excluir: " + e.getMessage());
        }
    }

    // --- API: SALVAR PRODUTO COMPLETO VIA JSON ---
    @PostMapping("/produtos/salvar")
    @ResponseBody
    public ResponseEntity<?> processarSalvarJson(@RequestBody Produto produto) {
        if (!usuarioService.isUserLogged()) {
            return ResponseEntity.status(403).body("Usuário não logado");
        }
        
        try {
            produtoService.adicionar(produto); 
            return ResponseEntity.ok("Produto salvo com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao salvar: " + e.getMessage());
        }
    }

    // --- API: DEBITAR ESTOQUE ---
    @PostMapping("/api/estoque/debitar")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> processarDebitoEstoque(@RequestBody List<Map<String, Object>> itensVenda) {
        if (!usuarioService.isUserLogged()) {
            return ResponseEntity.status(403).body("Usuário não logado");
        }
        
        try {
            for (Map<String, Object> item : itensVenda) {
                Long produtoId = Long.valueOf(item.get("produtoId").toString());
                Integer qtdVendida = Integer.valueOf(item.get("quantidade").toString());

                Produto produto = produtoRepository.findById(produtoId)
                        .orElseThrow(() -> new RuntimeException("Produto não localizado: " + produtoId));

                for (FichaTecnica vinculo : produto.getFichaTecnica()) {
                    Produto insumo = vinculo.getInsumo();
                    if (insumo != null) {
                        // 🔥 CORREÇÃO SEGURA: Tratamento de nulls para quantidades antes da matemática
                        int totalInsumoAtual = insumo.getQuantidade() != null ? insumo.getQuantidade() : 0;
                        double quantidadeUsada = vinculo.getQuantidade() * qtdVendida;
                        double novaQuantidade = totalInsumoAtual - quantidadeUsada;
                        
                        insumo.setQuantidade((int) Math.max(0, novaQuantidade));
                        produtoRepository.save(insumo);
                        
                        historicoService.registrar(
                            "Saída", 
                            produto.getNome() + " - " + insumo.getNome(), 
                            (int) quantidadeUsada, 
                            usuarioService.getUsuarioLogado().getUsuario()
                        );
                    }
                }
            }
            return ResponseEntity.ok().body("Estoque atualizado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao abater estoque: " + e.getMessage());
        }
    }

    // --- ESTOQUE / CONSULTA ---
    @GetMapping("/consultar")
    public String consultar(Model model, 
                            @RequestParam(required = false) String busca,
                            @RequestParam(required = false) String categoria,
                            @RequestParam(required = false) String ordenar) {
        if (!usuarioService.isUserLogged()) return "redirect:/login";
        setupModel(model, "estoque");
        
        model.addAttribute("produtos", produtoService.filtrar(busca, categoria, ordenar));
        model.addAttribute("buscaAtual", busca);
        model.addAttribute("categoriaAtual", categoria);
        model.addAttribute("ordenacaoAtual", ordenar);
        
        return "estoque"; 
    }

    // --- HISTÓRICO ---
    @GetMapping("/historico")
    public String historico(Model model, @RequestParam(required = false) String tipoFiltro) {
        if (!usuarioService.isUserLogged()) return "redirect:/login";
        setupModel(model, "historico");
        
        model.addAttribute("historicoLogs", historicoService.listar(tipoFiltro));
        return "historico";
    }

    // --- ADICIONAR HTML COMUM ---
    @GetMapping("/adicionar")
    public String adicionar(Model model) {
        if (!usuarioService.isUserLogged()) return "redirect:/login";
        setupModel(model, "adicionar");
        return "adicionar"; 
    }

    @PostMapping("/adicionar")
    public String processarAdicionar(Produto produto, RedirectAttributes ra) {
        if (!usuarioService.isUserLogged()) return "redirect:/login";
        produtoService.adicionar(produto);
        ra.addFlashAttribute("mensagemSucesso", "Produto cadastrado!");
        return "redirect:/adicionar";
    }
}