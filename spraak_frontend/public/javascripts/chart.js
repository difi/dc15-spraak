var bokmal;
var nynorsk;
var nnPercentAll;
var nbPercentAll;
var nnComplex = [];
var nbComplex = [];
var user = "difi";
var toptags = [];

var a = 0;
var b = 0;

$.getJSON('http://localhost:3002/api/v3/owner/'+user+'/all',function(data) {



/*
    //Gets total bokmål and nynorsk data.
    bokmal = data.all.lang_terms.buckets[0].doc_count;
    nynorsk = data.all.lang_terms.buckets[1].doc_count;
    var nb = parseInt(bokmal);
    var nn = parseInt(nynorsk);
    nnPercentAll = (nn / (nb + nn)) * 100;
    nbPercentAll = (nb / (nb + nn)) * 100;
*/




    var format = function(v){

        if (v.complexity_nn.doc_count > 0)  {
            var nn = parseInt(v.complexity_nn.avg);
            nnComplex.push(nn);
        } else {
            nnComplex.push(0);
        }
        if  (v.complexity_nb.doc_count > 0) {

            var nb = parseInt(v.complexity_nb.avg);
            nbComplex.push(nb);
        } else {
            nbComplex.push(0);
        }

    };



    $.each(data.toptags, function (key) {
        format(this);

        if (key === "web") {
            toptags.push("Web");
        } else if (key === "file") {
            toptags.push("Documents")
        } else if (key === "fb") {
            toptags.push("Facebook")
        } else if (key === "twitter") {
            toptags.push("Twitter")
        }


    });



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
                categories: toptags

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

$.getJSON('http://localhost:3002/api/v3/owners/lang', function(data) {
    var drilldown_series = [];
    var data_list = [];


    $.each(data.toptags, function(owner, ownerData) {
        var percentNN = (ownerData.lang_terms.nn.doc_count / ownerData.doc_count) * 100;
        data_list.push({name: capitalize(owner), y: percentNN, drilldown: owner});
        var drilldown_data = [];
        $.getJSON('http://localhost:3002/api/v3/owner/' + owner + "/all", function(data) {
            $.each(data.toptags, function(source, sourceData) {
                if (sourceData.lang_terms.nn != undefined) {
                    drilldown_data.push([capitalize(source), (sourceData.lang_terms.nn.doc_count / sourceData.doc_count) * 100]);
                }
                else {
                    drilldown_data.push([capitalize(source), 0]);
                }
            });
        });
        drilldown_series.push({id: owner, data: drilldown_data});
    });

    console.log(drilldown_series);
    /*
     A column chart showing the percentage of nynorsk for all "owners"
     And more information if column is clicked
     */

    Highcharts.setOptions({
        lang: {
            drillUpText: '<< Tilbake'
        }
    });
    
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
        drilldown: {
            series: drilldown_series
        },
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


/*
Returns the string with first letter in uppercase
 */
function capitalize(string) {
    return string[0].toUpperCase() + string.substr(1);
}







