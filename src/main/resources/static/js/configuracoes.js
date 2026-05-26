// ==========================================================================
// CONFIGURACOES.JS - Lógica de Customização Visuall, Datas e Segurança
// ==========================================================================

document.addEventListener('DOMContentLoaded', () => {
    inicializarDataAtual();
    inicializarGerenciamentoTemas();
});

/**
 * Renderiza e formata a data no topo superior direito da tela
 */
function inicializarDataAtual() {
    const elementoData = document.getElementById('currentDate');
    if (elementoData) {
        const d = new Date();
        elementoData.textContent = d.toLocaleDateString('pt-BR', { 
            weekday: 'long', 
            day: 'numeric', 
            month: 'long' 
        });
    }
}

/**
 * Monitora os cliques nos cards de temas e sincroniza a seleção visual
 */
function inicializarGerenciamentoTemas() {
    const cards = document.querySelectorAll('.theme-card');
    const savedTheme = localStorage.getItem('sl-theme') || 'light';

    // Garante que o card correspondente ao tema salvo inicie marcado na tela
    cards.forEach(card => {
        const themeId = card.getAttribute('data-theme-id');
        if (themeId === savedTheme) {
            card.classList.add('selected');
        } else {
            card.classList.remove('selected');
        }

        // Adiciona escuta de evento de clique para alteração dinâmica
        card.addEventListener('click', function () {
            const selectedTheme = this.getAttribute('data-theme-id');
            
            // Injeta o atributo root no HTML mapeado pelo estilo-clean.css
            document.documentElement.setAttribute('data-theme', selectedTheme);
            
            // Sincroniza classes visuais nas bordas
            cards.forEach(c => c.classList.remove('selected'));
            this.classList.add('selected');
            
            // Salva de forma permanente no navegador para não se perder nas trocas de abas
            localStorage.setItem('sl-theme', selectedTheme);
        });
    });
}

/**
 * Validação de segurança de front-end antes de submeter ao back-end
 */
function validarTrocaSenha() {
    const senhaAtual = document.getElementById('senhaAtual').value;
    const novaSenha = document.getElementById('novaSenha').value;
    const confirmarSenha = document.getElementById('confirmarSenha').value;

    if (senhaAtual === novaSenha) {
        alert("❌ A nova senha não pode ser idêntica à senha atual!");
        return false;
    }

    if (novaSenha !== confirmarSenha) {
        alert("❌ A confirmação falhou. A 'Nova Senha' e a 'Confirmar Nova Senha' precisam ser estritamente iguais!");
        return false;
    }

    // Passa na validação estrutural
    return true;
}