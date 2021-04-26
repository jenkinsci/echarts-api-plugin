/* global jQuery3, view, echartsJenkinsApi, bootstrap5 */
(function ($) {
    const trendConfiguration = $('#trendConfiguration');
    const numberOfBuildsInput = trendConfiguration.find('#numberOfBuilds');
    const numberOfDaysInput = trendConfiguration.find('#numberOfDays');
    const useBuildAsDomainCheckBox = trendConfiguration.find('#buildDomain');
    const trendLocalStorageId = 'echarts#trendConfiguration';

    trendConfiguration.on('show.bs.modal', function (e) {
        const trendJsonConfiguration = localStorage.getItem(trendLocalStorageId);
        if (trendJsonConfiguration == null) {
            numberOfBuildsInput.val(50);
            numberOfDaysInput.val(0);
            useBuildAsDomainCheckBox.prop('checked', true);
        }
        else {
            const jsonNode = JSON.parse(trendJsonConfiguration);
            numberOfBuildsInput.val(jsonNode.numberOfBuilds);
            numberOfDaysInput.val(jsonNode.numberOfDays);
            useBuildAsDomainCheckBox.prop('checked', jsonNode.buildAsDomain === 'true');
        }
    });

    const saveTrendConfiguration = $('#saveTrendConfiguration');
    saveTrendConfiguration.on('click', function (e) {
        const configurationJson = {
            numberOfBuilds: numberOfBuildsInput.val(),
            numberOfDays: numberOfDaysInput.val(),
            buildAsDomain: useBuildAsDomainCheckBox.prop('checked') ? 'true' : 'false'
        };
        localStorage.setItem(trendLocalStorageId, JSON.stringify(configurationJson));
    });

    trendConfiguration.on('keypress', function(e) {
        if (e.which === 13) {
            $('#saveTrendConfiguration').click();
        }
    });
})(jQuery3);
