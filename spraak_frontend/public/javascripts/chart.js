
var bokmal;
var nynorsk;
var nnPercent;
var nbPercent ;

$.getJSON('http://localhost:3002/api/all',function(data) {

    $.each(data.lang_terms, function() {
        $.each(this, function(k, v) {
            if (v.key === "nb") {
                bokmal = v.doc_count;
            } else {
                nynorsk = v.doc_count;

            };
        });
    });
    var nb = parseInt(bokmal);
    var nn = parseInt(nynorsk);
    nnPercent = (nn / (nb + nn))*100;
    nbPercent = (nb / (nb + nn))*100;


    console.log(nbPercent);
    console.log(nnPercent);



    $('#piechart').highcharts({
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
            name: "Brands",
            colorByPoint: true,
            data: [{
                name: "Nynorsk",
                y: nnPercent,
                sliced: true,
                selected: true

            }, {
                name: "Bokmål",
                y: nbPercent


            }]
        }]
    });
});


