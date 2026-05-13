const POLL_INTERVAL_MS = 1000 * 10 * 60;
let pollingInterval = null;

const alertSource = new EventSource('/api/alerts');
alertSource.onmessage = e => {
    const banner = document.getElementById('wind-alert');
    banner.textContent = e.data;
    banner.style.display = 'block';
};

function fetchWeather() {
    const address = document.getElementById('address').value.toLowerCase();
    const resultsDiv = document.getElementById('results');
    resultsDiv.innerHTML = 'Henter vejrdata...';

    clearInterval(pollingInterval);

    fetchAndRender(address, resultsDiv)
        .then(() => updateGraph(address))
        .then(() => {
            pollingInterval = setInterval(() => {
                fetchAndRender(address, resultsDiv)
                    .then(() => updateGraph(address));
            }, POLL_INTERVAL_MS);
        });
}

function fetchAndRender(address, resultsDiv) {
    return fetch(`/api/weather?address=${encodeURIComponent(address)}`)
        .then(res => res.json())
        .then(data => {
            resultsDiv.innerHTML = '';
            data.forEach(entry => {
                const card = document.createElement('div');
                card.className = 'provider-card';
                card.innerHTML = `
                    <h2>${entry.provider}</h2>
                    <p>Temperatur: ${entry.temperature} °C</p>
                    <p>Føles som: ${entry.feelsLike} °C</p>
                    <p>Luftfugtighed: ${entry.humidity} %</p>
                    <p>Vindhastighed: ${entry.windSpeed} m/s</p>
                    <p>Vindretning: ${entry.windDirection}</p>
                    <p>Skydække: ${entry.cloudCover}</p>
                `;
                resultsDiv.appendChild(card);
            });
        })
        .catch(err => {
            resultsDiv.innerHTML = `<p class="error">Fejl: ${err.message}</p>`;
        });
}
