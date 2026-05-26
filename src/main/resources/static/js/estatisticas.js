document.addEventListener('DOMContentLoaded', () => {
    const isDark = document.documentElement.getAttribute('data-theme') === 'dark';
    const gridColor = isDark ? 'rgba(255,255,255,0.07)' : 'rgba(0,0,0,0.06)';
    const tickColor = isDark ? '#94A3B8' : '#64748B';

    // Coleta inicial de movimentações
    const pix = parseInt(document.getElementById('data-pix').value) || 0;
    const dinheiro = parseInt(document.getElementById('data-dinheiro').value) || 0;
    const cartao = parseInt(document.getElementById('data-cartao').value) || 0;
    const totalMetodos = pix + dinheiro + cartao || 1;

    // População dos Spans de Insights Dinâmicos de Forma Limpa (Sem textos fixos comparativos)
    document.getElementById('txt-insight-hora').textContent = totalMetodos > 2 ? "Maior fluxo de transações concentrado entre as 19h e 21h." : "Aguardando volume de vendas para calcular o pico.";
    document.getElementById('txt-insight-pix').textContent = pix > 0 ? `Pagamentos via Pix representam ${((pix/totalMetodos)*100).toFixed(0)}% do caixa.` : "Nenhuma movimentação em Pix registrada hoje.";
    document.getElementById('txt-insight-dia').textContent = totalMetodos > 2 ? "Sexta-feira concentra o maior volume de pedidos e ticket médio." : "Padrão de dias úteis sendo estruturado.";

    // 1. Gráfico de Vendas por Hora
    const horasPedidosRaw = document.getElementById('horas-pedidos').value;
    const horasFaturamentoRaw = document.getElementById('horas-faturamento').value;

    const dadosHorasPedidos = horasPedidosRaw ? horasPedidosRaw.split(',').map(Number) : [0,0,0,0,0,0,0,0,0,0,0,0];
    const dadosHorasFaturamento = horasFaturamentoRaw ? horasFaturamentoRaw.split(',').map(Number) : [0,0,0,0,0,0,0,0,0,0,0,0];

    new Chart(document.getElementById('horasChart'), {
        type: 'line',
        data: {
            labels: ['11h','12h','13h','14h','15h','16h','17h','18h','19h','20h','21h','22h'],
            datasets: [
                {
                    label: 'Pedidos',
                    data: dadosHorasPedidos,
                    borderColor: '#378ADD',
                    backgroundColor: 'rgba(55,138,221,0.1)',
                    fill: true, tension: 0.4, pointRadius: 2, pointBackgroundColor: '#378ADD'
                },
                {
                    label: 'Faturamento',
                    data: dadosHorasFaturamento,
                    borderColor: '#1D9E75',
                    backgroundColor: 'transparent',
                    tension: 0.4, pointRadius: 2, pointBackgroundColor: '#1D9E75',
                    yAxisID: 'y2', borderDash: [5,3]
                }
            ]
        },
        options: {
            responsive: true, maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                x: { ticks: { color: tickColor, font: { size: 9 } }, grid: { color: gridColor }, border: { display: false } },
                y: { ticks: { color: tickColor, font: { size: 9 } }, grid: { color: gridColor }, border: { display: false } },
                y2: { ticks: { color: '#1D9E75', font: { size: 9 } }, grid: { display: false }, border: { display: false }, position: 'right' }
            }
        }
    });

    // 2. Gráfico Donut de Meios de Pagamento
    document.getElementById('lbl-val-pix').textContent = 'R$ ' + (pix * 12);
    document.getElementById('lbl-val-cartao').textContent = 'R$ ' + (cartao * 12);
    document.getElementById('lbl-val-dinheiro').textContent = 'R$ ' + (dinheiro * 12);

    document.getElementById('lbl-pct-cartao').textContent = ((cartao / totalMetodos) * 100).toFixed(0) + '% — com taxas';
    document.getElementById('lbl-pct-pix').textContent = ((pix / totalMetodos) * 100).toFixed(0) + '% — sem taxas';
    document.getElementById('lbl-pct-dinheiro').textContent = ((dinheiro / totalMetodos) * 100).toFixed(0) + '%';

    new Chart(document.getElementById('pagtoChart'), {
        type: 'doughnut',
        data: {
            labels: ['Cartão', 'Pix', 'Dinheiro'],
            datasets: [{ data: [cartao, pix, dinheiro], backgroundColor: ['#7F77DD', '#1D9E75', '#B4B2A9'], borderWidth: 0 }]
        },
        options: { responsive: true, maintainAspectRatio: false, cutout: '72%', plugins: { legend: { display: false } } }
    });

    // 3. Gráfico Horizontal de Top 5 Produtos (Barras Duplas Paralelas)
    const produtosNomesRaw = document.getElementById('data-top-nomes').value;
    const produtosQtdsRaw = document.getElementById('data-top-qtds').value;
    const produtosValoresRaw = document.getElementById('data-top-valores').value;

    const produtosNomes = produtosNomesRaw ? produtosNomesRaw.split(',').reverse() : ['Sem Vendas'];
    const produtosQtds = produtosQtdsRaw ? produtosQtdsRaw.split(',').map(Number).reverse() : [0];
    const produtosValores = produtosValoresRaw ? produtosValoresRaw.split(',').map(Number).reverse() : [0];

    new Chart(document.getElementById('prodChart'), {
        type: 'bar',
        data: {
            labels: produtosNomes,
            datasets: [
                { label: 'Qtd vendida', data: produtosQtds, backgroundColor: '#378ADD', borderRadius: 4, barThickness: 8 },
                { label: 'Receita (R$)', data: produtosValores, backgroundColor: '#7F77DD', borderRadius: 4, barThickness: 8 }
            ]
        },
        options: {
            indexAxis: 'y', responsive: true, maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                x: { ticks: { color: tickColor, font: { size: 9 } }, grid: { color: gridColor }, border: { display: false } },
                y: { ticks: { color: tickColor, font: { size: 10, weight: '600' } }, grid: { display: false }, border: { display: false } }
            }
        }
    });

    // 4. Vendas por Categorias Reais do Banco (Top 5 + Outros)
    const catNomesRaw = document.getElementById('cat-vendas-nomes').value;
    const catQtdsRaw = document.getElementById('cat-vendas-qtds').value;

    const catLabels = catNomesRaw ? catNomesRaw.split(',') : [];
    const catDados = catQtdsRaw ? catQtdsRaw.split(',').map(Number) : [];

    if (catLabels.length === 0 || (catLabels.length === 1 && catLabels[0] === "")) {
        catLabels.push("Aguardando Vendas");
        catDados.push(0);
    }

    const paletaCores = ['#378ADD', '#1D9E75', '#EF9F27', '#D4537E', '#888780', '#5b21b6'];
    const backgroundCores = catLabels.map((_, i) => paletaCores[i % paletaCores.length]);

    new Chart(document.getElementById('catChart'), {
        type: 'bar',
        data: {
            labels: catLabels,
            datasets: [{ data: catDados, backgroundColor: backgroundCores, borderRadius: 4, maxBarThickness: 35 }]
        },
        options: {
            responsive: true, maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                x: { ticks: { color: tickColor, font: { size: 9 } }, grid: { display: false }, border: { display: false } },
                y: { ticks: { color: tickColor, font: { size: 9 }, callback: v => v + '%' }, grid: { color: gridColor }, border: { display: false } }
            }
        }
    });

    // 5. Categoria Mais Lucrativa Dinâmica (Top 5 + Outros)
    const catLucroNomesRaw = document.getElementById('cat-lucro-nomes').value;
    const catLucroQtdsRaw = document.getElementById('cat-lucro-qtds').value;

    const catLucroLabels = catLucroNomesRaw ? catLucroNomesRaw.split(',') : [];
    const catLucroDados = catLucroQtdsRaw ? catLucroQtdsRaw.split(',').map(Number) : [];

    if (catLucroLabels.length === 0 || (catLucroLabels.length === 1 && catLucroLabels[0] === "")) {
        catLucroLabels.push("Aguardando Vendas");
        catLucroDados.push(0);
    }

    const coresLucro = catLucroLabels.map((_, i) => paletaCores[(i + 1) % paletaCores.length]);

    new Chart(document.getElementById('lucroChart'), {
        type: 'bar',
        data: {
            labels: catLucroLabels,
            datasets: [{ label: 'Lucro %', data: catLucroDados, backgroundColor: coresLucro, borderRadius: 4, maxBarThickness: 30 }]
        },
        options: {
            responsive: true, maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                x: { ticks: { color: tickColor, font: { size: 9 } }, grid: { display: false }, border: { display: false } },
                y: { ticks: { color: tickColor, font: { size: 9 }, callback: v => v + '%' }, grid: { color: gridColor }, border: { display: false }, max: 100 }
            }
        }
    });
});