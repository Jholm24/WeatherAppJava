let chart = null;

function updateGraph(address) {
    fetch(`/api/history?address=${encodeURIComponent(address)}`)
        .then(res => res.json())
        .then(data => {
            if (data.length === 0) return;

            const sources = [...new Set(data.map(d => d.source))];
            const labels = [...new Set(data.map(d => new Date(d.time).toLocaleTimeString()))];

            const colors = {
                openweather: 'rgb(54, 162, 235)',
                googleweather: 'rgb(255, 99, 132)'
            };

            const datasets = sources.map(source => {
                const readings = data.filter(d => d.source === source);
                return {
                    label: `Temperatur (${source})`,
                    data: readings.map(d => d.temp),
                    borderColor: colors[source] || 'rgb(75, 192, 192)',
                    borderWidth: 2,
                    tension: 0.3,
                    fill: false
                };
            });

            const allTemps = data.map(d => d.temp);
            const yMin = Math.min(...allTemps) - 5;
            const yMax = Math.max(...allTemps) + 5;

            if (chart) {
                chart.data.labels = labels;
                chart.data.datasets = datasets;
                chart.options.scales.y.min = yMin;
                chart.options.scales.y.max = yMax;
                chart.update();
            } else {
                const ctx = document.getElementById('myChart');
                chart = new Chart(ctx, {
                    type: 'line',
                    data: { labels, datasets },
                    options: {
                        scales: {
                            y: { min: yMin, max: yMax, title: { display: true, text: '°C' } },
                            x: { title: { display: true, text: 'Tid' } }
                        },
                        plugins: {
                            title: { display: true, text: `Temperatur over tid — ${address}` }
                        }
                    }
                });
            }
        })
        .catch(err => console.error('Graf fejl:', err));
}
