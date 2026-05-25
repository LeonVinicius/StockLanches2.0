// ==========================================================================
// PDV.JS - Lógica de Frente de Caixa (Carrinho e Pagamento)
// ==========================================================================

let carrinho = [];
let formaPagamentoSelecionada = 'Pix';
let valorTotal = 0;

// ==========================================================================
// FILTROS DE CATEGORIA E BUSCA
// ==========================================================================

document.addEventListener('DOMContentLoaded', function () {
    const inputBusca = document.getElementById('buscaProdutos');
    if (inputBusca) {
        inputBusca.addEventListener('input', function (e) {
            const categoriaAtiva = document.querySelector('.pill-btn.active');
            filtrarProdutosDoPDV(e.target.value, categoriaAtiva ? categoriaAtiva.textContent.trim() : 'Tudo');
        });
    }

    const botoesCategoria = document.querySelectorAll('.pill-btn');
    botoesCategoria.forEach(botao => {
        botao.addEventListener('click', function () {
            botoesCategoria.forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            const termoBusca = inputBusca ? inputBusca.value : '';
            filtrarProdutosDoPDV(termoBusca, this.textContent.trim());
        });
    });
});

function filtrarProdutosDoPDV(texto, categoria) {
    const cards = document.querySelectorAll('.product-card');
    if (!cards.length) return;

    cards.forEach(card => {
        const nome   = (card.getAttribute('data-nome') || '').toLowerCase();
        const catCard = (card.getAttribute('data-categoria') || '').toLowerCase();

        const bateTexto     = !texto || nome.includes(texto.toLowerCase());
        const bateCategoria = !categoria || categoria === 'Tudo' || catCard === categoria.toLowerCase();

        card.style.display = (bateTexto && bateCategoria) ? 'flex' : 'none';
    });
}

// ==========================================================================
// CARRINHO
// ==========================================================================

function adicionarAoCarrinho(id, nome, preco) {
    const itemExistente = carrinho.find(item => item.id === id);
    if (itemExistente) {
        itemExistente.quantidade += 1;
    } else {
        carrinho.push({ id: id, nome: nome, preco: parseFloat(preco), quantidade: 1 });
    }
    atualizarInterfaceCarrinho();
}

function atualizarInterfaceCarrinho() {
    const cartItemsContainer = document.querySelector('.cart-items');
    const totalSpans         = document.querySelectorAll('.cart-totals .value');
    const btnCheckout        = document.querySelector('.btn-checkout');

    if (cartItemsContainer) cartItemsContainer.innerHTML = '';
    valorTotal = 0;

    if (carrinho.length === 0) {
        if (cartItemsContainer) {
            cartItemsContainer.innerHTML = `
                <div class="cart-empty">
                    <i class="fas fa-shopping-basket"></i>
                    <p>Carrinho vazio<br><span style="font-weight:normal;font-size:0.8rem;">Adicione produtos para iniciar</span></p>
                </div>`;
        }
        if (btnCheckout) btnCheckout.disabled = true;
    } else {
        carrinho.forEach((item, index) => {
            valorTotal += item.preco * item.quantidade;
            if (cartItemsContainer) {
                cartItemsContainer.innerHTML += `
                    <div style="display:flex;justify-content:space-between;align-items:center;padding:10px 0;border-bottom:1px dashed var(--border-color);">
                        <div style="flex:1;">
                            <h4 style="font-size:0.85rem;font-weight:700;color:var(--text-primary);margin:0;">${item.nome}</h4>
                            <span style="font-size:0.75rem;color:var(--text-secondary);">${item.quantidade}x R$ ${item.preco.toFixed(2).replace('.', ',')}</span>
                        </div>
                        <div style="font-weight:700;color:var(--accent-blue);font-size:0.95rem;margin-right:10px;">
                            R$ ${(item.preco * item.quantidade).toFixed(2).replace('.', ',')}
                        </div>
                        <button onclick="removerDoCarrinho(${index})" style="background:none;border:none;color:var(--accent-red);cursor:pointer;">
                            <i class="fas fa-trash-alt"></i>
                        </button>
                    </div>`;
            }
        });
        if (btnCheckout) btnCheckout.disabled = false;
    }

    const textoTotal = `R$ ${valorTotal.toFixed(2).replace('.', ',')}`;
    if (totalSpans && totalSpans.length >= 2) {
        totalSpans[0].innerText = textoTotal;
        totalSpans[1].innerText = textoTotal;
    }

    const modalValor = document.getElementById('modal-valor-total');
    const modalQtd   = document.getElementById('modal-qtd-itens');
    const modalCliente = document.getElementById('modal-cliente-nome');

    if (modalValor) modalValor.innerText = textoTotal;
    if (modalQtd) modalQtd.innerText = carrinho.reduce((acc, item) => acc + item.quantidade, 0);
    if (modalCliente) {
        const clienteInput = document.getElementById('clienteNome');
        modalCliente.innerText = (clienteInput && clienteInput.value.trim()) ? clienteInput.value.trim() : 'Consumidor Final';
    }
}

function removerDoCarrinho(index) {
    carrinho.splice(index, 1);
    atualizarInterfaceCarrinho();
}

function selecionarPagamento(elemento, metodo) {
    document.querySelectorAll('.payment-option').forEach(op => op.classList.remove('selected'));
    elemento.classList.add('selected');
    formaPagamentoSelecionada = metodo;
}

// ==========================================================================
// FINALIZAR COMPRA — chama POST /pdv/finalizar (JSON)
// ==========================================================================

function finalizarCompra() {
    if (carrinho.length === 0) {
        alert('O carrinho está vazio.');
        return;
    }

    const clienteInput = document.getElementById('clienteNome');
    const clienteNome  = clienteInput && clienteInput.value.trim() ? clienteInput.value.trim() : 'Consumidor Final';

    const payload = {
        cliente: clienteNome,
        formaPagamento: formaPagamentoSelecionada,
        total: valorTotal,
        itens: carrinho   // [{id, nome, preco, quantidade}, ...]
    };

    const btnConfirmar = document.getElementById('btn-confirmar-pagamento');
    if (btnConfirmar) {
        btnConfirmar.innerText  = 'Processando...';
        btnConfirmar.disabled   = true;
    }

    fetch('/pdv/finalizar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
    .then(async response => {
        if (response.ok) {
            alert('✅ Venda finalizada com sucesso!');
            carrinho = [];
            atualizarInterfaceCarrinho();
            document.getElementById('modalPagamento').style.display = 'none';
            if (clienteInput) clienteInput.value = '';
        } else {
            // Tenta mostrar quais insumos estão faltando
            const data = await response.json().catch(() => null);
            if (data && data.faltando && data.faltando.length > 0) {
                const lista = data.faltando
                    .map(f => `• ${f.insumoNome}: disponível ${f.disponivel}, necessário ${f.necessario}`)
                    .join('\n');
                alert('❌ Estoque insuficiente:\n' + lista);
            } else {
                const texto = await response.text().catch(() => 'Erro desconhecido.');
                alert('❌ Erro ao processar a venda: ' + texto);
            }
        }
    })
    .catch(error => {
        console.error('Erro:', error);
        alert('Erro de comunicação com o servidor.');
    })
    .finally(() => {
        if (btnConfirmar) {
            btnConfirmar.innerText = 'Confirmar Pagamento';
            btnConfirmar.disabled  = false;
        }
    });
}