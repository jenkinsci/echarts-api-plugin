window.addEventListener("DOMContentLoaded", () => {
    const dataHolders = document.querySelectorAll(".echarts-api-trend-chart-data-holder");

    dataHolders.forEach(dataHolder => {
        const chartId = dataHolder.getAttribute("data-chart-id");
        const enableLinks = dataHolder.getAttribute("data-enable-links") || false;
        const configurationId = dataHolder.getAttribute("data-configuration-id");

        echartsJenkinsApi.renderConfigurableTrendChart(`${chartId}`, `${enableLinks}`, `${configurationId}`, trendProxy);
    });
});
