document.addEventListener('DOMContentLoaded', function() {
    Chart.defaults.color = '#94a3b8';
    Chart.defaults.borderColor = '#334155';

  
    const qtdLanches = document.getElementById('data-lanches').value;
    const qtdBebidas = document.getElementById('data-bebidas').value;
    const qtdSobremesas = document.getElementById('data-sobremesas').value;
    const qtdAcomp = document.getElementById('data-acomp').value;

    const top5NomesRaw = document.getElementById('data-top5-nomes').value;
    const top5QtdRaw = document.getElementById('data-top5-qtd').value;


    const top5Nomes = top5NomesRaw ? top5NomesRaw.split(',') : [];
    const top5Qtd = top5QtdRaw ? top5QtdRaw.split(',') : [];


    const ctxPizza = document.getElementById('graficoPizza').getContext('2d');
    new Chart(ctxPizza, {
        type: 'pie',
        data: {
            labels: ['Lanches', 'Bebidas', 'Sobremesas', 'Acompanhamentos'],
            datasets: [{
                data: [qtdLanches, qtdBebidas, qtdSobremesas, qtdAcomp], // Dados dinâmicos aqui
                backgroundColor: ['#3b82f6', '#10b981', '#f59e0b', '#a855f7'],
                borderWidth: 0
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { position: 'right' } }
        }
    });


    const ctxBarras = document.getElementById('graficoBarras').getContext('2d');
    new Chart(ctxBarras, {
        type: 'bar',
        data: {
            labels: top5Nomes, 
            datasets: [{
                label: 'Qtd. Estoque',
                data: top5Qtd, 
                backgroundColor: '#ef4444',
                borderRadius: 4,
                barThickness: 20
            }]
        },
        options: {
            indexAxis: 'y',
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: { x: { beginAtZero: true, grid: { display: false } } }
        }
    });
});