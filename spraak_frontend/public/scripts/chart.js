// remove _nn from path to make code work on nn pages
var url = window.location.pathname.replace('_nn','');
var owners = [];
/*
 Affects total.html and total_nn.html
 */

var sourcesNames = {
    'web'       : 'Nett',
    'file'      : 'Dokument',
    'form'      : 'Skjema',
    'fb'        : 'Facebook',
    'twitter'   : 'Twitter'
};

if (url === "/total") {
    loadTypeahead(); // ownerTypeahead.js

    /*
     * Elements in total.html
     * Save in vars to avoid duplicate jQuery selectors
     */
    var selectedOwner;
    var lixChart = $('#lixChart');
    var pieChart = $('#piechart');
    var stats = $('#stats');
    var infoTextNN = $('#totalInfoTextNN');
    var infoTextNB = $('#totalInfoText');
    var noHitsMessageNN = $('#noHitsMessageNN');
    var noHitsMessageNB = $('#noHitsMessageNB');
    var ownerSelectButton = $("#ownerSelectButton");
    var ownerSelectTypeahead = $("#ownerSelectTypeahead");

    ownerSelectTypeahead.keyup(function(event) {
        selectedOwner = ownerSelectTypeahead.val();
        /*
         Enter-button while in text field == click on ownerSelectButton.
         */
        if(event.keyCode == 13) {
            ownerSelectButton.click();
        }

        /*
         Disable ownerSelectButton if search field is empty.
         */
        if(selectedOwner == '') {
            ownerSelectButton.attr('disabled', 'disabled');
        }
        else {
            ownerSelectButton.removeAttr('disabled');
        }
    });

    ownerSelectButton.click(function () {
        // Get selected choice from typeahead
        selectedOwner = ownerSelectTypeahead.val();
        if (selectedOwner == '') {
            return;
        }

        $.getJSON('/api/v3/owner/' + selectedOwner + '/all', function (data) {
            if(jQuery.isEmptyObject(data.toptags)) {
                /*
                 Add message when selectedOwner is invalid. Hide charts since they have nothing to show.
                 */
                stats.attr('hidden', 'hidden');
                noHitsMessageNN.text('Fann ingen data for "' + capitalize(selectedOwner) + '".');
                noHitsMessageNB.text('Fant ingen data for "' + capitalize(selectedOwner) + '".');
                return;
            }

            /*
                Where the data is saved
             */
            var nnComplex = [];
            var nbComplex = [];
            var sources = [];
            var bokmal = 0;
            var nynorsk = 0;

            // Get total nn og nb percentage for selectedOwner
            $.each(data.toptags, function() {
                // If no documents exist in language, set to 0
                bokmal += (this.lang_terms.nb != null ? this.lang_terms.nb.doc_count : 0);
                nynorsk += (this.lang_terms.nn != null ? this.lang_terms.nn.doc_count : 0);
            });

            var nnPercentAll = (nynorsk / (bokmal + nynorsk)) * 100;
            var nbPercentAll = (bokmal / (bokmal + nynorsk)) * 100;

            var format = function (v) {
                if (v.complexity_nn.doc_count > 0) {
                    nnComplex.push(v.complexity_nn.avg);
                } else {
                    nnComplex.push(0);
                }
                if (v.complexity_nb.doc_count > 0) {
                    nbComplex.push(v.complexity_nb.avg);
                } else {
                    nbComplex.push(0);
                }
            };

            $.each(data.toptags, function (key) {
                format(this);
                sources.push(getNorwegianSourceName(key));
            });

            /*
             * Add text under piechart depending on nnPercentAll
             */
            if(nnPercentAll < 25) {
                infoTextNN.text(capitalize(selectedOwner) + ' har ikkje oppnådd kravet på 25% nynorsk frå språkrådet.');
                infoTextNB.text(capitalize(selectedOwner) + ' har ikke oppnådd kravet på 25% nynorsk fra språkrådet.');
            }
            else if(nnPercentAll >= 25) {
                infoTextNN.text(capitalize(selectedOwner) + ' har vore flinke!');
                infoTextNB.text(capitalize(selectedOwner) + ' har vært flinke!');
            }

            /*
             * Add relevant text to view
             */
            stats.removeAttr('hidden');
            noHitsMessageNN.text('');
            noHitsMessageNB.text('');
            $('#owner').text(capitalize(selectedOwner));

            loadSourceChart(selectedOwner);
            loadComplexityChart(lixChart, selectedOwner, nnComplex, nbComplex, sources);
            loadPieChart(pieChart, selectedOwner, nnPercentAll, nbPercentAll);
            loadDomainsList(selectedOwner);
            loadDocumentsByWordCount(selectedOwner);

        });
    });
}

