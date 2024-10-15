window.addEventListener("DOMContentLoaded", () => {
    const dataHolders = document.querySelectorAll(".echarts-api-chart-setup-data-holder");

    dataHolders.forEach(dataHolder => {
        const chartId = dataHolder.getAttribute("data-chart-id");
        echartsJenkinsApi.configureChart(chartId);
    });
});
