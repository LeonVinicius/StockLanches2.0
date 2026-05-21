// ==========================================================================
// ESTOQUE.JS - Controle de Insumos + Ficha Técnica (Vínculo Produto-Insumo)
// ==========================================================================

// --------------------------------------------------------------------------
// ESTADO GLOBAL
// --------------------------------------------------------------------------

// Insumos cadastrados (mock — virá do back-end via Thymeleaf/API)
// Estrutura: { id, nome, qtdAtual, unidade, qtdMinima }
let insumos = [
    { id: 1, nome: 'Pão de Hambúrguer',       qtdAtual: 100,  unidade: 'un', qtdMinima: 30 },
    { id: 2, nome: 'Carne de Hambúrguer (150g)', qtdAtual: 50, unidade: 'un', qtdMinima: 20 },
    { id: 3, nome: 'Queijo Cheddar',           qtdAtual: 2500, unidade: 'g',  qtdMinima: 500 },
    { id: 4, nome: 'Salsicha',                 qtdAtual: 80,   unidade: 'un', qtdMinima: 20 },
    { id: 5, nome: 'Batata',                   qtdAtual: 350,  unidade: 'g',  qtdMinima: 500 },
    { id: 6, nome: 'Coca-Cola Lata',           qtdAtual: 10,   unidade: 'un', qtdMinima: 24 },
];

// Produtos cadastrados no cardápio (mock — virá do back-end)
// Estrutura: { id, nome, categoria }
let produtos = [
    { id: 1, nome: 'X-Burger Clássico', categoria: 'Lanches' },
    { id: 2, nome: 'X-Bacon',           categoria: 'Lanches' },
    { id: 3, nome: 'X-Tudo',            categoria: 'Lanches' },
    { id: 4, nome: 'Coca-Cola 350ml',   categoria: 'Bebidas' },
    { id: 5, nome: 'Combo Família',     categoria: 'Combos'  },
];

// Ficha técnica: mapa insumoId → lista de vínculos
// Estrutura: { [insumoId]: [ { produtoId, quantidade, unidade } ] }
let fichaTecnica = {
    1: [ { produtoId: 1, quantidade: 1, unidade: 'un' }, { produtoId: 2, quantidade: 1, unidade: 'un' } ],
    2: [ { produtoId: 1, quantidade: 1, unidade: 'un' } ],
    3: [ { produtoId: 1, quantidade: 30, unidade: 'g'  }, { produtoId: 2, quantidade: 50, unidade: 'g' } ],
};

// Controle de qual insumo está sendo editado nos modais
let insumoEmEdicao = null;

// Filtro ativo
let filtroStatus = 'todos';
let termoBusca   = '';

// --------------------------------------------------------------------------
// HELPERS
// --------------------------------------------------------------------------

function calcularStatus(insumo) {
    const ratio = insumo.qtdAtual / insumo.qtdMinima;
    if (ratio >= 1)   return 'adequado';
    if (ratio >= 0.5) return 'alerta';
    return 'critico';
}

function statusLabel(s) {
    return { adequado: 'Adequado', alerta: 'Alerta', critico: 'Crítico' }[s] || s;
}

function statusIcon(s) {
    return { adequado: 'fa-check-circle', alerta: 'fa-exclamation-triangle', critico: 'fa-exclamation-triangle' }[s];
}

function fmt(n) { return n.toLocaleString('pt-BR'); }

function proximoId() {
    return insumos.length ? Math.max(...insumos.map(i => i.id)) + 1 : 1;
}

function toast(msg, tipo = 'ok') {
    const t = document.getElementById('toast');
    const i = t.querySelector('i');
    const s = t.querySelector('span');
    i.className = `fas ${tipo === 'ok' ? 'fa-check-circle ok' : 'fa-times-circle err'}`;
    s.textContent = msg;
    t.classList.add('show');
    setTimeout(() => t.classList.remove('show'), 3000);
}

// --------------------------------------------------------------------------
// RENDER TABELA
// --------------------------------------------------------------------------

