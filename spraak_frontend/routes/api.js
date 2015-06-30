var express = require('express');
var router = express.Router();

var elasticsearch = require('elasticsearch');

var client = new elasticsearch.Client({
    host: 'elasticsearch.difi.local:8080',
    log: 'trace'
});


var all = {
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

router.get("/all", (function(req, res) {
    client.search({
        index: 'spraak',
        body: {
            aggs: all
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
            aggs: all
        }
    }).then(function (resp) {
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
                    aggs: all
                },
                all: {
                    global: {},
                    aggs: all
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
                    aggs: all
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

router.get("/v1/owners", (function(req, res) {
    client.search({
        index: 'spraak',
        body: {
            aggs: {
                toptags: {
                    terms: {
                        field: "owner"
                    },
                    aggs: {
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

router.get("/v1/owner/:owner", (function(req, res) {
    client.search({
        index: 'spraak',
        body: {
            aggs: {
                toptags: {
                    filter: {
                        term: {
                            owner: req.params.owner
                        }
                    },
                    aggs: all
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



router.get("/v2/owner/:owner/all", (function(req, res) {
    client.search({
        index: 'spraak',
        body: {
            query: {
                filtered: {
                    query: {match_all: {}},
                    filter:{
                        term: {owner: req.params.owner}
                    }
                }
            },
            aggs: {
                toptags: {
                    terms: {
                        field: "type"
                    },
                    aggs: all
                },
                all: {
                    global: {},
                    aggs: all
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

router.get("/v2/owners", (function(req, res) {
    client.search({
        index: 'spraak',
        body: {
            aggs: {
                toptags: {
                    terms: {
                        field: "owner"
                    },
                }
            }
        }
    }).then(function (resp) {
        var hits = resp.aggregations;
        var d = resp.aggregations.toptags.buckets;
        var l = [];
        for(var i in d){
            console.log(d[i])
            l.push(d[i].key)
        }
        res.send(l);
    }, function (err) {
        console.trace(err.message);
    });
}))

router.get("/v2/owners/lang", (function(req, res) {
    client.search({
        index: 'spraak',
        body: {
            aggs: {
                toptags: {
                    terms: {
                        field: "owner"
                    },
                    aggs: {
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

module.exports = router;
