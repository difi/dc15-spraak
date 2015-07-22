


$('.piechartDifi').highcharts({
    chart: {
        plotBackgroundColor: null,
        plotBorderWidth: null,
        plotShadow: false,
        type: 'pie'
    },
    title: {
        text: 'Nynorsk- og bokmålsandelen til Difi:'
    },
    tooltip: {
        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
    },
    plotOptions: {
        pie: {
            allowPointSelect: true,
            cursor: 'pointer',
            dataLabels: {
                enabled: true,
                format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                style: {
                    color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                }
            }
        }
    },
    series: [{
        name: "Andel",
        colorByPoint: true,
        data: [{
            name: "Nynorsk",
            y: 25,
            sliced: true,
            selected: true

        }, {
            name: "Bokmål",
            y: 75


        }]
    }]
});

$('.piechartFjas').highcharts({

    chart: {
        plotBackgroundColor: null,
        plotBorderWidth: null,
        plotShadow: false,
        type: 'pie'
    },
    title: {
        text: 'Nynorsk- og bokmålsandelen til fjas:'
    },
    tooltip: {
        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
    },
    plotOptions: {
        pie: {
            allowPointSelect: true,
            cursor: 'pointer',
            dataLabels: {
                enabled: true,
                format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                style: {
                    color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                }
            }
        }
    },
    series: [{
        name: "Andel",
        colorByPoint: true,
        data: [{
            name: "Nynorsk",
            y: 50,
            sliced: true,
            selected: true

        }, {
            name: "Bokmål",
            y: 50


        }]
    }]
});


$('.piechartFjas2').highcharts({
    chart: {
        plotBackgroundColor: null,
        plotBorderWidth: null,
        plotShadow: false,
        type: 'pie'
    },
    title: {
        text: 'Nynorsk- og bokmålsandelen til Fjas2:'
    },
    tooltip: {
        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
    },
    plotOptions: {
        pie: {
            allowPointSelect: true,
            cursor: 'pointer',
            dataLabels: {
                enabled: true,
                format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                style: {
                    color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                }
            }
        }
    },
    series: [{
        name: "Andel",
        colorByPoint: true,
        data: [{
            name: "Nynorsk",
            y: 35,
            sliced: true,
            selected: true

        }, {
            name: "Bokmål",
            y: 65


        }]
    }]
});