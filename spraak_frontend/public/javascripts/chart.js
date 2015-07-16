var bokmal;
var nynorsk;
var nnPercentAll;
var nbPercentAll;
var nnComplex = [];
var nbComplex = [];
var user = "difi";
var toptags = [];
var url = window.location.href;


if (url === "http://localhost:3002/total" || url === "http://localhost:3002/complex") {
    $.getJSON('http://localhost:3002/api/v3/owner/' + user + '/all', function (data) {

        /*
         //Gets total bokmål and nynorsk data.
         bokmal = data.all.lang_terms.buckets[0].doc_count;
         nynorsk = data.all.lang_terms.buckets[1].doc_count;
         var nb = parseInt(bokmal);
         var nn = parseInt(nynorsk);
         nnPercentAll = (nn / (nb + nn)) * 100;
         nbPercentAll = (nb / (nb + nn)) * 100;
         */
        var format = function (v) {

            if (v.complexity_nn.doc_count > 0) {
                var nn = parseInt(v.complexity_nn.avg);
                nnComplex.push(nn);
            } else {
                nnComplex.push(0);
            }
            if (v.complexity_nb.doc_count > 0) {
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
        if (url === "http://localhost:3002/total") {
            console.log("piechart loaded");

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
                        y: 25,
                        sliced: true,
                        selected: true

                    }, {
                        name: "Bokmål",
                        y: 75


                    }]
                }]
            });

        }

        if (url === "http://localhost:3002/complex") {
            console.log("complex loaded");

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
        }
    });
}



if (url === "http://localhost:3002/agency") {

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
})
}



// This is the version that will work with v3/owners/all eventually
/*
$.getJSON('http://localhost:3002/api/v3/owners/all', function(data) {
    var drilldown_series = [];
    var data_list = [];

    $.each(data.owners, function(owner, ownerData) {
        var percentNN = (ownerData.lang_terms.nn.doc_count / ownerData.doc_count) * 100;
        data_list.push({name: capitalize(owner), y: percentNN, drilldown: owner});

        var drilldown_data = [];
        $.each(ownerData.topterms, function(term, termData) {
            drilldown_data.push([capitalize(term), (termData.lang_terms.nn.doc_count / termData.doc_count) * 100]);
        });

        drilldown_series.push({id: owner, data: drilldown_data});
    });

    //
     A column chart showing the percentage of nynorsk for all "owners"
     And more information if column is clicked
     //
=======
    
>>>>>>> 94cf8d8afca522cab7e7086b488ead8c41b2f1d7
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


if(url === "http://localhost:3002/nynorsk_o_meter" || url === "http://localhost:3002/nynorsk_o_meter_nn") {
    $.getJSON('http://localhost:3002/api/v3/all', function(data) {
        var nnPercent = parseFloat((data.all.lang_terms.nn.doc_count / (data.all.lang_terms.nn.doc_count + data.all.lang_terms.nb.doc_count) * 100).toFixed(2));
        $('#nynorskOMeter').highcharts({

            chart: {
                type: 'gauge',
                plotBackgroundColor: null,
                plotBackgroundImage: null,
                plotBorderWidth: 0,
                plotShadow: false
            },

            title: {
                text: 'Nynorsk-o-meteret'
            },

            pane: {
                startAngle: -120,
                endAngle: 120,
                background: [{
                    backgroundColor: {
                        linearGradient: {x1: 0, y1: 0, x2: 0, y2: 1},
                        stops: [
                            [0, '#FFF'],
                            [1, '#333']
                        ]
                    },
                    borderWidth: 0,
                    outerRadius: '109%'
                }, {
                    backgroundColor: {
                        linearGradient: {x1: 0, y1: 0, x2: 0, y2: 1},
                        stops: [
                            [0, '#333'],
                            [1, '#FFF']
                        ]
                    },
                    borderWidth: 1,
                    outerRadius: '107%'
                }, {
                    // default background
                }, {
                    backgroundColor: '#DDD',
                    borderWidth: 0,
                    outerRadius: '105%',
                    innerRadius: '103%'
                }]
            },
            credits: {
                enabled: false
            },

            // the value axis
            yAxis: {
                min: 0,
                max: 100,

                minorTickInterval: 'auto',
                minorTickWidth: 1,
                minorTickLength: 10,
                minorTickPosition: 'inside',
                minorTickColor: '#666',

                tickPixelInterval: 30,
                tickWidth: 2,
                tickPosition: 'inside',
                tickLength: 10,
                tickColor: '#666',
                labels: {
                    step: 2,
                    rotation: 'auto'
                },
                title: {
                    text: '%'
                },
                plotBands: [{
                    from: 50,
                    to: 100,
                    color: '#55BF3B' // green
                }, {
                    from: 25,
                    to: 50,
                    color: '#DDDF0D' // yellow
                }, {
                    from: 0,
                    to: 25,
                    color: '#DF5353' // red
                }]
            },

            series: [{
                name: 'Andel nynorsk totalt',
                data: [nnPercent],
                tooltip: {
                    valueSuffix: ' %'
                }
            }]

        });
    });
}