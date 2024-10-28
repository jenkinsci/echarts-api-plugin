window.addEventListener("DOMContentLoaded", () => {
    const dataHolders = document.querySelectorAll(".echarts-api-trend-chart-data-holder");

    dataHolders.forEach(dataHolder => {
        const chartId = dataHolder.getAttribute("data-chart-id");
        const enableLinks = dataHolder.getAttribute("data-enable-links");
        const configurationId = dataHolder.getAttribute("data-configuration-id");
        const generatedId = dataHolder.getAttribute("data-generated-id");

        echartsJenkinsApi.renderConfigurableTrendChart(chartId, enableLinks, configurationId, window[`trendProxy${generatedId}`]);
    });
});
