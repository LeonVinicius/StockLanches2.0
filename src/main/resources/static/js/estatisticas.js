// ==========================================================================
// ESTATISTICAS.JS - Motor de Desenhos Gráficos e Sincronização de Temas
// ==========================================================================

document.addEventListener('DOMContentLoaded', () => {
    const isDark = document.documentElement.getAttribute('data-theme') === 'dark';
    const gridColor = isDark ? 'rgba(255,255,255,0.07)' : 'rgba(0,0,0,0.06)';
    const tickColor = isDark ? '#94A3B8' : '#64748B';

    // 1. Gráfico de Vendas por Hora
    new Chart(document.getElementById('horasChart'), {
        type: 'line',
        data: {
            labels: ['11h','12h','13h','14h','15h','16h','17h','18h','19h','20h','21h','22h'],
            datasets: [
                {
                    label: 'Pedidos',
                    data: [2,6,8,4,3,2,5,9,18,22,16,7],
                    borderColor: '#378ADD',
                    backgroundColor: 'rgba(55,138,221,0.1)',
                    fill: true, tension: 0.4, pointRadius: 2, pointBackgroundColor: '#378ADD'
                },
                {
                    label: 'Faturamento',
                    data: [80,220,310,155,110,80,195,345,690,840,615,265],
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
    new Chart(document.getElementById('pagtoChart'), {
        type: 'doughnut',
        data: {
            labels: ['Cartão', 'Pix', 'Dinheiro'],
            datasets: [{ data: [67, 33, 0.1], backgroundColor: ['#7F77DD', '#1D9E75', '#B4B2A9'], borderWidth: 0 }]
        },
        options: { responsive: true, maintainAspectRatio: false, cutout: '72%', plugins: { legend: { display: false } } }
    });

    // 3. 🟢 GRÁFICO HORIZONTAL DE BARRAS GÊMEAS (Multi-Series - image_548b05.png)
    new Chart(document.getElementById('prodChart'), {
        type: 'bar',
        data: {
            labels: ['X-Frango', 'X-Tudo', 'Batata frita', 'X-Bacon', 'X-Salada'],
            datasets: [
                { 
                    label: 'Qtd vendida', 
                    data: [22, 29, 41, 38, 52], 
                    backgroundColor: '#378ADD', // Barra Azul
                    borderRadius: 4, 
                    barThickness: 8
                },
                { 
                    label: 'Receita (R$)', 
                    data: [55, 87, 41, 95, 120], 
                    backgroundColor: '#7F77DD', // Barra Roxa paralelamente abaixo
                    borderRadius: 4, 
                    barThickness: 8
                }
            ]
        },
        options: {
            indexAxis: 'y', // Ativa exibição na horizontal
            responsive: true, 
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                x: { ticks: { color: tickColor, font: { size: 9 } }, grid: { color: gridColor }, border: { display: false } },
                y: { ticks: { color: tickColor, font: { size: 10, weight: '600' } }, grid: { display: false }, border: { display: false } }
            }
        }
    });

    // 4. Gráfico de Vendas por Categorias Dinâmicas vindas do seu banco de dados
    const lanches = parseInt(document.getElementById('data-lanches').value) || 0;
    const bebidas = parseInt(document.getElementById('data-bebidas').value) || 0;
    const sobremesas = parseInt(document.getElementById('data-sobremesas').value) || 0;
    const acomp = parseInt(document.getElementById('data-acomp').value) || 0;

    new Chart(document.getElementById('catChart'), {
        type: 'bar',
        data: {
            labels: ['Lanches', 'Bebidas', 'Sobremesas', 'Acompanhamentos'],
            datasets: [{ 
                data: [lanches, bebidas, sobremesas, acomp], 
                backgroundColor: ['#378ADD', '#1D9E75', '#EF9F27', '#D4537E'], 
                borderRadius: 4, 
                maxBarThickness: 35 
            }]
        },
        options: {
            responsive: true, maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                x: { ticks: { color: tickColor, font: { size: 9 } }, grid: { display: false }, border: { display: false } },
                y: { ticks: { color: tickColor, font: { size: 9 } }, grid: { color: gridColor }, border: { display: false } }
            }
        }
    });

    // 5. Gráfico de Margem de Lucro por Categoria
    new Chart(document.getElementById('lucroChart'), {
        type: 'bar',
        data: {
            labels: ['Bebidas', 'Sobremesas', 'Porções', 'Lanches'],
            datasets: [{ data: [58, 45, 38, 32], backgroundColor: ['#1D9E75', '#EF9F27', '#378ADD', '#7F77DD'], borderRadius: 4, maxBarThickness: 30 }]
        },
        options: {
            responsive: true, maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                x: { ticks: { color: tickColor, font: { size: 9 } }, grid: { display: false }, border: { display: false } },
                y: { ticks: { color: tickColor, font: { size: 9 }, callback: v => v + '%' }, grid: { color: gridColor }, border: { display: false }, max: 70 }
            }
        }
    });
});