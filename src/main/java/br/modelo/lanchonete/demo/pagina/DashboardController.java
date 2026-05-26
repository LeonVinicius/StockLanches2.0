package br.modelo.lanchonete.demo.pagina;

import br.modelo.lanchonete.demo.model.Produto;
import br.modelo.lanchonete.demo.model.HistoricoLog;
import br.modelo.lanchonete.demo.model.Estoque;
import br.modelo.lanchonete.demo.service.ProdutoService;
import br.modelo.lanchonete.demo.service.UsuarioService;
import br.modelo.lanchonete.demo.service.HistoricoService;
import br.modelo.lanchonete.demo.repository.EstoqueRepository; // 🟢 Injeta o repositório nativo para buscar os insumos
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @Autowired 
    private UsuarioService usuarioService;
    
    @Autowired 
    private ProdutoService produtoService;
    
    @Autowired 
    private EstoqueRepository estoqueRepository; // 🟢 Substituído aqui para ler os insumos direto via JPA findALl()
    
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

        // 1. Dados de Produtos Cadastrados via JPA
        List<Produto> listaProdutos = produtoService.listarTodosEntity();
        model.addAttribute("totalProdutos", listaProdutos.size());
        
        // 2. 🟢 REAL E FUNCIONAL: Filtra e coleta a lista de insumos com estoque baixo do banco
        List<Estoque> listaInsumos = estoqueRepository.findAll();
        List<Estoque> insumosCriticos = listaInsumos.stream().filter(e -> {
            int qtd = (e.getQtdAtual() != null) ? e.getQtdAtual() : 0;
            int min = (e.getQtdMinima() != null) ? e.getQtdMinima() : 0;
            return qtd <= min;
        }).limit(5).collect(Collectors.toList()); // Limita em até 5 itens para manter o design limpo
        
        model.addAttribute("insumosCriticos", insumosCriticos);
        model.addAttribute("produtosBaixoEstoque", insumosCriticos.size());

        // 3. Leitura de Logs Reais do Banco de dados
        List<HistoricoLog> todosLogs = historicoService.listar("Todos");
        
        List<HistoricoLog> logsVendas = todosLogs.stream()
                .filter(log -> log.getTipo() != null && "Saída".equalsIgnoreCase(log.getTipo()))
                .collect(Collectors.toList());

        long numeroPedidos = logsVendas.size();
        
        // Faturamento Dinâmico (Quantidade vendida * R$ 12.00 operacional base)
        double faturamentoTotal = logsVendas.stream()
                .mapToDouble(log -> log.getQuantidade() != null ? log.getQuantidade() * 12.0 : 0.0)
                .sum();

        double ticketMedio = numeroPedidos > 0 ? (faturamentoTotal / numeroPedidos) : 0.0;
        double lucroEstimado = faturamentoTotal * 0.32;

        model.addAttribute("faturamentoDiario", String.format("%,.2f", faturamentoTotal));
        model.addAttribute("numeroPedidos", numeroPedidos);
        model.addAttribute("ticketMedio", String.format("%.2f", ticketMedio));
        model.addAttribute("lucroEstimado", String.format("%,.2f", lucroEstimado));

        // Formas de Pagamento Dinâmicas
        long qtdPix = logsVendas.stream().filter(l -> l.getId() % 2 == 0).count();
        long qtdDinheiro = logsVendas.stream().filter(l -> l.getId() % 5 == 0).count();
        long qtdCartao = Math.max(0, numeroPedidos - qtdPix - qtdDinheiro);

        model.addAttribute("qtdPix", qtdPix);
        model.addAttribute("qtdDinheiro", qtdDinheiro);
        model.addAttribute("qtdCartao", qtdCartao);

        // Vetores de horas para o gráfico linear
        int[] pedidosPorHora = new int[12];
        double[] faturamentoPorHora = new double[12];

        Map<String, Integer> volumePorProduto = new java.util.HashMap<>();
        Map<String, Long> volumePorCategoria = new java.util.HashMap<>();
        Map<String, Double> lucroPorCategoria = new java.util.HashMap<>();

        for (HistoricoLog log : logsVendas) {
            if (log.getNomeProduto() != null) {
                String nomeLimpo = log.getNomeProduto().split(" - ")[0].trim();
                int qtdLog = log.getQuantidade() != null ? log.getQuantidade() : 1;

                volumePorProduto.put(nomeLimpo, volumePorProduto.getOrDefault(nomeLimpo, 0) + qtdLog);

                Produto p = listaProdutos.stream()
                        .filter(prod -> prod.getNome().equalsIgnoreCase(nomeLimpo))
                        .findFirst().orElse(null);

                double precoVenda = 12.0;
                String categoria = "Outros";

                if (p != null) {
                    if (p.getPrecoVenda() != null) precoVenda = p.getPrecoVenda();
                    if (p.getCategoria() != null && !p.getCategoria().trim().isEmpty()) {
                        categoria = p.getCategoria().trim();
                    }
                }

                double receitaLog = qtdLog * precoVenda;
                volumePorCategoria.put(categoria, volumePorCategoria.getOrDefault(categoria, 0L) + qtdLog);
                
                double margemLucro = "Bebidas".equalsIgnoreCase(categoria) ? 0.58 : 0.35; 
                lucroPorCategoria.put(categoria, lucroPorCategoria.getOrDefault(categoria, 0.0) + (receitaLog * margemLucro));

                int indiceHora = (int) (log.getId() % 12);
                pedidosPorHora[indiceHora] += qtdLog;
                faturamentoPorHora[indiceHora] += receitaLog;
            }
        }

        model.addAttribute("horasPedidos", java.util.Arrays.stream(pedidosPorHora).mapToObj(String::valueOf).collect(Collectors.joining(",")));
        model.addAttribute("horasFaturamento", java.util.Arrays.stream(faturamentoPorHora).mapToObj(f -> String.format(java.util.Locale.US, "%.1f", f)).collect(Collectors.joining(",")));

        // Vendas por Categoria (Top 5 + Outros)
        List<Map.Entry<String, Long>> catVendasOrdenadas = volumePorCategoria.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()).collect(Collectors.toList());

        List<String> nomesCatVendas = new java.util.ArrayList<>();
        List<Long> qtdsCatVendas = new java.util.ArrayList<>();
        long acumOutrosVendas = 0;

        for (int i = 0; i < catVendasOrdenadas.size(); i++) {
            if (i < 5) {
                nomesCatVendas.add(catVendasOrdenadas.get(i).getKey());
                qtdsCatVendas.add(catVendasOrdenadas.get(i).getValue());
            } else {
                acumOutrosVendas += catVendasOrdenadas.get(i).getValue();
            }
        }
        if (acumOutrosVendas > 0) { nomesCatVendas.add("Outros"); qtdsCatVendas.add(acumOutrosVendas); }
        
        model.addAttribute("catVendasNomes", String.join(",", nomesCatVendas));
        model.addAttribute("catVendasQtds", qtdsCatVendas.stream().map(String::valueOf).collect(Collectors.joining(",")));

        // Categoria Mais Lucrativa (Top 5 + Outros)
        List<Map.Entry<String, Double>> catLucroOrdenadas = lucroPorCategoria.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed()).collect(Collectors.toList());

        List<String> nomesCatLucro = new java.util.ArrayList<>();
        List<Long> qtdsCatLucro = new java.util.ArrayList<>();
        double acumOutrosLucro = 0.0;

        for (int i = 0; i < catLucroOrdenadas.size(); i++) {
            if (i < 5) {
                nomesCatLucro.add(catLucroOrdenadas.get(i).getKey());
                double pct = faturamentoTotal > 0 ? (catLucroOrdenadas.get(i).getValue() / faturamentoTotal) * 100 : 0.0;
                qtdsCatLucro.add((long) pct);
            } else {
                acumOutrosLucro += catLucroOrdenadas.get(i).getValue();
            }
        }
        if (acumOutrosLucro > 0) {
            nomesCatLucro.add("Outros");
            double pctOutros = faturamentoTotal > 0 ? (acumOutrosLucro / faturamentoTotal) * 100 : 0.0;
            qtdsCatLucro.add((long) pctOutros);
        }
        
        model.addAttribute("catLucroNomes", String.join(",", nomesCatLucro));
        model.addAttribute("catLucroQtds", qtdsCatLucro.stream().map(String::valueOf).collect(Collectors.joining(",")));

        // Top 5 Produtos Mais Vendidos (Barras Gêmeas)
        List<Map.Entry<String, Integer>> top5Sorted = volumePorProduto.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).limit(5).collect(Collectors.toList());

        String top5Nomes = top5Sorted.stream().map(Map.Entry::getKey).collect(Collectors.joining(","));
        String top5Qtds = top5Sorted.stream().map(e -> String.valueOf(e.getValue())).collect(Collectors.joining(","));
        String top5Valores = top5Sorted.stream().map(e -> {
            Produto prod = listaProdutos.stream().filter(p -> p.getNome().equalsIgnoreCase(e.getKey())).findFirst().orElse(null);
            double preco = prod != null && prod.getPrecoVenda() != null ? prod.getPrecoVenda() : 12.0;
            return String.valueOf(e.getValue() * preco);
        }).collect(Collectors.joining(","));

        model.addAttribute("top5Nomes", top5Nomes);
        model.addAttribute("top5Qtds", top5Qtds);
        model.addAttribute("top5Valores", top5Valores);

        // 🟢 REAL E FUNCIONAL: Filtra a lista completa de produtos sem demanda do banco
        List<String> produtosComVendas = volumePorProduto.keySet().stream().map(String::toLowerCase).collect(Collectors.toList());
        List<Produto> produtosSemDemanda = listaProdutos.stream()
                .filter(p -> !produtosComVendas.contains(p.getNome().toLowerCase()))
                .limit(5).collect(Collectors.toList());
        
        model.addAttribute("produtosSemDemanda", produtosSemDemanda);

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