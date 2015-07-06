var express = require('express');
var router = express.Router();
var extend = require('util')._extend

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








// Insert number of wasted times here: 3

var _format_bucket = function(data, input){
    var b = data["buckets"];
    for (var n in data["buckets"]) {
        var d = b[n]
        var key = d["key"]
        delete d["key"]
        var r = _format(d, {}, key)
        input = extend(input, r)
    }
    return input;
}

var _format_map = function(data, struct, token){
    for (var i in data) {
        var index = data[i];
        if(index == null){
            index = 0;
        }
        if (!index.hasOwnProperty("buckets")) {
            if (typeof index == "object") {
                var b = _format(index, {}, i);
                if (struct[token] == undefined) {
                    struct[token] = {}
                }
                struct[token] = extend(struct[token], b)
            } else if (i != "doc_count") {
                struct[i] = index;
            } else {
                struct[token] = {}
                struct[token][i] = index
            }
        } else {
            struct[token][i] = {}
            _format_bucket(index, struct[token][i])
        }
    }
}


var _format = function(data, struct, token){
    // General formating
    if(data != null && data["buckets"] != null) {
        struct[token] = {}
        _format_bucket(data, struct[token])
    }else {
        _format_map(data, struct, token);
    }
    return struct
}

var format = function(data){
    // Pivot function
    var ret = {}
    for (var i in data){
        var index = data[i]
        ret = extend(_format(index, {}, i), ret);
    }
    return ret
}


var es = function(req,res,next){
    res.es = function(json,cb) {
        client.search(json).then(function (resp) {
            console.log(resp)
            console.log("nay")
            var hits = resp.aggregations;
            if(cb != null) {
                console.log(hits)
                var s = cb(hits);
                console.log(s)
                res.send(s);
            }else {
                res.send(hits);
            }
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




router.get("/v3/owner/:owner/all", (function(req, res) {
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
    }, format);
}))







router.get("/v3/owners/all", (function(req, res) {
    res.es({
        index: 'spraak',
        body: {
            aggs: {
                owners: {
                    terms: {
                        field: "owner"
                    },
                    aggs: {
                        topterms:{
                            terms: {
                                field: "type"
                            },
                            aggs: all
                        },
                        all:{
                            global:{},
                            aggs: all
                        }
                    }
                }
            }
        }
    }, format);
}))


router.get("/v3/all", (function(req, res) {
    res.es({
        index: 'spraak',
        body: {
            aggs: {
                all: {
                    global: {},
                    aggs: all
                }
            }
        }
    }, format);
}))




router.get("/v3/owners", (function(req, res) {
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

router.get("/v3/owners/lang", (function(req, res) {
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
    }, format);
}))




module.exports = {
    'router': router,
    'es': es
};
