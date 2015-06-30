var bokmal;
var nynorsk;
var nnPercentAll;
var nbPercentAll;
var nnComplex = [];
var nbComplex = [];

$.getJSON('http://localhost:3002/api/v1/all',function(data) {


    //Gets total bokmål and nynorsk data.
    bokmal = data.all.lang_terms.buckets[0].doc_count;
    nynorsk = data.all.lang_terms.buckets[1].doc_count;
    var nb = parseInt(bokmal);
    var nn = parseInt(nynorsk);
    nnPercentAll = (nn / (nb + nn))*100;
    nbPercentAll = (nb / (nb + nn))*100;



    var format = function(v){


        if (v.complexity_nn.doc_count > 0){

            console.log(v.complexity_nn.complexity.avg);
            var nn = parseInt(v.complexity_nn.complexity.avg);
            nnComplex.push(nn);


        } else {
            nnComplex.push(0);
        }

        if  (v.complexity_nb.doc_count > 0) {
            console.log(v.complexity_nb.complexity.avg);
            var nb = parseInt(v.complexity_nb.complexity.avg)
            nbComplex.push(nb);


        }
        else {
            nbComplex.push(0);

        }
    };


      $.each(data.toptags.buckets, function () {


          format(this);
      });

    console.log(nbComplex);
    console.log(nnComplex);


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
                y: nnPercentAll,
                sliced: true,
                selected: true

            }, {
                name: "Bokmål",
                y: nbPercentAll


            }]
        }]
    });


        $('#lixChart').highcharts({
            chart: {
                type: 'areaspline'
            },
            title: {
                text: 'Gjennomsnittlig kompleksitetsgrad nynorsk og bokmål'
            },
            legend: {
                layout: 'vertical',
                align: 'left',
                verticalAlign: 'top',
                x: 570,
                y: 60,
                floating: true,
                borderWidth: 1,
                backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'
            },
            xAxis: {
                categories: [
                    'Web',
                    'Twitter',
                    'Facebook',
                    'Docx',
                    'PDF',
                    'Doc',
                    'ODT'

                ]

            },
            yAxis: {
                title: {
                    text: 'LIX-score'
                }
            },
            tooltip: {
                shared: true,
                valueSuffix: ''
            },
            credits: {
                enabled: false
            },
            plotOptions: {
                areaspline: {
                    fillOpacity: 0.5
                }
            },
            series: [{
                name: 'Nynorsk',
                data: nnComplex
            }, {
                name: 'Bokmål',
                data: nbComplex
            }]
        });



});





