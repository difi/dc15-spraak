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




var es = function(req,res,next){
    res.es = function(json,cb) {
        client.search(json).then(function (resp) {
            var hits = resp.aggregations;
            if(cb != null)
                cb(hits);
            else
                res.send(hits);
        }, function (err) {
            console.trace(err.message);
        });
    }
    next();
}


router.get("/all", (function(req, res) {
    res.es({
        index: 'spraak',
        body: {
            aggs: all
        }
    });
}))


router.get("/all/:type", (function(req, res) {
    res.es({
        index: 'spraak',
        type: req.params.type,
        body: {
            aggs: all
        }
    });
}))

router.get("/v1/all", (function(req, res) {
    res.es({
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
    });
}))

router.get("/v1/all/:type", (function(req, res) {
    res.es({
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
    });
}))

router.get("/v1/owners", (function(req, res) {
    res.es({
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
    });
}))

router.get("/v1/owner/:owner", (function(req, res) {
    res.es({
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
    });
}))



router.get("/v2/owner/:owner/all", (function(req, res) {
    res.es({
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
    });
}))

router.get("/v2/owners", (function(req, res) {
    res.es({
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
    }, (function (resp) {
            var d = resp.toptags.buckets;
            var l = [];
            for(var i in d){
                console.log(d[i])
                l.push(d[i].key)
            }
            res.send(l);
        })
    );
}))

router.get("/v2/owners/lang", (function(req, res) {
    res.es({
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
    });
}))

module.exports = {
    'router': router,
    'es': es
};
