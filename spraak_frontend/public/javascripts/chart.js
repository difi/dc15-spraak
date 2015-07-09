var bokmal;
var nynorsk;
var nnPercentAll;
var nbPercentAll;
var nnComplex = [];
var nbComplex = [];
var user = "difi";
var drilldown_series = [];
var data_list = [];
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
$.getJSON('http://localhost:3002/api/v2/owners', function(data) {
    ownerList = data;
    var completedCalls = 0;
    $.each(ownerList, function(k, owner) {
        var drilldown_data = [];
        $.getJSON('http://localhost:3002/api/v2/owner/' + owner + '/all', function(data) {
            $.each(data.all.lang_terms.buckets, function() {
                if(this.key == "nn") {
                    data_list.push({name: capitalize(owner), y: (this.doc_count / data.all.doc_count) * 100, drilldown: owner});
                }
            });
            $.each(data.toptags.buckets, function(k, langitem) {
                $.each(langitem.lang_terms.buckets, function() {
                    if(this.key == "nn") {
                        drilldown_data.push([capitalize(langitem.key), (this.doc_count / langitem.doc_count) * 100 ]);
                    }
                });
            });
        })
            .done(function() {
                //Inner API call done.
                completedCalls++;
                if(completedCalls == ownerList.length) {
                    // All API calls done
                    // Chart can now be drawn

                    drawAllOwnersChart();
                }
            });
        drilldown_series.push({id: owner, data: drilldown_data});
    });
});

function drawAllOwnersChart() {

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
}
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
 */

/*
Returns the string with first letter in uppercase
 */
function capitalize(string) {
    return string[0].toUpperCase() + string.substr(1);
}







