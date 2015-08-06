var express = require('express');
var router = express.Router();
var extend = require('util')._extend

//Maks antall som kan returneres i en enkelt query.
var pages = 50;

var elasticsearch = require('elasticsearch');

//Alt: docker01.difi.local.8080',

var client = new elasticsearch.Client({
    host: 'elasticsearch.difi.local:8080'
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
    },
    confidences: {
        terms:{
            field:"lang"
        },
        aggs:{
            count: {
                stats: {
                    field: "confidence"
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
            var hits = resp.aggregations;
            if(cb == "raw"){
                res.send(resp);
            }
            else if(cb != null) {
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

//Henter ut oppsamlet info om spesifikk kilde for alle typer data.
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

//Henter ut oppsamlet info om spesifikk alle typer data.
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



//Henter ut totalinfo, mao samlingen av web, filer osv..
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



//Henter ut alle kilder.
router.get("/v3/owners", (function(req, res) {
    res.es({
            index: 'spraak',
            body: {
                aggs: {
                    toptags: {
                        terms: {
                            field: "owner",
                            size: 9999999
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
//Henter ut info om alle språk.
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

//Henter ut info om sannsynlighet for riktig språk.
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





//Henter ut info for et gitt år for alle kilder.
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

//Henter ut info for et gitt år og for en gitt type(e.g. web.)
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

//Henter info for en gitt type.
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
//Søker etter et ord. Dette kan erstattes med v5/search/query-tingen da den gjør det samme med mer.
router.get("/v4/search/:word", (function(req, res) {
    res.es({
        index: 'spraak',
        body: {
            query: {match:{text:{query:req.params.word}}},
            aggs: {
                toptags: {
                    terms: {
                        field: "owner"

                    },
                    aggs: all
                }
            }
        }

    },format);
}));

//Denne kan antakeligvis fjernes og erstattes med søk fra v3.
router.get("/v4/searchwithname/:word/:owner", (function(req, res) {
    console.log("COUNTING AMT OF DOCS WITH WORD " + req.params.word + " WRITTEN BY " +req.params.etat);
    res.es({
        index: 'spraak',
        body: {
            query: {
                filtered: {
                    query: {match:{text:{query:req.params.word}}},
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
                }
            }
        }
    },format);
}));

//Returnerer dokumenter som er i et visst ordintervall.
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

//Returnerer dokumenter av type som er i et visst ordintervall.
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

//Returnerer dokumenter av type fra eier som er i et visst ordintervall.
router.get("/v4/bywordcount/type/:type/:owner/:lower/:higher", (function(req, res) {
    var _type = req.params.type;
    var _owner = req.params.owner;
    var lower = parseInt(req.params.lower);
    var lower = isNaN(lower)? 0 : lower;
    var higher = parseInt(req.params.higher);
    var higher = isNaN(higher)? 9999999 :  higher;
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
                                    term:{owner:_owner}
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


//Returnerer søk etter tekst baser på query.
router.get("/v3/search/query/:query", (function(req, res) {
    var query_split = req.params.query.toLowerCase().split("&");
    var text, must = [],from = 0, size = 10, lower = 0, higher = 99999999999;
    var _lower = 0,_higher = 99999999999;
    for(var i = 0; i < query_split.length; i++){
        var key = query_split[i].split("=")[0];
        var val = query_split[i].split("=")[1];
        console.log(key +": " + val);
        if(key=="text"){
            text = val;
        }else if(key=="source" && val != "all"){
            must.push({term:{"owner":val}});
        }else if(key=="type" && val != "all"){
            must.push({term:{"type":val}});
        }else if(key=="lang" && val != "all"){
            must.push({term:{"lang":val}});
        }else if(key=="domain" && val != "all"){
            must.push({term:{"domain":val}});
        }else if(key=="from"){
            from = parseInt(val);
        }else if(key=="size"){
            size = parseInt(val);
        }else if(key=="mincount"){
            lower = parseInt(val);
        }else if(key=="maxcount"){
            higher = parseInt(val);
        }else if(key=="mincomp"){
            _lower = parseFloat(val);
        }else if(key=="maxcomp"){
            _higher = parseFloat(val);
        }
    }
    must.push({numeric_range:{
            words:{
                gte:lower,
                    lte:higher
            }
        }});
    must.push({numeric_range:{
        complexity:{
            gte:_lower,
            lte:_higher
        }
    }});

    res.es({
        index: 'spraak',
        from: from,
        size: size,
        body: {
            filter:{
                bool: {
                    must: must
                }

            },
            query: {
                match:{
                    text:{
                        query:text
                    }
                }
            }
        }

    },raw);
}));

//Returnerer liste med alle eiere.
router.get("/v3/all/names", (function(req, res) {
    res.es({
        index: 'spraak',
        body: {
            aggs: {
                owners: {
                    terms: {
                        size: 100,
                        field: "owner"
                    },
                    aggs:all
                }
            }
        }
    }, format);
}));

//Returnerer liste med alle domener.
router.get("v3/all/domains/:owner", (function(req, res) {
    res.es({
        index: 'spraak',
        body: {
            aggs: {
                filtered: {
                    filter: {
                        bool: {
                            must: {
                                term: {
                                    owner: req.params.owner
                                }
                            }
                        }
                    },
                    aggs: {
                        domains: {
                            terms: {
                                size: 100,
                                field: "domain"
                            },
                            aggs: all
                        }
                    }
                }
            }
        }
    }, format);
}));

//Returnerer liste med alle domener.
router.get("/v3/all/domains", (function(req, res) {
    res.es({
        index: 'spraak',
        body: {
            aggs: {
                domains: {
                    terms: {
                        size: 100,
                        field: "domain"
                    }
                }
            }
        }
    }, format);
}));

//Returnerer ordfrekvenser for gitt type og eier, max amt ord.
router.get("/v3/stats/type/:type/:lang/:amt", (function(req, res) {
    res.es({
        index: 'spraak',
        body:{
            "size": 0,
            "aggs" : {
                "filtered":{
                    "filter":{
                        "bool":{
                            "must":[
                                {"term":{"type":req.params.type}},
                                {"term":{"lang":req.params.lang}}
                            ]
                        }
                    },
                    "aggs":{
                        "gris" : {
                            "terms" : {
                                "field" : "text",
                                "size":parseInt(req.params.amt)
                            }
                        }
                    }
                }
            }
        }
    },cleanSmall);
}));

//Returnerer ordfrekvenser for gitt språk, max amt ord.
router.get("/v3/stats/all/:lang/:amt", (function(req, res) {
    res.es({
        index: 'spraak',
        body:{
            "size": 0,
            "aggs" : {
                "filtered":{
                    "filter":{
                        "bool":{
                            "must":[
                                {"term":{"lang":req.params.lang}}
                            ]
                        }
                    },
                    "aggs":{
                        "gris" : {
                            "terms" : {
                                "field" : "text",
                                "size":parseInt(req.params.amt)
                            }
                        }
                    }
                }
            }
        }
    },cleanSmall);
}));


//Returnerer ordfrekvenser for gitt språk og eier, max amt ord.
router.get("/v3/stats/owner/:owner/:lang/:amt", (function(req, res) {
    res.es({
        index: 'spraak',
        body:{
            "size": 0,
            "aggs" : {
                "filtered":{
                    "filter":{
                        "bool":{
                            "must":[
                                {"term":{"lang":req.params.lang}},
                                {"term":{"owner":req.params.owner}}

                            ]
                        }
                    },
                    "aggs":{
                        "gris" : {
                            "terms" : {
                                "field" : "text",
                                "size":parseInt(req.params.amt)
                            }
                        }
                    }
                }
            }
        }
    },cleanSmall);
}));

function isNumber(obj) { return !isNaN(parseInt(obj)) && !isNaN(parseFloat(obj));}
var cleanSmall = function(data){

    console.log(data["filtered"]["gris"]["buckets"]);
    var newArray = new Array();
    for(i = 0; i < data["filtered"]["gris"]["buckets"].length; i++){
        var element =  data["filtered"]["gris"]["buckets"][i];
        if(element["key"].length > 4 && !isNumber(element)){
            console.log(element["key"]);
            newArray.push(element);
        }
    }
    data["filtered"]["gris"]["buckets"] = newArray;
    return data;
}

module.exports = {
    'router': router,
    'es': es
};
