// ==========================================================================
// PDV.JS - Lógica de Frente de Caixa (Carrinho e Pagamento)
// ==========================================================================

// Variáveis de estado do PDV
let carrinho = [];
let formaPagamentoSelecionada = 'Pix'; // Padrão
let valorTotal = 0;

// ==========================================================================
// 🔥 NOVO: Filtros de Categoria e Busca - PASSO 3
// ==========================================================================

// Aguarda o carregamento completo da página
document.addEventListener('DOMContentLoaded', function() {
    // 1. Escuta o campo de busca por texto
    const inputBusca = document.querySelector('.pdv-catalog input[type="text"]');
    if (inputBusca) {
        inputBusca.addEventListener('input', function(e) {
            filtrarProdutosDoPDV(e.target.value, null);
        });
    }

    // 2. Escuta os cliques nas Categorias (Pills)
    const botoesCategoria = document.querySelectorAll('.pill-btn');
    botoesCategoria.forEach(botao => {
        botao.addEventListener('click', function() {
            // Altera a classe active visualmente
            botoesCategoria.forEach(b => b.classList.remove('active'));
            this.classList.add('active');

            const categoriaSelecionada = this.textContent.trim();
            filtrarProdutosDoPDV('', categoriaSelecionada);
        });
    });
});

// Função que varre os cards na tela escondendo os que não batem com o filtro
function filtrarProdutosDoPDV(texto, categoria) {
    const cards = document.querySelectorAll('.product-card');
    
    // Se não houver cards, sai da função
    if (!cards.length) return;
    
    cards.forEach(card => {
        // Pega o nome do produto (dentro do h3)
        const nomeElement = card.querySelector('h3');
        const nome = nomeElement ? nomeElement.textContent.toLowerCase() : '';
        
        // Pega a categoria do produto (dentro do elemento .category)
        const catElement = card.querySelector('.category');
        const catCard = catElement ? catElement.textContent.toLowerCase() : '';
        
        let bateTexto = true;
        let bateCategoria = true;

        // Filtro por texto (busca)
        if (texto && texto.trim() !== '') {
            bateTexto = nome.includes(texto.toLowerCase());
        }

        // Filtro por categoria (se não for "Tudo" ou "Todos")
        if (categoria && categoria !== 'Tudo' && categoria !== 'Todos') {
            // Comparação case-insensitive
            bateCategoria = catCard === categoria.toLowerCase();
        }

        // Se passar em ambos os filtros, exibe, senão esconde
        if (bateTexto && bateCategoria) {
            card.style.display = 'flex';
        } else {
            card.style.display = 'none';
        }
    });
}

// ==========================================================================
// FUNÇÕES ORIGINAIS DO PDV (mantidas)
// ==========================================================================

// 1. Função para adicionar produtos ao carrinho
function adicionarAoCarrinho(id, nome, preco) {
    // Verifica se o item já está no carrinho
    const itemExistente = carrinho.find(item => item.id === id);

    if (itemExistente) {
        itemExistente.quantidade += 1; // Só aumenta a quantidade
    } else {
        carrinho.push({
            id: id,
            nome: nome,
            preco: parseFloat(preco),
            quantidade: 1
        }); // Adiciona novo item
    }

    atualizarInterfaceCarrinho();
}

