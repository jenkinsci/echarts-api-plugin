/* global jQuery */
(function ($) {
    $(document).ready(function () {
        renderCharts($);
    });
})(jQuery);

/**
 * Renders all charts that have the class 'echarts' using ECharts.
 *
 * @param {Object} $ - JQuery
 */
function renderCharts($) {
    /**
     * Renders a trend chart in the a div using ECharts.
     *
     * @param {String} chartDivId - the ID of the div where the chart should be shown in
     */
    function renderPieChart (chartDivId) {
        function isEmpty(str) {
            return (!str || str.length === 0);
        }

        /**
         * Returns the title properties of the chart.
         *
         * @param {String} title - the title
         */
        function getTitle(title) {
            if (!isEmpty(title)) {
                return {
                    text: title,
                    textStyle: {
                        fontWeight: 'normal',
                        fontSize: '16'
                    },
                    left: 'center'
                };
            } else {
                return null;
            }
        }

        const chartPlaceHolder = document.getElementById(chartDivId);
        const model = JSON.parse(chartPlaceHolder.getAttribute('data-chart-model'));
        const title = chartPlaceHolder.getAttribute('data-title');
        const chart = echarts.init(chartPlaceHolder);
        chartPlaceHolder.echart = chart;

        const options = {
            title: getTitle(title),
            tooltip: {
                trigger: 'item',
                formatter: '{b}: {c} ({d}%)'
            },
            legend: {
                orient: 'horizontal',
                x: 'center',
                y: 'bottom',
                type: 'scroll'
            },
            series: [{
                type: 'pie',
                radius: ['30%', '70%'],
                avoidLabelOverlap: false,
                color: model.colors,
                label: {
                    normal: {
                        show: false,
                        position: 'center'
                    },
                    emphasis: {
                        show: false
                    }
                },
                labelLine: {
                    normal: {
                        show: true
                    }
                },
                data: model.data
            }
            ]
        };
        chart.setOption(options);
        chart.resize();
        chart.on('click', function (params) {
            window.location.assign(params.name);
        });

        window.onresize = function() {
            chart.resize();
        };
    }

    const allCharts = $('div.echarts');
    allCharts.each(function () {
        const chart = $(this);
        const id = chart.attr('id');

        renderPieChart(id);
    });
}

