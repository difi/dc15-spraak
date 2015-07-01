var bokmal;
var nynorsk;
var nnPercentAll;
var nbPercentAll;
var nnComplex = [];
var nbComplex = [];
var user = "difi";
var a = 0;
var b = 0;

$.getJSON('http://localhost:3002/api/v2/owner/'+user+'/all',function(data) {


    //Gets total bokmål and nynorsk data.
    bokmal = data.all.lang_terms.buckets[0].doc_count;
    nynorsk = data.all.lang_terms.buckets[1].doc_count;
    var nb = parseInt(bokmal);
    var nn = parseInt(nynorsk);
    nnPercentAll = (nn / (nb + nn))*100;
    nbPercentAll = (nb / (nb + nn))*100;



    var format = function(v){
        if (v.complexity_nn.doc_count > 0){
            if(v.key === "docx" || v.key === "doc" || v.key === "pdf" || v.key === "odt") {
                a += parseInt(v.complexity_nn.complexity.avg);

            } else {
                var nn = parseInt(v.complexity_nn.complexity.avg);
                nnComplex.push(nn);

            }
        } else {
            if(v.key === "docx" || v.key === "doc" || v.key === "pdf" || v.key === "odt") {
                a+= 0;
            } else {
                nnComplex.push(0);
            }
        }

        if  (v.complexity_nb.doc_count > 0) {
            if((v.key === "docx" || v.key === "doc" || v.key === "pdf" || v.key === "odt")){
                b += parseInt(v.complexity_nb.complexity.avg);
            } else {
                var nb = parseInt(v.complexity_nb.complexity.avg);
                nbComplex.push(nb);
            }
        } else {
            if(v.key === "docx" || v.key === "doc" || v.key === "pdf" || v.key === "odt") {
                b+= 0;
            }
            else {
                nbComplex.push(0);
            }
        }
    };

    $.each(data.toptags.buckets, function () {
          format(this);
    });

    nnComplex.push(a/4);
    nbComplex.push(b/4);



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
            name: "Andel",
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
                    'Dokumenter'
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
                    fillOpacity: 0.1
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

$.getJSON('http://localhost:3002/api/v1/owners' ,function(data) {
    var data_list = [];
    $.each(data.toptags.buckets, function(index, item) {
        var nn_percent = -1;
        $.each(item.lang_terms.buckets, function(index, langitem) {
            if(langitem.key == "nn") {
                nn_percent = (langitem.doc_count / item.doc_count) * 100;
            }
        });
        var name = (item.key)[0].toUpperCase() + item.key.substr(1);
        data_list.push({name: name , y: nn_percent});
    });


    /*
    A column chart showing the percentage of nynorsk for all "owners"
     */
    $('#nnPercentageAllChart').highcharts({
        chart: {
            type: 'column'
        },
        title: {
            text: 'Nynorskandel for hver etat'
        },
        xAxis: {
            type: 'category'
        },
        yAxis: {
            title: {
                text: '% nynorsk'
            }
        },
        legend: {
            enabled: false
        },
        tooltip: {
            shared: true,
            headerFormat: '',
            pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y:.2f}%</b> nynorsk<br/>'
        },
        credits: {
            enabled: false
        },
        series: [{
            name: 'Andel nynorsk',
            data: data_list
        }],
        plotOptions: {
            series: {
                borderWidth: 0,
                dataLabels: {
                    enabled: true,
                    format: '{point.y:.1f}%',
                    style: {
                        color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                    }
                }
            }
        }
    });
});








