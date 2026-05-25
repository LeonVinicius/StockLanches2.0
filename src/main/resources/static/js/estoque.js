// ==========================================================================
// ESTOQUE.JS - Controle de Insumos Real integrado via API Monolítica
// ==========================================================================

let insumos = []; // Carregado em tempo real do banco de dados
let insumoEmEdicaoId = null; // null = Novo, número = Editando
let filtroStatus = 'todos';
let termoBusca   = '';

// --------------------------------------------------------------------------
// CARREGAR DADOS DO SERVIDOR (BANCO DE DADOS)
// --------------------------------------------------------------------------
async function carregarInsumosDoBanco() {
    try {
        const response = await fetch('/api/estoque');
        if (response.ok) {
            insumos = await response.json();
            renderTabela();
        } else {
            console.error("Erro ao carregar insumos do servidor.");
        }
    } catch (error) {
        console.error("Erro de conexão ao buscar insumos:", error);
    }
}

// --------------------------------------------------------------------------
// AUXILIARES DE RENDERIZAÇÃO E REGRA DE NEGÓCIO
// --------------------------------------------------------------------------
function calcularStatus(insumo) {
    if (!insumo.qtdMinima || insumo.qtdMinima === 0) return 'adequado';
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

function fmt(n) { return n ? n.toLocaleString('pt-BR') : '0'; }

function toast(msg, tipo = 'ok') {
    const t = document.getElementById('toast');
    if (!t) return;
    const i = t.querySelector('i');
    const s = t.querySelector('span');
    i.className = `fas ${tipo === 'ok' ? 'fa-check-circle ok' : 'fa-times-circle err'}`;
    s.textContent = msg;
    t.classList.add('show');
    setTimeout(() => t.classList.remove('show'), 3000);
}

// --------------------------------------------------------------------------
// RENDERIZAR TABELA DINÂMICA
// --------------------------------------------------------------------------
function renderTabela() {
    const tbody = document.getElementById('tbodyInsumos');
    if (!tbody) return;

    let lista = [...insumos];

    if (termoBusca) {
        lista = lista.filter(i => i.nome.toLowerCase().includes(termoBusca.toLowerCase()));
    }

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

function renderSummary() {
    const tInsumos = document.getElementById('totalInsumos');
    const tAdequado = document.getElementById('totalAdequado');
    const tCritico = document.getElementById('totalCritico');
    
    if(tInsumos) tInsumos.textContent = insumos.length;
    if(tAdequado) tAdequado.textContent = insumos.filter(i => calcularStatus(i) === 'adequado').length;
    if(tCritico) tCritico.textContent = insumos.filter(i => calcularStatus(i) === 'critico').length;
}

// --------------------------------------------------------------------------
// GERENCIAMENTO DOS MODAIS (ADICIONAR / SALVAR / EDITAR / DELETAR)
// --------------------------------------------------------------------------
function abrirModalAdicionar() {
    insumoEmEdicaoId = null;
    document.getElementById('formInsumoTitle').textContent = 'Adicionar Insumo';
    document.getElementById('formInsumoNome').value    = '';
    document.getElementById('formInsumoQtd').value     = '';
    document.getElementById('formInsumoMin').value     = '';
    document.getElementById('formInsumoUnd').value     = 'un';
    document.getElementById('modalInsumo').classList.add('open');
}

function abrirModalEditar(id) {
    const insumo = insumos.find(i => i.id == id);
    if (!insumo) return;
    
    insumoEmEdicaoId = id;
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

// 🔥 SALVAMENTO EM TEMPO REAL INTEGRADO AO CONTROLLER
async function salvarInsumo() {
    const nome  = document.getElementById('formInsumoNome').value.trim();
    const qtd   = parseFloat(document.getElementById('formInsumoQtd').value);
    const min   = parseFloat(document.getElementById('formInsumoMin').value);
    const und   = document.getElementById('formInsumoUnd').value;

    if (!nome || isNaN(qtd) || isNaN(min)) {
        alert('Por favor, preencha todos os campos do insumo corretamente.');
        return;
    }

    const payload = {
        nome: nome,
        qtdAtual: qtd,
        qtdMinima: min,
        unidade: und
    };

    // Caso seja uma edição, acopla o ID existente no JSON para o JPA atualizar
    if (insumoEmEdicaoId !== null) {
        payload.id = insumoEmEdicaoId;
    }

    try {
        const response = await fetch('/api/estoque/salvar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            toast(insumoEmEdicaoId !== null ? 'Insumo atualizado!' : 'Insumo cadastrado!');
            fecharModalInsumo();
            carregarInsumosDoBanco(); // Atualiza a tabela na hora puxando do banco
        } else {
            const txtErro = await response.text();
            alert("Erro do servidor: " + txtErro);
        }
    } catch (error) {
        console.error("Erro transacional ao salvar:", error);
        alert("Falha na comunicação com o servidor.");
    }
}

function abrirModalExcluir(id) {
    const insumo = insumos.find(i => i.id == id);
    if (!insumo) return;
    insumoEmEdicaoId = id;
    document.getElementById('excluirNome').textContent = insumo.nome;
    document.getElementById('modalExcluir').classList.add('open');
}

function fecharModalExcluir() {
    document.getElementById('modalExcluir').classList.remove('open');
}

async function confirmarExcluir() {
    if (insumoEmEdicaoId === null) return;

    try {
        const response = await fetch(`/api/estoque/${insumoEmEdicaoId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            toast('Insumo removido com sucesso!');
            fecharModalExcluir();
            carregarInsumosDoBanco();
        } else {
            const txtErro = await response.text();
            alert("Erro ao excluir: " + txtErro);
        }
    } catch (error) {
        console.error("Erro ao deletar:", error);
    }
}

// --------------------------------------------------------------------------
// INICIALIZAÇÃO
// --------------------------------------------------------------------------
document.addEventListener('DOMContentLoaded', () => {
    carregarInsumosDoBanco(); // Puxa os dados reais assim que a página abre

    const searchInsumo = document.getElementById('searchInsumo');
    if (searchInsumo) {
        searchInsumo.addEventListener('input', e => {
            termoBusca = e.target.value;
            renderTabela();
        });
    }

    const filtroStatusSel = document.getElementById('filtroStatus');
    if (filtroStatusSel) {
        filtroStatusSel.addEventListener('change', e => {
            filtroStatus = e.target.value;
            renderTabela();
        });
    }
});