function renderTabela() {
    const tbody = document.getElementById('tbodyInsumos');

    let lista = [...insumos];

    // Filtro por busca
    if (termoBusca) {
        lista = lista.filter(i => i.nome.toLowerCase().includes(termoBusca.toLowerCase()));
    }

    // Filtro por status
    if (filtroStatus !== 'todos') {
        lista = lista.filter(i => calcularStatus(i) === filtroStatus);
    }

    if (!lista.length) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6" class="table-empty">
                    <i class="fas fa-box-open"></i>
                    Nenhum insumo encontrado.
                </td>
            </tr>`;
        renderSummary();
        return;
    }

    tbody.innerHTML = lista.map(insumo => {
        const status = calcularStatus(insumo);
        const ratio  = Math.min(insumo.qtdAtual / insumo.qtdMinima, 1);
        const idPad  = String(insumo.id).padStart(3, '0');
        const vincQtd = (fichaTecnica[insumo.id] || []).length;

        return `
        <tr>
            <td class="td-id">#${idPad}</td>
            <td class="td-nome">${insumo.nome}</td>
            <td>
                <div class="qty-cell">
                    <span class="qty-value">${fmt(insumo.qtdAtual)} <small style="color:var(--text-muted);font-weight:400">${insumo.unidade}</small></span>
                    <div class="qty-bar-bg">
                        <div class="qty-bar-fill ${status}" style="width:${(ratio * 100).toFixed(1)}%"></div>
                    </div>
                </div>
            </td>
            <td style="color:var(--text-secondary);font-size:0.85rem">${insumo.unidade}</td>
            <td>
                <span class="status-badge ${status}">
                    <i class="fas ${statusIcon(status)}"></i>
                    ${statusLabel(status)}
                </span>
            </td>
            <td class="td-acoes">
                <div class="btn-acoes-group">
                    <button class="btn-icon green" title="Vincular a Produtos (${vincQtd} vínculo${vincQtd !== 1 ? 's' : ''})"
                            onclick="abrirModalVincular(${insumo.id})">
                        <i class="fas fa-link"></i>
                    </button>
                    <button class="btn-icon blue" title="Editar" onclick="abrirModalEditar(${insumo.id})">
                        <i class="fas fa-pen"></i>
                    </button>
                    <button class="btn-icon red" title="Excluir" onclick="abrirModalExcluir(${insumo.id})">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </td>
        </tr>`;
    }).join('');

    renderSummary();
}

// --------------------------------------------------------------------------
// CARDS DE RESUMO
// --------------------------------------------------------------------------

function renderSummary() {
    document.getElementById('totalInsumos').textContent  = insumos.length;
    document.getElementById('totalAdequado').textContent = insumos.filter(i => calcularStatus(i) === 'adequado').length;
    document.getElementById('totalCritico').textContent  = insumos.filter(i => calcularStatus(i) === 'critico').length;
}

// --------------------------------------------------------------------------
// MODAL: ADICIONAR / EDITAR INSUMO
// --------------------------------------------------------------------------

function abrirModalAdicionar() {
    insumoEmEdicao = null;
    document.getElementById('formInsumoTitle').textContent = 'Adicionar Insumo';
    document.getElementById('formInsumoNome').value    = '';
    document.getElementById('formInsumoQtd').value     = '';
    document.getElementById('formInsumoMin').value     = '';
    document.getElementById('formInsumoUnd').value     = 'un';
    document.getElementById('modalInsumo').classList.add('open');
}

function abrirModalEditar(id) {
    const insumo = insumos.find(i => i.id === id);
    if (!insumo) return;
    insumoEmEdicao = id;

    document.getElementById('formInsumoTitle').textContent = 'Editar Insumo';
    document.getElementById('formInsumoNome').value = insumo.nome;
    document.getElementById('formInsumoQtd').value  = insumo.qtdAtual;
    document.getElementById('formInsumoMin').value  = insumo.qtdMinima;
    document.getElementById('formInsumoUnd').value  = insumo.unidade;
    document.getElementById('modalInsumo').classList.add('open');
}

function fecharModalInsumo() {
    document.getElementById('modalInsumo').classList.remove('open');
}

function salvarInsumo() {
    const nome  = document.getElementById('formInsumoNome').value.trim();
    const qtd   = parseFloat(document.getElementById('formInsumoQtd').value);
    const min   = parseFloat(document.getElementById('formInsumoMin').value);
    const und   = document.getElementById('formInsumoUnd').value;

    if (!nome || isNaN(qtd) || isNaN(min)) {
        toast('Preencha todos os campos corretamente.', 'err');
        return;
    }

    if (insumoEmEdicao !== null) {
        // Editar
        const insumo = insumos.find(i => i.id === insumoEmEdicao);
        insumo.nome      = nome;
        insumo.qtdAtual  = qtd;
        insumo.qtdMinima = min;
        insumo.unidade   = und;
        toast(`"${nome}" atualizado com sucesso!`);
    } else {
        // Novo
        insumos.push({ id: proximoId(), nome, qtdAtual: qtd, unidade: und, qtdMinima: min });
        toast(`"${nome}" adicionado ao estoque!`);
    }

    fecharModalInsumo();
    renderTabela();
}

// --------------------------------------------------------------------------
// MODAL: EXCLUIR
// --------------------------------------------------------------------------

function abrirModalExcluir(id) {
    const insumo = insumos.find(i => i.id === id);
    if (!insumo) return;
    insumoEmEdicao = id;
    document.getElementById('excluirNome').textContent = insumo.nome;
    document.getElementById('modalExcluir').classList.add('open');
}

function fecharModalExcluir() {
    document.getElementById('modalExcluir').classList.remove('open');
}

function confirmarExcluir() {
    const insumo = insumos.find(i => i.id === insumoEmEdicao);
    const nome = insumo ? insumo.nome : '';
    insumos = insumos.filter(i => i.id !== insumoEmEdicao);
    delete fichaTecnica[insumoEmEdicao];
    fecharModalExcluir();
    renderTabela();
    toast(`"${nome}" removido do estoque.`);
}

// --------------------------------------------------------------------------
// MODAL: VINCULAR INSUMO → PRODUTOS (Ficha Técnica)
// --------------------------------------------------------------------------

function abrirModalVincular(id) {
    const insumo = insumos.find(i => i.id === id);
    if (!insumo) return;
    insumoEmEdicao = id;

    // Cabeçalho do insumo
    document.getElementById('vinc-nome').textContent    = insumo.nome;
    document.getElementById('vinc-estoque').textContent =
        `Estoque atual: ${fmt(insumo.qtdAtual)} ${insumo.unidade}`;

    // Popular select de produtos
    const sel = document.getElementById('vinc-produto-sel');
    sel.innerHTML = '<option value="">Selecione um produto...</option>' +
        produtos.map(p => `<option value="${p.id}">${p.nome} (${p.categoria})</option>`).join('');

    // Unidade padrão baseada no insumo
    document.getElementById('vinc-und-input').value = insumo.unidade;

    renderVinculos();
    document.getElementById('modalVincular').classList.add('open');
}

function fecharModalVincular() {
    document.getElementById('modalVincular').classList.remove('open');
}

function renderVinculos() {
    const lista = fichaTecnica[insumoEmEdicao] || [];
    const container = document.getElementById('vinculosLista');

    if (!lista.length) {
        container.innerHTML = '<div class="vinculos-vazio"><i class="fas fa-unlink"></i> Nenhum produto vinculado ainda.</div>';
        return;
    }

    container.innerHTML = lista.map((v, idx) => {
        const prod = produtos.find(p => p.id === v.produtoId);
        const nomeProd = prod ? prod.nome : `Produto #${v.produtoId}`;
        return `
        <div class="vinculo-row">
            <span class="produto-nome"><i class="fas fa-burger" style="color:var(--text-muted);margin-right:6px;font-size:0.8rem"></i>${nomeProd}</span>
            <span class="vinculo-qty">${v.quantidade} ${v.unidade}</span>
            <span class="vinculo-und" style="font-size:0.75rem;color:var(--text-muted)">por unidade</span>
            <button class="btn-remove-vinculo" onclick="removerVinculo(${idx})" title="Remover vínculo">
                <i class="fas fa-times"></i>
            </button>
        </div>`;
    }).join('');
}