// 2. Função para atualizar a tela do carrinho
function atualizarInterfaceCarrinho() {
    const cartItemsContainer = document.querySelector('.cart-items');
    const totalSpan = document.querySelectorAll('.cart-totals .value');
    const btnCheckout = document.querySelector('.btn-checkout');
    
    // Limpa a tela
    if (cartItemsContainer) cartItemsContainer.innerHTML = '';
    valorTotal = 0;

    if (carrinho.length === 0) {
        // Estado Vazio
        if (cartItemsContainer) {
            cartItemsContainer.innerHTML = `
                <div class="cart-empty">
                    <i class="fas fa-shopping-basket"></i>
                    <p>Carrinho vazio<br><span style="font-weight: normal; font-size: 0.8rem;">Adicione produtos para iniciar</span></p>
                </div>
            `;
        }
        if (btnCheckout) btnCheckout.disabled = true;
    } else {
        // Renderiza os itens do carrinho
        carrinho.forEach((item, index) => {
            valorTotal += (item.preco * item.quantidade);
            
            if (cartItemsContainer) {
                cartItemsContainer.innerHTML += `
                    <div style="display: flex; justify-content: space-between; align-items: center; padding: 10px 0; border-bottom: 1px dashed var(--border-color);">
                        <div style="flex: 1;">
                            <h4 style="font-size: 0.85rem; font-weight: 700; color: var(--text-primary); margin: 0;">${item.nome}</h4>
                            <span style="font-size: 0.75rem; color: var(--text-secondary);">${item.quantidade}x R$ ${item.preco.toFixed(2).replace('.', ',')}</span>
                        </div>
                        <div style="font-weight: 700; color: var(--accent-blue); font-size: 0.95rem; margin-right: 10px;">
                            R$ ${(item.preco * item.quantidade).toFixed(2).replace('.', ',')}
                        </div>
                        <button onclick="removerDoCarrinho(${index})" style="background: none; border: none; color: var(--accent-red); cursor: pointer;">
                            <i class="fas fa-trash-alt"></i>
                        </button>
                    </div>
                `;
            }
        });
        if (btnCheckout) btnCheckout.disabled = false;
    }

    // Atualiza os textos de Subtotal e Total na tela
    const textoTotal = `R$ ${valorTotal.toFixed(2).replace('.', ',')}`;
    if (totalSpan && totalSpan.length >= 2) {
        totalSpan[0].innerText = textoTotal; // Subtotal
        totalSpan[1].innerText = textoTotal; // Total
    }

    // Atualiza o valor também dentro do Modal de Pagamento
    const modalValorTotal = document.getElementById('modal-valor-total');
    const modalQtdItens = document.getElementById('modal-qtd-itens');
    if (modalValorTotal) modalValorTotal.innerText = textoTotal;
    if (modalQtdItens) modalQtdItens.innerText = carrinho.reduce((acc, item) => acc + item.quantidade, 0);
}

// 3. Função para remover um item
function removerDoCarrinho(index) {
    carrinho.splice(index, 1);
    atualizarInterfaceCarrinho();
}

// 4. Função para selecionar a forma de pagamento visualmente no modal
function selecionarPagamento(elemento, metodo) {
    // Remove a classe 'selected' de todos
    const opcoes = document.querySelectorAll('.payment-option');
    opcoes.forEach(op => op.classList.remove('selected'));
    
    // Adiciona no que foi clicado
    elemento.classList.add('selected');
    formaPagamentoSelecionada = metodo;
}

// 5. Enviar para o Back-end (Spring Boot)
function finalizarCompra() {
    if (carrinho.length === 0) return;

    // Pega o nome do cliente se foi digitado
    const clienteNome = document.getElementById('clienteNome') ? document.getElementById('clienteNome').value : 'Consumidor Final';

    // Monta o "Pacote" (JSON) para enviar à API
    const payload = {
        cliente: clienteNome,
        formaPagamento: formaPagamentoSelecionada,
        total: valorTotal,
        itens: carrinho
    };

    // Aqui desabilitamos o botão para não clicar duas vezes
    const btnConfirmar = document.getElementById('btn-confirmar-pagamento');
    if (btnConfirmar) {
        btnConfirmar.innerText = 'Processando...';
        btnConfirmar.disabled = true;
    }

    // Envia via AJAX (Fetch API) para o Java
    fetch('/pdv/finalizar', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
    })
    .then(response => {
        if (response.ok) {
            alert('Venda finalizada com sucesso!');
            // Limpa o PDV para a próxima venda
            carrinho = [];
            atualizarInterfaceCarrinho();
            const modalPagamento = document.getElementById('modalPagamento');
            if (modalPagamento) modalPagamento.style.display = 'none';
            const clienteNomeInput = document.getElementById('clienteNome');
            if (clienteNomeInput) clienteNomeInput.value = '';
        } else {
            alert('Erro ao processar a venda. Verifique o estoque.');
        }
    })
    .catch(error => {
        console.error('Erro:', error);
        alert('Erro de comunicação com o servidor.');
    })
    .finally(() => {
        if (btnConfirmar) {
            btnConfirmar.innerText = 'Confirmar Pagamento';
            btnConfirmar.disabled = false;
        }
    });
}