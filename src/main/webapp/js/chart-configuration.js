/* global jQuery3, view, echartsJenkinsApi, bootstrap5 */

/**
 * Reads the trend configuration from the local storage.
 *
 * @param {String} id - the ID of the configuration
 * @return the configuration or {} if no such configuration is found
 */
EChartsJenkinsApi.prototype.readConfiguration = function (id) {
    try {
        const configuration = localStorage.getItem(id);
        if (configuration) {
            return configuration;
        }
    }
    catch (e) {
        // ignore any errors
    }
    return "{}";
}

/**
 * Configures the content of the trend configuration  dialog.
 *
 * @param {String} suffix - the suffix for the ID of the affected trend configuration dialog
 * @param {Function} fillDialog - a function to fill the configuration dialog with additional values from the JSON
 *     configuration object
 * @param {Function} saveDialog - a function to save the configuration dialog values to the JSON configuration object
 */
EChartsJenkinsApi.prototype.configureTrend = function (suffix, fillDialog, saveDialog) {
    const trendConfiguration = jQuery3('#trend-configuration-' + suffix);
    const numberOfBuildsInput = trendConfiguration.find('#builds-' + suffix);
    const numberOfDaysInput = trendConfiguration.find('#days-' + suffix);
    const useBuildAsDomainCheckBox = trendConfiguration.find('#build-domain-' + suffix);
    const trendLocalStorageId = 'jenkins-echarts-trend-configuration-' + suffix;
    const saveButton = '#save-trend-configuration-' + suffix;

    function setDefaultValues() {
        numberOfBuildsInput.val(50);
        numberOfDaysInput.val(0);
        useBuildAsDomainCheckBox.prop('checked', true);
        if (fillDialog) {
            fillDialog(trendConfiguration, {});
        }
    }

    trendConfiguration.on('show.bs.modal', function (e) {
        const trendJsonConfiguration = echartsJenkinsApi.readConfiguration(trendLocalStorageId);
        if (trendJsonConfiguration === "{}") {
            setDefaultValues();
        }
        else {
            try {
                const jsonNode = JSON.parse(trendJsonConfiguration);
                numberOfBuildsInput.val(jsonNode.numberOfBuilds);
                numberOfDaysInput.val(jsonNode.numberOfDays);
                useBuildAsDomainCheckBox.prop('checked', jsonNode.buildAsDomain === 'true');
                if (fillDialog) {
                    fillDialog(trendConfiguration, jsonNode);
                }
            }
            catch (e) {
                setDefaultValues();
            }
        }
    });

    jQuery3(saveButton).on('click', function (e) {
        const configurationJson = {
            numberOfBuilds: numberOfBuildsInput.val(),
            numberOfDays: numberOfDaysInput.val(),
            buildAsDomain: useBuildAsDomainCheckBox.prop('checked') ? 'true' : 'false'
        };
        if (saveDialog) {
            saveDialog(trendConfiguration, configurationJson);
        }
        localStorage.setItem(trendLocalStorageId, JSON.stringify(configurationJson));
    });

    trendConfiguration.on('keypress', function (e) {
        if (e.which === 13) {
            jQuery3(saveButton).click();
        }
    });
}
