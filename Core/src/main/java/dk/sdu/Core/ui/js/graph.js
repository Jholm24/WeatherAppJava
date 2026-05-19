let chart = null;
let historyData = [];
let currentAddress = '';
let selectedVar = 'temp';

const VARIABLES = [
    { key: 'temp',      label: 'Temperatur',    unit: '°C'  },
    { key: 'humidity',  label: 'Luftfugtighed', unit: '%'   },
    { key: 'windSpeed', label: 'Vindhastighed', unit: 'm/s' }
];

const SOURCE_COLORS = {
    openweather:   'rgb(54, 162, 235)',
    googleweather: 'rgb(255, 99, 132)'
};

function renderButtons() {
    const container = document.getElementById('var-buttons');
    container.innerHTML = '';
    VARIABLES.forEach(v => {
        const btn = document.createElement('button');
        btn.textContent = v.label;
        btn.className = 'var-btn' + (v.key === selectedVar ? ' active' : '');
        btn.onclick = () => {
            selectedVar = v.key;
            renderButtons();
            renderChart();
        };
        container.appendChild(btn);
    });
}

function renderChart() {
    if (historyData.length === 0) return;

    const varConfig = VARIABLES.find(v => v.key === selectedVar);
    const sources = [...new Set(historyData.map(d => d.source))];
    // Use timestamps from one source only — both providers are polled at the same time,
    // so their reading counts always match. Mixing timestamps from all sources doubles the labels.
    const labels = historyData
        .filter(d => d.source === sources[0])
        .map(d => new Date(d.time).toLocaleTimeString());

    const datasets = sources.map(source => {
        const readings = historyData.filter(d => d.source === source);
        return {
            label: `${varConfig.label} (${source})`,
            data: readings.map(d => d[selectedVar]),
            borderColor: SOURCE_COLORS[source] || 'rgb(75, 192, 192)',
            borderWidth: 2,
            tension: 0.3,
            fill: false
        };
    });

    const allValues = historyData.map(d => d[selectedVar]).filter(v => v != null);
    const padding = varConfig.key === 'temp' ? 5 : 2;
    const yMin = Math.min(...allValues) - padding;
    const yMax = Math.max(...allValues) + padding;
    const titleText = `${varConfig.label} over tid — ${currentAddress}`;

    if (chart) {
        chart.data.labels = labels;
        chart.data.datasets = datasets;
        chart.options.scales.y.min = yMin;
        chart.options.scales.y.max = yMax;
        chart.options.scales.y.title.text = varConfig.unit;
        chart.options.plugins.title.text = titleText;
        chart.update();
    } else {
        const ctx = document.getElementById('myChart');
        chart = new Chart(ctx, {
            type: 'line',
            data: { labels, datasets },
            options: {
                scales: {
                    y: { min: yMin, max: yMax, title: { display: true, text: varConfig.unit } },
                    x: { title: { display: true, text: 'Tid' } }
                },
                plugins: {
                    title: { display: true, text: titleText }
                }
            }
        });
    }
}

function updateGraph(address) {
    currentAddress = address;
    fetch(`/api/history?address=${encodeURIComponent(address)}`)
        .then(res => res.json())
        .then(data => {
            if (data.length === 0) return;
            historyData = data;
            renderButtons();
            renderChart();
        })
        .catch(err => console.error('Graf fejl:', err));
}
