package br.modelo.lanchonete.demo.pagina;

import br.modelo.lanchonete.demo.model.Produto;
import br.modelo.lanchonete.demo.service.ProdutoService;
import br.modelo.lanchonete.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class DashboardController {

    @Autowired private UsuarioService usuarioService;
    @Autowired private ProdutoService produtoService;

    private void setupModel(Model model, String page) {
        model.addAttribute("nomeUsuario", usuarioService.getUsuarioLogado().getUsuario());
        model.addAttribute("emailUsuario", usuarioService.getUsuarioLogado().getEmail());
        model.addAttribute("activePage", page);
    }

    @GetMapping("/configuracoes")
    public String configuracoes(Model model) {
        if (!usuarioService.isUserLogged()) return "redirect:/login";
        setupModel(model, "configuracoes");
        return "configuracoes";
    }

    @GetMapping("/estatisticas")
    public String estatisticas(Model model) {
        if (!usuarioService.isUserLogged()) return "redirect:/login";
        setupModel(model, "estatisticas");

        List<Produto> lista = produtoService.listarTodos();

        // Dados para os cards com tratamento de NullPointerException
        model.addAttribute("totalProdutos", lista.size());
        
        long countBaixo = lista.stream().filter(p -> {
            int qtd = (p.getQuantidade() != null) ? p.getQuantidade() : 0;
            int min = (p.getEstoqueMinimo() != null) ? p.getEstoqueMinimo() : 5;
            return qtd <= min;
        }).count();
        
        model.addAttribute("produtosBaixoEstoque", countBaixo);
        model.addAttribute("faturamentoDiario", "1.240,00");

        // Contagem de categorias segura
        model.addAttribute("qtdLanches", lista.stream().filter(p -> "Lanches".equalsIgnoreCase(p.getCategoria())).count());
        model.addAttribute("qtdBebidas", lista.stream().filter(p -> "Bebidas".equalsIgnoreCase(p.getCategoria())).count());
        model.addAttribute("qtdSobremesas", lista.stream().filter(p -> "Sobremesas".equalsIgnoreCase(p.getCategoria())).count());
        model.addAttribute("qtdAcomp", lista.stream().filter(p -> "Acompanhamentos".equalsIgnoreCase(p.getCategoria())).count());

        return "estatisticas"; 
    }
}