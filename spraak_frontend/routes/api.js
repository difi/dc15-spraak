var express = require('express');
var router = express.Router();
var extend = require('util')._extend

//Maks antall som kan returneres i en enkelt query.
var pages = 50;

var elasticsearch = require('elasticsearch');


//Alt: docker01.difi.local.8080',

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
        },
        aggs: {
            count: {
                stats: {
                    field: "words"
                }
            }
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
        var index = data[i]
        if (index == null || index["buckets"] == null) {
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
            //console.log(resp)
            if(cb == raw){
                res.send(resp);
            }

            var hits = resp.aggregations;

            if(cb != null) {
                var s = cb(hits);
                res.send(s);
            }
            else
            {
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
            return l;
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
                        size: 50,
                        field: "owner"
                    },
                    aggs: {
                        topterms: {
                            terms: {
                                field: "type"
                            },
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
            },
            searchType: "scan",
            scroll: "10m"
        }, (function (resp) {
            var d = resp.toptags.buckets;
            var l = [];
            for(var i in d){
                console.log(d[i])
                l.push(d[i].key)
            }
            return l;
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


router.get("/v3/owners/confidence", function(req, res){
    res.es({
        index: "spraak",
        body: {
            aggs: {
                confidence: {
                    stats: {field: "confidence"}
                }
            }
        }
    },format)
})





router.get("/v4/owners/all/foryear/:date", (function(req, res) {
    res.es({
        index: 'spraak',
        body: {
            query: {
                filtered: {
                    filter:{
                        bool:{
                            must:{
                                term: {
                                    post_year: req.params.date
                                }
                            }
                        }
                    }
                }
            },
            aggs: {
                owners: {
                    terms: {
                        size: pages,
                        field: "owner"
                    },
                    aggs: {
                        topterms: {
                            terms: {
                                field: "type"
                            },
                            aggs: all
                        }
                    }
                }
            },
            sort:{
                "lang": {
                    order:"asc",
                    mode:"avg"
                }
            }
        }
    }, format);
}))

router.get("/v4/owners/all/yearfortype/:date/:type", (function(req, res) {
    res.es({
        index: 'spraak',
        body: {
            query: {
                filtered: {
                    filter:{
                        bool:{
                            must:[
                                {
                                    term: {type:        req.params.type}
                                },
                                {
                                    term: {post_year:   req.params.date}
                                }

                            ]
                        }
                    }
                }
            },
            aggs: {
                owners: {
                    terms: {
                        size: pages,
                        field: "owner"
                    },
                    aggs: {
                        topterms: {
                            terms: {
                                field: "type"
                            },
                            aggs: all
                        }
                    }
                }
            }
        }
    }, format);
}));


router.get("/v4/owners/all/fortype/:type", (function(req, res) {
    res.es({
        index: 'spraak',
        body: {
            query: {
                filtered: {
                    filter:{
                        bool:{
                            must:[
                                {term: {type: req.params.type}}
                            ]
                        }
                    }
                }
            },
            aggs: {
                owners: {
                    terms: {
                        size: pages,
                        field: "owner"
                    },
                    aggs: {
                        topterms: {
                            terms: {
                                field: "type"
                            },
                            aggs: all
                        }
                    }
                }
            },
            sort:{
                owner: {
                    order:"asc",
                    mode:"avg"
                }
            }
        }
    }, format);
}));
var raw = "raw";

router.get("/v4/search/:word", (function(req, res) {
    res.es({
        index: 'spraak',
        body: {
            query: {
                match:{
                    text:{
                        query:req.params.word
                    }
                }
            }
        }

    },raw);
}));

router.get("/v4/searchwithname/:word/:etat", (function(req, res) {
    res.es({

        index: 'spraak',

            filtered:{
                query: {
                    match: {
                        text: {
                            query: req.params.word
                        }
                    }
                },
                filter:{
                    must:{
                        term:{owner:req.params.owner}
                    }
                }
            }

    },raw);
}));


router.get("/v4/bywordcount/:lower/:higher", (function(req, res) {
    lower = parseInt(req.params.lower);
    lower = isNaN(lower)? 0 : lower;
    higher = parseInt(req.params.higher);
    higher = isNaN(higher)? 9999999 :  higher;
    res.es({
        index: 'spraak',
        body: {
            query: {
                filtered: {
                    filter:{
                        numeric_range:{
                            words:{
                                gte:lower,
                                lte:higher
                            }
                        }
                    }
                }
            },
            aggs: {
                owners: {
                    terms: {
                        size: pages,
                        field: "owner"
                    },
                    aggs: {
                        topterms: {
                            terms: {
                                field: "type"
                            },
                            aggs: all
                        }
                    }
                }
            }
        }
    }, format);
}));


router.get("/v4/bywordcount/type/:type/:lower/:higher", (function(req, res) {
    _type = req.params.type;
    lower = parseInt(req.params.lower);
    lower = isNaN(lower)? 0 : lower;
    higher = parseInt(req.params.higher);
    higher = isNaN(higher)? 9999999 :  higher;
    res.es({
        index: 'spraak',
        body: {
            query: {
                filtered: {
                    filter:{
                        bool:{
                            must:[
                                {
                                    term:{type:_type}
                                },
                                {
                                    numeric_range:{
                                        words:{
                                            gte:lower,
                                                lte:higher
                                        }
                                    }
                                }
                                    ]
                            }
                        }
                    }
                },
            aggs: {
                owners: {
                    terms: {
                        size: pages,
                        field: "owner"
                    },
                    aggs: {
                        topterms: {
                            terms: {
                                field: "type"
                            },
                            aggs: all
                        }
                    }
                }
            }
        }
    }, format);
}));


module.exports = {
    'router': router,
    'es': es
};
