package br.modelo.lanchonete.demo.pagina;

import br.modelo.lanchonete.demo.dto.request.ItemVendaRequestDTO;
import br.modelo.lanchonete.demo.dto.response.ProdutoResponseDTO;
import br.modelo.lanchonete.demo.dto.response.VerificacaoEstoqueResponseDTO;
import br.modelo.lanchonete.demo.service.CategoriaService;
import br.modelo.lanchonete.demo.service.EstoqueService;
import br.modelo.lanchonete.demo.service.ProdutoService;
import br.modelo.lanchonete.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PdvController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private EstoqueService estoqueService;

    // ------------------------------------------------------------------
    // Abre a página do PDV
    // ------------------------------------------------------------------
    @GetMapping("/pdv")
    public String abrirPdv(Model model, @RequestParam(required = false) String busca) {
        if (!usuarioService.isUserLogged()) return "redirect:/login";

        model.addAttribute("nomeUsuario", usuarioService.getUsuarioLogado().getUsuario());
        model.addAttribute("emailUsuario", usuarioService.getUsuarioLogado().getEmail());
        model.addAttribute("activePage", "pdv");

        List<ProdutoResponseDTO> produtos = produtoService.filtrarProdutos(busca, null, null);
        model.addAttribute("produtos", produtos);
        model.addAttribute("buscaAtual", busca);
        model.addAttribute("categorias", categoriaService.listarTodas());

        return "pdv";
    }

    // ------------------------------------------------------------------
    // Endpoint chamado pelo pdv.js via fetch POST /pdv/finalizar
    // Recebe JSON com: cliente, formaPagamento, total, itens[]
    // ------------------------------------------------------------------
    @PostMapping("/pdv/finalizar")
    @ResponseBody
    public ResponseEntity<?> finalizarVenda(@RequestBody Map<String, Object> payload) {
        if (!usuarioService.isUserLogged()) {
            return ResponseEntity.status(403).body("Usuário não logado");
        }

        try {
            // Extrai a lista de itens do JSON recebido
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> itensRaw = (List<Map<String, Object>>) payload.get("itens");

            if (itensRaw == null || itensRaw.isEmpty()) {
                return ResponseEntity.badRequest().body("Carrinho vazio.");
            }

            // Converte para ItemVendaRequestDTO
            List<ItemVendaRequestDTO> itens = itensRaw.stream().map(i -> {
                ItemVendaRequestDTO dto = new ItemVendaRequestDTO();
                dto.setProdutoId(Long.valueOf(i.get("id").toString()));
                dto.setQuantidade(Integer.valueOf(i.get("quantidade").toString()));
                return dto;
            }).toList();

            // 1. Verifica disponibilidade
            VerificacaoEstoqueResponseDTO verificacao = estoqueService.verificarDisponibilidade(itens);
            if (!verificacao.isOk()) {
                return ResponseEntity.badRequest().body(verificacao);
            }

            // 2. Debita o estoque
            String usuario = usuarioService.getUsuarioLogado().getUsuario();
            estoqueService.debitarEstoque(itens, usuario);

            Map<String, Object> resposta = new HashMap<>();
            resposta.put("ok", true);
            resposta.put("mensagem", "Venda finalizada com sucesso!");
            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao processar venda: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Mantido para compatibilidade com form POST legado (opcional)
    // ------------------------------------------------------------------
    @PostMapping("/pdv/vender")
    public String processarVendaForm(@RequestParam Long produtoId,
                                     @RequestParam Integer quantidadeVenda,
                                     org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        if (!usuarioService.isUserLogged()) return "redirect:/login";

        try {
            ItemVendaRequestDTO itemVenda = new ItemVendaRequestDTO();
            itemVenda.setProdutoId(produtoId);
            itemVenda.setQuantidade(quantidadeVenda);
            List<ItemVendaRequestDTO> itens = List.of(itemVenda);

            VerificacaoEstoqueResponseDTO verificacao = estoqueService.verificarDisponibilidade(itens);
            if (!verificacao.isOk()) {
                ra.addFlashAttribute("mensagemErro", "Estoque insuficiente para concluir a venda.");
                return "redirect:/pdv";
            }

            estoqueService.debitarEstoque(itens, usuarioService.getUsuarioLogado().getUsuario());
            ra.addFlashAttribute("mensagemSucesso", "Venda realizada com sucesso!");

        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao processar venda: " + e.getMessage());
        }

        return "redirect:/pdv";
    }
}