function adicionarVinculo() {
    const prodId = parseInt(document.getElementById('vinc-produto-sel').value);
    const qty    = parseFloat(document.getElementById('vinc-qty-input').value);
    const und    = document.getElementById('vinc-und-input').value.trim();

    if (!prodId) { toast('Selecione um produto.', 'err'); return; }
    if (isNaN(qty) || qty <= 0) { toast('Informe uma quantidade válida.', 'err'); return; }
    if (!und) { toast('Informe a unidade.', 'err'); return; }

    // Evitar duplicata
    if (!fichaTecnica[insumoEmEdicao]) fichaTecnica[insumoEmEdicao] = [];
    const jaExiste = fichaTecnica[insumoEmEdicao].find(v => v.produtoId === prodId);
    if (jaExiste) {
        toast('Este produto já está vinculado a este insumo.', 'err');
        return;
    }

    fichaTecnica[insumoEmEdicao].push({ produtoId: prodId, quantidade: qty, unidade: und });
    document.getElementById('vinc-qty-input').value = '';
    renderVinculos();
    renderTabela(); // atualiza contador de vínculos na tabela
    toast('Vínculo adicionado!');
}

function removerVinculo(idx) {
    if (!fichaTecnica[insumoEmEdicao]) return;
    fichaTecnica[insumoEmEdicao].splice(idx, 1);
    renderVinculos();
    renderTabela();
    toast('Vínculo removido.');
}

// --------------------------------------------------------------------------
// FUNÇÃO PÚBLICA: Verificar se há estoque suficiente para um pedido
// Use no PDV antes de finalizar a venda.
//
// Parâmetro: itensCarrinho = [ { produtoId, quantidade } ]
// Retorno: { ok: true } ou { ok: false, faltando: [ { insumoNome, falta, unidade } ] }
// --------------------------------------------------------------------------

