package br.modelo.lanchonete.demo.pagina;

import br.modelo.lanchonete.demo.model.Estoque;
import br.modelo.lanchonete.demo.repository.EstoqueRepository;
import br.modelo.lanchonete.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class EstoqueController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EstoqueRepository estoqueRepository;

    private boolean isUserLogged() {
        return usuarioService.isUserLogged();
    }

    private void setupModel(Model model, String page) {
        model.addAttribute("nomeUsuario", usuarioService.getUsuarioLogado().getUsuario());
        model.addAttribute("emailUsuario", usuarioService.getUsuarioLogado().getEmail());
        model.addAttribute("activePage", page);
    }

 // Atualize o método para ficar assim dentro do EstoqueController.java:
    @GetMapping("/consultar")
    public String consultarEstoque(Model model,
                                   @RequestParam(required = false) String busca,
                                   @RequestParam(required = false) String categoria,
                                   @RequestParam(required = false) String ordenar) {
        if (!isUserLogged()) return "redirect:/login";
        setupModel(model, "estoque");
        
        // Deixa os metadados prontos na View para o Thymeleaf e os scripts usarem
        model.addAttribute("buscaAtual", busca);
        model.addAttribute("categoriaAtual", categoria);
        model.addAttribute("ordenacaoAtual", ordenar);
        
        return "estoque";
    }

    @GetMapping("/api/estoque")
    @ResponseBody
    public ResponseEntity<List<Estoque>> listarTodosInsumos() {
        if (!isUserLogged()) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(estoqueRepository.findAll());
    }

    @PostMapping("/api/estoque/salvar")
    @ResponseBody
    public ResponseEntity<?> salvarInsumo(@RequestBody Estoque insumo) {
        if (!isUserLogged()) return ResponseEntity.status(403).body("Usuário não logado");
        
        try {
            if (insumo.getNome() == null || insumo.getNome().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("O nome do insumo é obrigatório.");
            }
            Estoque salvo = estoqueRepository.save(insumo);
            return ResponseEntity.ok(salvo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao salvar insumo: " + e.getMessage());
        }
    }

    @DeleteMapping("/api/estoque/{id}")
    @ResponseBody
    public ResponseEntity<?> excluirInsumo(@PathVariable Long id) {
        if (!isUserLogged()) return ResponseEntity.status(403).body("Usuário não logado");
        
        try {
            if (!estoqueRepository.existsById(id)) {
                return ResponseEntity.badRequest().body("Insumo não encontrado.");
            }
            estoqueRepository.deleteById(id);
            return ResponseEntity.ok("Insumo excluído com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao excluir insumo: " + e.getMessage());
        }
    }
}