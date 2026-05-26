// ==========================================================================
// HISTORICO.JS - Auditoria de Logs e Emissão de Relatórios
// ==========================================================================

document.addEventListener('DOMContentLoaded', () => {
    inicializarFiltroHistorico();
});

/**
 * Filtra as linhas de histórico na tela em tempo real à medida que o usuário digita
 */
function inicializarFiltroHistorico() {
    const inputFiltro = document.getElementById('filtroHistorico');
    if (!inputFiltro) return;

    inputFiltro.addEventListener('input', (e) => {
        const termo = e.target.value.toLowerCase().trim();
        const itens = document.querySelectorAll('.history-item');
        let itensVisiveis = 0;

        itens.forEach(item => {
            const elementoTitulo = item.querySelector('.history-item-title');
            if (elementoTitulo) {
                const textoPesquisa = elementoTitulo.getAttribute('data-search') || '';
                
                if (textoPesquisa.includes(termo)) {
                    item.style.display = 'flex';
                    itensVisiveis++;
                } else {
                    item.style.display = 'none';
                }
            }
        });

        // Exibe mensagem amigável caso a busca oculte todos os registros
        atualizarMensagemListaVazia(itensVisiveis === 0 && itens.length > 0);
    });
}

/**
 * Controla a exibição de aviso caso a busca não traga registros
 */
function atualizarMensagemListaVazia(mostrar) {
    let msgVazia = document.getElementById('historicoPesquisaVazia');
    const containerLista = document.querySelector('.history-list');

    if (mostrar) {
        if (!msgVazia && containerLista) {
            msgVazia = document.createElement('div');
            msgVazia.id = 'historicoPesquisaVazia';
            msgVazia.style.cssText = 'text-align: center; padding: 40px; color: var(--text-muted); width: 100%;';
            msgVazia.innerHTML = `
                <i class="fas fa-search-minus" style="font-size: 2rem; margin-bottom: 10px;"></i>
                <p>Nenhum log corresponde aos critérios da busca.</p>
            `;
            containerLista.appendChild(msgVazia);
        }
    } else {
        if (msgVazia) msgVazia.remove();
    }
}

/**
 * Ação disparada pelo botão de exportar relatório detalhado
 */
function emitirRelatorio() {
    console.log("[AUDITORIA] Solicitação de relatório PDF gerada.");
    alert("📊 Preparando a consolidação das auditorias! A funcionalidade de download do Relatório Detalhado (PDF) será interligada no próximo módulo do sistema.");
}