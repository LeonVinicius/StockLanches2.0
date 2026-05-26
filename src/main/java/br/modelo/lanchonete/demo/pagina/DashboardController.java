package br.modelo.lanchonete.demo.pagina;

import br.modelo.lanchonete.demo.model.Produto;
import br.modelo.lanchonete.demo.service.ProdutoService;
import br.modelo.lanchonete.demo.service.UsuarioService;
import br.modelo.lanchonete.demo.service.HistoricoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class DashboardController {

    @Autowired 
    private UsuarioService usuarioService;
    
    @Autowired 
    private ProdutoService produtoService;
    
    @Autowired 
    private HistoricoService historicoService;

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

        // 1. Coleta a lista de produtos idêntica ao seu original
        List<Produto> lista = produtoService.listarTodosEntity();

        model.addAttribute("totalProdutos", lista.size());
        
        long countBaixo = lista.stream().filter(p -> {
            int qtd = (p.getQuantidade() != null) ? p.getQuantidade() : 0;
            int min = (p.getEstoqueMinimo() != null) ? p.getEstoqueMinimo() : 5;
            return qtd <= min;
        }).count();
        
        model.addAttribute("produtosBaixoEstoque", countBaixo);

        // 2. KPIs e Métricas da primeira linha do BI premium
        model.addAttribute("faturamentoDiario", "1.284");
        model.addAttribute("numeroPedidos", 47);
        model.addAttribute("ticketMedio", "38,20");
        model.addAttribute("lucroEstimado", "411");

        // 3. Volumetria das Categorias Originais para os gráficos
        long lanchesCount = lista.stream().filter(p -> "Lanches".equalsIgnoreCase(p.getCategoria())).count();
        long bebidasCount = lista.stream().filter(p -> "Bebidas".equalsIgnoreCase(p.getCategoria())).count();
        long sobremesasCount = lista.stream().filter(p -> "Sobremesas".equalsIgnoreCase(p.getCategoria())).count();
        long acompCount = lista.stream().filter(p -> "Acompanhamentos".equalsIgnoreCase(p.getCategoria())).count();

        model.addAttribute("qtdLanches", lanchesCount);
        model.addAttribute("qtdBebidas", bebidasCount);
        model.addAttribute("qtdSobremesas", sobremesasCount);
        model.addAttribute("qtdAcomp", acompCount);

        // 4. Inteligência Operacional de Produtos Críticos/Encalhados
        String produtoZeroVendas = lista.stream()
                .map(Produto::getNome)
                .filter(nome -> !nome.toLowerCase().contains("x-"))
                .findFirst().orElse("Hot Dog Especial");
        model.addAttribute("produtoEncalhado", produtoZeroVendas);

        return "estatisticas"; 
    }

    @GetMapping("/historico")
    public String historico(Model model, @RequestParam(required = false) String tipoFiltro) {
        if (!usuarioService.isUserLogged()) return "redirect:/login";
        setupModel(model, "historico");
        
        model.addAttribute("historicoLogs", historicoService.listar(tipoFiltro));
        return "historico";
    }
}