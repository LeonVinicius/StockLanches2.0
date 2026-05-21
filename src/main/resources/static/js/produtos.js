// Adiciona proteção para evitar erros em outras páginas
function abrirModalProduto() {
    const modal = document.getElementById('modalProduto');
    if (modal) {
        modal.style.display = 'flex';
    } else {
        console.error("Modal 'modalProduto' não encontrado.");
    }
}

function fecharModalProduto() {
    const modal = document.getElementById('modalProduto');
    if (modal) modal.style.display = 'none';
}

function salvarProduto() {
    const nome = document.getElementById('prodNome').value;
    const preco = document.getElementById('prodPreco').value;
    console.log("Salvando:", nome, preco);
    fecharModalProduto();
}
let listaIngredientes = []; // Array que guarda os ingredientes selecionados

// Adicionar ingrediente ao modal
function adicionarIngredienteAoModal(id, nome) {
    listaIngredientes.push({ id: id, nome: nome, qtd: 1, un: 'un' });
    renderizarIngredientes();
}

// Renderizar a lista dentro do div 'listaIngredientes'
function renderizarIngredientes() {
    const container = document.getElementById('listaIngredientes');
    container.innerHTML = listaIngredientes.map((ing, index) => `
        <div style="display:flex; gap:10px; align-items:center; margin-bottom:8px;">
            <span style="flex:1;">${ing.nome}</span>
            <input type="number" value="${ing.qtd}" onchange="atualizarQtd(${index}, this.value)" style="width:60px; padding:5px;">
            <span>${ing.un}</span>
            <i class="fas fa-trash" style="color:red; cursor:pointer;" onclick="removerIngrediente(${index})"></i>
        </div>
    `).join('');
}

function removerIngrediente(index) {
    listaIngredientes.splice(index, 1);
    renderizarIngredientes();
}

function atualizarQtd(index, valor) {
    listaIngredientes[index].qtd = valor;
}