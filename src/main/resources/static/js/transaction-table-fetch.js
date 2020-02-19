const timeFrames = {
    _7days : "7days",
    _28days : "28days"
};

const lineChartLoad = function (timeframe) {
    let lineCh = document.getElementById('tknSale').getContext("2d");

    fetch(`/user/transactions/${timeframe}`)
        .then(resp => resp.json())
        .then((json) => {
            let labels = json.dateNames;
            let data = json.transactionCounts;

            let chart = new Chart(lineCh, {
                // The type of chart we want to create
                type: 'line',

                // The data for our dataset
                data: {
                    labels: labels,
                    datasets: [{
                        label: "",
                        tension: 0.4,
                        backgroundColor: 'transparent',
                        borderColor: '#2c80ff',
                        pointBorderColor: "#2c80ff",
                        pointBackgroundColor: "#fff",
                        pointBorderWidth: 2,
                        pointHoverRadius: 6,
                        pointHoverBackgroundColor: "#fff",
                        pointHoverBorderColor: "#2c80ff",
                        pointHoverBorderWidth: 2,
                        pointRadius: 6,
                        pointHitRadius: 6,
                        data: data,
                    }]
                },

                // Configuration options go here
                options: {
                    legend: {
                        display: false
                    },
                    maintainAspectRatio: false,
                    tooltips: {
                        callbacks: {
                            title: function (tooltipItem, data) {
                                return 'Date : ' + data['labels'][tooltipItem[0]['index']];
                            },
                            label: function (tooltipItem, data) {
                                return data['datasets'][0]['data'][tooltipItem['index']] + ' Tokens';
                            }
                        },
                        backgroundColor: '#eff6ff',
                        titleFontSize: 13,
                        titleFontColor: '#6783b8',
                        titleMarginBottom: 10,
                        bodyFontColor: '#9eaecf',
                        bodyFontSize: 14,
                        bodySpacing: 4,
                        yPadding: 15,
                        xPadding: 15,
                        footerMarginTop: 5,
                        displayColors: false
                    },
                    scales: {
                        yAxes: [{
                            ticks: {
                                beginAtZero: true,
                                fontSize: 12,
                                fontColor: '#9eaecf',

                            },
                            gridLines: {
                                color: "#e5ecf8",
                                tickMarkLength: 0,
                                zeroLineColor: '#e5ecf8'
                            },

                        }],
                        xAxes: [{
                            ticks: {
                                fontSize: 12,
                                fontColor: '#9eaecf',
                                source: 'auto',
                            },
                            gridLines: {
                                color: "transparent",
                                tickMarkLength: 20,
                                zeroLineColor: '#e5ecf8',
                            },
                        }]
                    }
                }
            });
            }
        )
};

const timeFrameSelectApply = function () {
    let timeFrameSelectValue = timeframeSelectElem.options[timeframeSelectElem.selectedIndex].value;

    if (timeFrameSelectValue === '7 days') {
        lineChartLoad(timeFrames._7days);
    }

    if (timeFrameSelectValue === '4 weeks') {
        lineChartLoad(timeFrames._28days);
    }
};

window.addEventListener('load', lineChartLoad(timeFrames._7days));

const timeframeSelectElem = document.getElementById('timeframe');
timeframeSelectElem.addEventListener('change', timeFrameSelectApply);