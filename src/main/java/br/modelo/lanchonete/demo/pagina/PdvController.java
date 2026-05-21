package br.modelo.lanchonete.demo.pagina;

import br.modelo.lanchonete.demo.model.Produto;
import br.modelo.lanchonete.demo.repository.CategoriaRepository; // 🔥 NOVO IMPORT
import br.modelo.lanchonete.demo.service.HistoricoService;
import br.modelo.lanchonete.demo.service.ProdutoService;
import br.modelo.lanchonete.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class PdvController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private HistoricoService historicoService;

    @Autowired
    private CategoriaRepository categoriaRepository; // 🔥 ADICIONADO

    // Abre a tela do PDV com os produtos reais do banco
    @GetMapping("/pdv")
    public String abrirPdv(Model model, @RequestParam(required = false) String busca) {
        if (!usuarioService.isUserLogged()) return "redirect:/login";

        model.addAttribute("nomeUsuario", usuarioService.getUsuarioLogado().getUsuario());
        model.addAttribute("emailUsuario", usuarioService.getUsuarioLogado().getEmail());
        model.addAttribute("activePage", "pdv");

        // Permite filtrar os produtos dinamicamente na tela
        List<Produto> produtos = produtoService.filtrar(busca, null, null);
        model.addAttribute("produtos", produtos);
        model.addAttribute("buscaAtual", busca);
        
        // 🔥 ADICIONADO: Puxa as categorias do banco para as abas do PDV
        model.addAttribute("categorias", categoriaRepository.findAll());

        return "pdv";
    }

    // Processa a venda de um item e abate do estoque físico
    @PostMapping("/pdv/vender")
    public String processarVenda(@RequestParam Long produtoId, 
                                 @RequestParam Integer quantidadeVenda, 
                                 RedirectAttributes ra) {
        if (!usuarioService.isUserLogged()) return "redirect:/login";

        Produto produto = produtoService.buscarPorId(produtoId);
        
        if (produto != null) {
            if (produto.getQuantidade() >= quantidadeVenda) {
                // Abate o estoque do produto
                produto.setQuantidade(produto.getQuantidade() - quantidadeVenda);
                produtoService.atualizar(produto);

                // Registra de forma automática no seu Histórico existente
                historicoService.registrar(
                    "Saída", 
                    produto.getNome() + " (Venda PDV)", 
                    quantidadeVenda, 
                    usuarioService.getUsuarioLogado().getUsuario()
                );

                ra.addFlashAttribute("mensagemSucesso", "Venda realizada com sucesso!");
            } else {
                ra.addFlashAttribute("mensagemErro", "Estoque insuficiente para o produto: " + produto.getNome());
            }
        } else {
            ra.addFlashAttribute("mensagemErro", "Produto não encontrado.");
        }

        return "redirect:/pdv";
    }
}