function loadDocumentsByWordCount(selectedOwner) {
    var fileData = [];
    var done = 0;
    $.getJSON('/api/v4/bywordcount/type/file/' + selectedOwner + '/0/2500', function(data) {
        $.each(data.owners, function(owner, ownerValue) {
            var nnPercent = 100 * (ownerValue.topterms.file.lang_terms.nn != null ? ownerValue.topterms.file.lang_terms.nn.doc_count : 0) / ownerValue.topterms.file.doc_count;
            fileData.push({name: 'Dokumenter under 10 sider', y: nnPercent})
        });

        $.getJSON('/api/v4/bywordcount/type/file/' + selectedOwner + '/2501/9999999', function(data) {
            $.each(data.owners, function(owner, ownerValue) {
                var nnPercent = 100 * (ownerValue.topterms.file.lang_terms.nn != null ? ownerValue.topterms.file.lang_terms.nn.doc_count : 0) / ownerValue.topterms.file.doc_count;
                fileData.push({name: 'Dokumenter over 10 sider', y: nnPercent})
            });

            $('#documentsByWordCountChart').highcharts({
                chart: {
                    type: 'column'
                },
                title: {
                    text: 'Nynorskandel for dokumenter over og under ti sider'
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
                    shared: true
                },
                credits: {
                    enabled: false
                },
                series: [{
                    name: 'Andel nynorsk',
                    data: fileData,
                    tooltip: {
                        headerFormat: '',
                        pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y:.2f}%</b> nynorsk<br/>'
                    }
                }
                ],
                plotOptions: {
                    series: {
                        borderWidth: 0,
                        dataLabels: {
                            column: {
                                enabled: true,
                                format: '{point.y:.1f}%',
                                style: {
                                    color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                                }
                            }
                        }
                    }
                }
            });
        });
    });



}

function loadPieChart(chartElement, selectedOwner, nnPercentAll, nbPercentAll) {
    chartElement.highcharts({
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false,
            type: 'pie'
        },
        title: {
            text: 'Nynorsk- og bokmålsandelen til ' + capitalize(selectedOwner) + ":"
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
        credits: {
            enabled: false
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
}

function loadComplexityChart(chartElement, selectedOwner, nnComplex, nbComplex, sources) {
    chartElement.highcharts({
        chart: {
            type: 'areaspline'
        },
        title: {
            text: 'Gjennomsnittlig kompleksitetsgrad'
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'top',
            floating: true,
            borderWidth: 1,
            backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'
        },
        xAxis: {
            categories: sources

        },
        yAxis: {
            title: {
                text: 'LIX-score'
            }
        },
        tooltip: {
            shared: true,
            headerFormat: 'LIX-kompleksitet <br/> <b>{point.x}</b> <br/>',
            pointFormat: '<span style="color:{point.color}">{series.name}</span>: <b>{point.y:.2f}</b><br/>'
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

function loadSourceChart(selectedOwner) {
    $.getJSON('/api/v3/owner/' + selectedOwner + '/all', function(data) {
        var data_list = [];
        var error = [];
        $.each(data.toptags, function(source, sourceData) {
            var percentNN = ((sourceData.lang_terms.nn != null ? sourceData.lang_terms.nn.doc_count : 0) / sourceData.doc_count) * 100;
            var avgError = (sourceData.confidences.nn != null ? 1 - sourceData.confidences.nn.avg : 0) + 0.05;
            error.push([percentNN * (1 - avgError), percentNN * (1 + avgError)]);
            data_list.push({name: getNorwegianSourceName(source), y: percentNN});
        });

        $('#nnSourceChart').highcharts({
            chart: {
                type: 'column'
            },
            title: {
                text: 'Nynorskandel for hver kilde'
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
                shared: true
            },
            credits: {
                enabled: false
            },
            series: [{
                name: 'Andel nynorsk',
                data: data_list,
                tooltip: {
                    headerFormat: '',
                    pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y:.2f}%</b> nynorsk<br/>'
                }
            },
                {
                    name: 'Gjennomsnittlig feilestimat',
                    data: error,
                    type: 'errorbar',
                    tooltip: {
                        pointFormat: 'Feilestimat <br/> Innenfor: {point.low:.2f} - {point.high:.2f} %)<br/>',
                    }
                }
            ],
            plotOptions: {
                series: {
                    borderWidth: 0,
                    dataLabels: {
                        column: {
                            enabled: true,
                            format: '{point.y:.1f}%',
                            style: {
                                color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                            }
                        }
                    }
                }
            }
        });
    })
}

/*
 Returns the string with first letter in uppercase
 */
function capitalize(string) {
    return string[0].toUpperCase() + string.substr(1);
}

if(url === "/nynorsk_o_meter") {
    $.getJSON('/api/v3/all', function(data) {
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

function loadDomainsList(selectedOwner) {
    var domainsList = $('#domainsList');
    var dataList = $('<div></div>');
    var head = $('#domainHead');
    var headNN = $('#domainHeadNN');

    $.getJSON('/api/v3/all/domains/' + selectedOwner, function(data) {
        $.each(data.filtered.domains, function(key, val) {
            var nnPercent = 100 * (val.lang_terms.nn != null ? val.lang_terms.nn.doc_count : 0) / val.doc_count;
            dataList.before('<div class=pure-g><div class="pure-u-1-3"><p>' + key + '</p></div><div class="pure-u-1-3"><p>' + val.doc_count + '</p></div><div class="pure-u-1-3"><p>' + nnPercent.toFixed(2) + '</p></div></div>');
        });
    });
    domainsList.empty();
    head.empty();
    headNN.empty();
    head.append('<div class=pure-g><div class="pure-u-1-3"><p><b>Domene</b></p></div><div class="pure-u-1-3"><p><b>Antall dokumenter</b></p></div><div class="pure-u-1-3"><p><b>Prosent nynorsk</b></p></div></div>');
    headNN.append('<div class=pure-g><div class="pure-u-1-3"><p><b>Domene</b></p></div><div class="pure-u-1-3"><p><b>Antal dokument</b></p></div><div class="pure-u-1-3"><p><b>Prosent nynorsk</b></p></div></div>');
    domainsList.append(dataList);
}

function getNorwegianSourceName(dbSourceName) {
    return sourcesNames[dbSourceName];
}

Highcharts.setOptions({
    chart: {
        style: {
            fontFamily: 'Open Sans'
        }
    }
});
