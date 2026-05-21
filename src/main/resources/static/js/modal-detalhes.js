

function abrirModalDetalhes(nome, categoria, custo, fornecedor, descricao) {
 
    document.getElementById('m-nome').innerText = nome;
    document.getElementById('m-categoria').innerText = categoria;
    

    const valorCusto = parseFloat(custo) || 0;
    const custoFormatado = valorCusto.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
    document.getElementById('m-custo').innerText = custoFormatado;
    

    document.getElementById('m-fornecedor').innerText = fornecedor || 'Não informado';
    document.getElementById('m-descricao').innerText = descricao || 'Nenhuma descrição disponível para este produto.';

    const modal = document.getElementById('modalDetalhes');
    if (modal) {
        modal.style.display = 'flex';
    }
}

function fecharModalDetalhes(event) {
    const modal = document.getElementById('modalDetalhes');
    
    if (!modal) return;

 
    if (event.target === modal || event.target.closest('button')) {
         modal.style.display = 'none';
    }
}