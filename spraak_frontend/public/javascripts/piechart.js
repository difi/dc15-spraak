
$(function () {
    $('#piechart').highcharts({
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false,
            type: 'pie'
        },
        title: {
            text: 'Er stemningen på topp angående sammenslåingen av Skuddleiksveg og Strambreifjord kommune?'
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
            name: "Brands",
            colorByPoint: true,
            data: [{
                name: "Tja",
                y: 56.33
            }, {
                name: "Bare hvis Snirklebotfjorden blir med og!",
                y: 24.030000000000005,
                sliced: true,
                selected: true
            }, {
                name: "Per Sandberg",
                y: 10.38
            }, {
                name: "Hugleik",
                y: 4.77

            }]
        }]
    });
});