function verificarEstoqueParaPedido(itensCarrinho) {
    // Monta um mapa: insumoId → quantidade necessária total
    const necessario = {};

    itensCarrinho.forEach(item => {
        // Para cada insumo vinculado ao produto
        Object.entries(fichaTecnica).forEach(([insumoId, vinculos]) => {
            const vinculo = vinculos.find(v => v.produtoId === item.produtoId);
            if (vinculo) {
                const id = parseInt(insumoId);
                necessario[id] = (necessario[id] || 0) + (vinculo.quantidade * item.quantidade);
            }
        });
    });

    const faltando = [];
    Object.entries(necessario).forEach(([insumoId, qtdNecessaria]) => {
        const insumo = insumos.find(i => i.id === parseInt(insumoId));
        if (!insumo) return;
        if (insumo.qtdAtual < qtdNecessaria) {
            faltando.push({
                insumoNome: insumo.nome,
                disponivel: insumo.qtdAtual,
                necessario: qtdNecessaria,
                falta: qtdNecessaria - insumo.qtdAtual,
                unidade: insumo.unidade,
            });
        }
    });

    return faltando.length ? { ok: false, faltando } : { ok: true };
}

// --------------------------------------------------------------------------
// FUNÇÃO PÚBLICA: Debitar estoque após venda confirmada
// Use no PDV no confirmarPagamento(), antes do fetch para o back-end.
//
// Parâmetro: itensCarrinho = [ { produtoId, quantidade } ]
// --------------------------------------------------------------------------

// ==========================================================================
// 🔥 NOVAS FUNÇÕES PARA SINCRONIZAR COM O BACK-END
// ==========================================================================

// Função para debitar estoque diretamente no banco de dados via API
async function debitarEstoqueNoBanco(itensCarrinho) {
    // payload estruturado: [ { produtoId: 1, quantidade: 2 }, ... ]
    const payload = itensCarrinho.map(item => ({
        produtoId: item.id,
        quantidade: item.qtd
    }));

    try {
        const response = await fetch('/api/estoque/debitar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const erroTexto = await response.text();
            console.error("Falha ao debitar estoque:", erroTexto);
            toast("Erro ao debitar estoque no servidor!", "err");
            return false;
        }
        
        toast("Estoque atualizado com sucesso!");
        return true;
    } catch (error) {
        console.error("Erro de comunicação ao atualizar estoque:", error);
        toast("Erro de conexão com o servidor!", "err");
        return false;
    }
}

// Função para debitar estoque localmente (fallback/mock)
function debitarEstoqueLocal(itensCarrinho) {
    itensCarrinho.forEach(item => {
        Object.entries(fichaTecnica).forEach(([insumoId, vinculos]) => {
            const vinculo = vinculos.find(v => v.produtoId === item.produtoId);
            if (vinculo) {
                const insumo = insumos.find(i => i.id === parseInt(insumoId));
                if (insumo) {
                    insumo.qtdAtual -= vinculo.quantidade * item.quantidade;
                    if (insumo.qtdAtual < 0) insumo.qtdAtual = 0;
                }
            }
        });
    });
    renderTabela();
}

// Função principal de debitar estoque (tenta banco primeiro, fallback local)
async function debitarEstoque(itensCarrinho) {
    // Tenta debitar no banco de dados real
    const sucesso = await debitarEstoqueNoBanco(itensCarrinho);
    
    if (sucesso) {
        // Se sucesso no banco, também atualiza localmente para manter consistência
        debitarEstoqueLocal(itensCarrinho);
    } else {
        console.warn("Usando fallback local para debitar estoque");
        debitarEstoqueLocal(itensCarrinho);
        toast("Estoque atualizado apenas localmente (modo offline)", "err");
    }
}

// --------------------------------------------------------------------------
// FILTROS
// --------------------------------------------------------------------------

function setFiltroStatus(val) {
    filtroStatus = val;
    document.querySelectorAll('.pill-btn').forEach(b => b.classList.remove('active'));
    document.querySelector(`.pill-btn[data-status="${val}"]`).classList.add('active');
    renderTabela();
}

// --------------------------------------------------------------------------
// INIT
// --------------------------------------------------------------------------

document.addEventListener('DOMContentLoaded', () => {
    renderTabela();

    // Busca
    document.getElementById('searchInsumo').addEventListener('input', e => {
        termoBusca = e.target.value;
        renderTabela();
    });

    // Filtro select
    document.getElementById('filtroStatus').addEventListener('change', e => {
        filtroStatus = e.target.value;
        renderTabela();
    });

    // Fechar modais clicando no overlay
    ['modalInsumo', 'modalExcluir', 'modalVincular'].forEach(id => {
        document.getElementById(id).addEventListener('click', e => {
            if (e.target.id === id) {
                document.getElementById(id).classList.remove('open');
            }
        });
    });
});