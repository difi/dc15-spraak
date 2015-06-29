var express = require('express');
var router = express.Router();

var elasticsearch = require('elasticsearch');

var client = new elasticsearch.Client({
    host: 'elasticsearch.difi.local:8080',
    log: 'trace'
});


router.get("/all", (function(req, res) {
    client.search({
        index: 'spraak',
        body: {
            aggs: {
                complexity_nb: {
                    filter: {
                        term: {
                            lang: "nb"
                        }
                    },
                    aggs: {
                        complexity: {
                            stats: {
                                field: "complexity"
                            }
                        }
                    }
                },
                complexity_nn: {
                    filter: {
                        term: {
                            lang: "nn"
                        }
                    },
                    aggs: {
                        complexity: {
                            stats: {
                                field: "complexity"
                            }
                        }
                    }
                },
                lang_terms: {
                    terms: {
                        field: "lang"
                    }
                }
            }
        }
    }).then(function (resp) {
        console.log(resp.aggregations);
        var hits = resp.aggregations;
        res.send(hits);
    }, function (err) {
        console.trace(err.message);
    });
}))


router.get("/all/:type", (function(req, res) {
    client.search({
        index: 'spraak',
        type: req.params.type,
        body: {
            aggs: {
                complexity_nb: {
                    filter: {
                        term: {
                            lang: "nb"
                        }
                    },
                    aggs: {
                        complexity: {
                            stats: {
                                field: "complexity"
                            }
                        }
                    }
                },
                complexity_nn: {
                    filter: {
                        term: {
                            lang: "nn"
                        }
                    },
                    aggs: {
                        complexity: {
                            stats: {
                                field: "complexity"
                            }
                        }
                    }
                },
                lang_terms: {
                    terms: {
                        field: "lang"
                    }
                }
            }
        }    }).then(function (resp) {
        console.log(resp.aggregations);
        var hits = resp.aggregations;
        res.send(hits);
    }, function (err) {
        console.trace(err.message);
    });
}))

router.get("/v1/all", (function(req, res) {
    client.search({
        index: 'spraak',
        body: {
            aggs: {
                toptags: {
                    terms: {
                        field: "type"
                    },
                    aggs: {
                        complexity_nb: {
                            filter: {
                                term: {
                                    lang: "nb"
                                }
                            },
                            aggs: {
                                complexity: {
                                    stats: {
                                        field: "complexity"
                                    }
                                }
                            }
                        },
                        complexity_nn: {
                            filter: {
                                term: {
                                    lang: "nn"
                                }
                            },
                            aggs: {
                                complexity: {
                                    stats: {
                                        field: "complexity"
                                    }
                                }
                            }
                        },
                        lang_terms: {
                            terms: {
                                field: "lang"
                            }
                        }
                    }
                }
            }

        }
    }).then(function (resp) {
        console.log(resp.aggregations);
        var hits = resp.aggregations;
        res.send(hits);
    }, function (err) {
        console.trace(err.message);
    });
}))

router.get("/v1/all/:type", (function(req, res) {
    client.search({
        index: 'spraak',
        type: req.params.type,
        body: {
            aggs: {
                toptags: {
                    terms: {
                        field: "type"
                    },
                    aggs: {
                        complexity_nb: {
                            filter: {
                                term: {
                                    lang: "nb"
                                }
                            },
                            aggs: {
                                complexity: {
                                    stats: {
                                        field: "complexity"
                                    }
                                }
                            }
                        },
                        complexity_nn: {
                            filter: {
                                term: {
                                    lang: "nn"
                                }
                            },
                            aggs: {
                                complexity: {
                                    stats: {
                                        field: "complexity"
                                    }
                                }
                            }
                        },
                        lang_terms: {
                            terms: {
                                field: "lang"
                            }
                        }
                    }
                }
            }
        }}).then(function (resp) {
        console.log(resp.aggregations);
        var hits = resp.aggregations;
        res.send(hits);
    }, function (err) {
        console.trace(err.message);
    });
}))

module.exports = router;
