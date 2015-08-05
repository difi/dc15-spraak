
var valid = false;
function validate(source){
    if(source.value != "" && isNaN(source.value)){
        source.style.borderColor = "red";
        valid = false;
    }else{
        valid = valid ? true : false;
        source.style.borderColor = "rgba(123, 180, 235, .5)";
    }
}
var query;
var visible = false;
var start = 0;
function toggleAdvanced(){
    visible = !visible;
    document.getElementById("advanced").style.height= visible? "300px":"0px";
    document.getElementById("advanced").style.visibility = visible ? "visible": "hidden";
}

function search(n){
    document.getElementById("loading").style.visibility = "visible";
    if(n){
        start += n;
        start = start <= 0 ? 0 : start;
    }
    var xml = new XMLHttpRequest();
    var a, b, lang, min, max, mincomp, maxcomp;
    if(visible) {
        a =document.getElementById("kilde").value == "all" ? "" : document.getElementById("kilde").value;
        b = document.getElementById("type").value == "all" ? "" : document.getElementById("type").value;
        lang = document.getElementById("type").value == "all" ? "" : document.getElementById("lang").value;
        min = document.getElementById("minord").value;
        max = document.getElementById("maxord").value;
        mincomp = document.getElementById("minkomp").value;
        maxcomp = document.getElementById("maxkomp").value;
        console.log(maxkomp.value);
    }
    var q = "";
    if(a){
        q +="&source="+a;
    }
    if(b){
        q +="&type="+b;
    }
    if(lang){
        q +="&lang="+lang;
    }
    if(min && parseInt(min)){
        q +="&mincount="+parseInt(min);
    }
    if(max && parseInt(max)){
        q +="&maxcount="+parseInt(max);
    }
    if(mincomp && parseInt(mincomp)){
        q +="&mincomp="+parseInt(mincomp);
    }
    if(maxcomp && parseInt(maxcomp)){
        q +="&maxcomp="+parseInt(maxcomp);
    }
    q+= "&from="+start;
    console.log(q);
    xml.open("GET","/api/v3/search/query/text="+query+q,true);
    xml.onload = function(){
        handleResult(xml.response);
    };
    xml.onerror = function(err){
        console.log(err);
    };
    xml.send();
}

var step = 0;
function fix(arr){
    var r = arr;
    var query_thing = query.split(" ");
    for(var i =0; i < r.length; i++){
        for(var w in query_thing){
            var word = query_thing[w];
            var count = 0;
            s = r[i]["_source"]["text"].split(" ");
            for(var j = 0; j < s.length; j++){
                if(s[j] == word){
                    count++;
                }
            }
            if(!r[i]["counts"])
                r[i]["counts"] = {};
            r[i]["counts"][word] = count;
        }
    }

    return r;
}

function highlightText(text){
    var query_list = query.split(" ");
    var newtext = "";
    var text_split = text.split(/\.| |:|,|_|-|\|/g);
    if(text_split.length == 0)
        console.log(text);
    var neverfound = true;
    for(var i = 0; i < text.length; i++){
        var found = false;
        for(var j = 0; j < query_list.length; j++){
            if(text_split[i]) {
                text_split[i] = text_split[i].replace(/\s/g, "");
                if (text_split[i].startsWith(query_list[j]))
                {
                    newtext += "<span class='highlight'>" + text_split[i] + "</span> ";
                    found = true;
                    neverfound = false;
                    break;
                }
            }
        }
        if(!found  && text_split[i]){
            newtext += text_split[i] + " ";
        }
    }
    if(neverfound){
        console.log("NEVER FOUND: " +text_split);
    }
    return newtext;
}

function handleResult(text){
    document.getElementById("noresults").style.visibility="hidden";
    var obj = JSON.parse(text);
    var arr = obj["hits"]["hits"];

    if(arr.length == 0){
        document.getElementById("loading").style.visibility = "hidden";
        if(start > 0){
            document.getElementById("noresults").style.visibility="visible";
            start -=10;
            return;
        }
        document.getElementById("resultbox").innerHTML = "";
        start = 0;
        var d = document.createElement("div");
        d.setAttribute("class","searchresult");
        var src = document.createElement("div");
        src.setAttribute("class","resultsrc");
        src.setAttribute("style","text-align: center;")
        src.innerHTML = "INGEN RESULTATER";
        var content = document.createElement("div");
        content.setAttribute("class","resultcontent");
        content.setAttribute("style","text-align: center;")
        content.innerHTML = "FANT INGEN RESULTATER FOR <br/>"+ query +"<br/> PÅ <br/>"+document.getElementById("type").options[document.getElementById("type").selectedIndex].innerHTML + "<br/> HOS <br/>"+ document.getElementById("kilde").options[document.getElementById("kilde").selectedIndex].innerHTML.substring(1) ;
        d.appendChild(src);
        d.appendChild(content);
        document.getElementById("resultbox").appendChild(d);
        document.getElementById("forrige").style.visibility="hidden";
        document.getElementById("neste").style.visibility="hidden";
        return;
    }

    document.getElementById("resultbox").innerHTML = "";
    document.getElementById("forrige").style.visibility="visible";
    document.getElementById("neste").style.visibility="visible";

    arr = fix(arr);

    var query_thing = query.split(" ");
    arr = arr.sort(function(a, b){
        var sum = 0;
        for(var i in query_thing ){
            var word = query_thing[i];
            sum += b["counts"][word]-a["counts"][word];
        }
        return sum;
    });

    document.getElementById("resultbox").innerHTML = "Viser elementer f.o.m element " + start + " t.o.m " + (start+arr.length-1) + ". Totalt " +obj["hits"]["total"] +" elementer.";

    for(var i = 0; i < arr.length; i++){
        var sep = document.createElement("div");
        sep.setAttribute("class","separator");
        var cur = arr[i]["_source"];
        var d = document.createElement("div");
            d.setAttribute("class","searchresult");
        var title = document.createElement("div");
            title.setAttribute("class","resulttitle");
            title.innerHTML = "Tittel: " + cur["title"];
        var src = document.createElement("div");
            src.setAttribute("class","resultsrc");
            src.innerHTML = "Kilde: <a href='"+cur["site"]+"'>"+cur["owner"] +"</a> - "+cur["type"];
        var comp = document.createElement("div");
            comp.setAttribute("class","resultcomp");
            comp.innerHTML ="Kompleksitet: " + + parseFloat(cur["complexity"]).toFixed(2);
        var count = document.createElement("div");
            count.setAttribute("class","resultcount");
            count.innerHTML ="Antall ord: " + + cur["words"];
        var content = document.createElement("div");
            content.setAttribute("class","resultcontent");
        var t = produceSummary(cur["title"], cur["text"]);
        if(document.getElementById("type").value=="twitter")
            content.innerHTML += highlightText(t);
        else if(t.length > 50) {
            if(t.length < 600)
                content.innerHTML += "..." + highlightText(t) + "...";
            else
                content.innerHTML+= "..."+ highlightText(t).substring(0,600)+"...";
        }
        else if(cur["text"] > 200)
            content.innerHTML+= "..."+ highlightText(cur["text"].substring(0,600))+"...";
        else
            content.innerHTML += highlightText(cur["text"]);
        d.appendChild(title);
        d.appendChild(src);
        d.appendChild(comp);
        d.appendChild(count);
        d.appendChild(content);
        document.getElementById("resultbox").appendChild(d);
        if(i < arr.length -1)
            document.getElementById("resultbox").appendChild(sep);
    }
    document.getElementById("loading").style.visibility = "hidden";
}

function alfalyze(){
    var rot = 0;
    /*setInterval(function(){
        document.body.style.transform="rotate("+rot+"deg)";
    }, 200);*/
    var img = document.createElement("img");
        img.src = "images/alf.png";
        img.setAttribute("id","alf");
        img.setAttribute("style","position: fixed; left: 30%; top: 35%; width:500px; height: 500px;");

    document.body.appendChild(img);
    setInterval(function(){
        rot++;
        document.getElementById("alf").style["-webkit-filter"]= "hue-rotate("+rot*5+"deg)";
        document.getElementById("alf").style.transform = "rotate("+(-1*rot)+"deg) scale("+((rot*4%100)/100)+")";

    }, 20);
}
function click(event){
    if(valid) {
        alert("Invalid values!")
        return;
    }
    query = document.getElementById("searchtext").value;
    if(query.toLowerCase()=="alf"){
        alfalyze();
        return;
    }
    if(query == "")
        return;
    document.getElementById("loading").style.visibility = "visible";
    search();
};

window.onload=function(){

    loader = document.getElementById("cover");
    loader.style.visibility = "visible";
    var option = document.createElement("option");
    option.setAttribute("value", "all");
    option.innerText = "all";
    document.getElementById("kilde").appendChild(option);
    document.getElementById("thebutton").onclick = function(event){click(event);};

    xml = new XMLHttpRequest();
    xml.open("GET", "/api/v3/all/names", true);
    xml.onload = function(){
        var json = JSON.parse(xml.response);
        for(name in json["owners"]){
            var option = document.createElement("option");
            option.setAttribute("value", name);
            option.innerText = name;
            document.getElementById("kilde").appendChild(option);
        }
        document.getElementById("kilde");
        loader.style.visibility = "hidden";
    };
    xml.send();
};


function split_content_to_sentences(content){
    var content = content.replace("\n",". ");
    return content.split(". ");
}

function split_content_to_paragraphs(content){
    var c = content.split(". ");
    var toret = [];
    i = 0;
    var cur = "";
    if(c.length > 5){
        for(var j = 0; j < c.length; j++){
            cur += c[j]+". ";
            if(i++%3){
                i = 0;
                toret.push(cur);
                cur = "";
            }
        }
    }

    if(cur.length > 0)
        toret.push(cur);
    return toret;
}

function sentences_intersection(sent1, sent2){
    s1 = sent1.split(" ");
    s2 = sent2.split(" ");

    if(s1.length + s2.length == 0)
        return 0;

    return intersection(s1, s2).length / ((s1.length + s2.length) / 2 );
}


function get_sentences_ranks(title, content){
    var sentences = split_content_to_sentences(content);

    var n = sentences.length;

    var query_list = query.split(" ");
    var sentences_dic = {};
    for(var i = 0; i < n; i++){
        var score = 0;
        for(var k =0; k < query_list.length; k++){
            if(query_list[k] && sentences[i].indexOf(query_list[k]) >= 0){
                count = sentences[i].match(new RegExp("\\b" +query_list[k]+"\\b","g"));
                if(count == null)
                    count = 0;
                else
                    count = count.length;
                score += count/sentences[i].split(" ").length;
                if(title.indexOf(query_list[k]) >= 0)
                    score *= 1.5;
                if(isNaN(score)){
                    console.log("Count: " + count);
                    console.log("SentL: " +sentences[i].split(" ").length);
                    console.log("Sent: " +sentences[i]);
                }
            }
        }
        sentences_dic[sentences[i]] = score;
    }
    return sentences_dic;
}
function get_best_sentence(paragraph, sentences_dic){

    var sentences = split_content_to_sentences(paragraph);
    var best_sentence = "";
    var max_value = 0;
    for(var i = 0; i < sentences.length; i++){
        s = sentences[i];
        if(s){
            if(sentences_dic[s] > max_value){
                best_sentence = s;
                max_value = sentences_dic[s];
            }
        }
    }
    if(max_value==0)
        return "";
    return best_sentence;
}


function contains(p, q){
    var query_list = q.split(" ");
    for(var i = 0; i < q.length; i++){
        if(p.indexOf(query_list[i]) >= 0 && query_list[i] != ""){
            return true;
        }
    }
    return false;

}
function get_summary(content, sentences_dic){
    var paragraphs = split_content_to_paragraphs(content);
    var summary = "";
    for(var i = 0; i < paragraphs.length; i++){
        var p = paragraphs[i];
        var sentence = get_best_sentence(p, sentences_dic);
        if(sentence)
            summary+=sentence+". \n";
    }
    return summary;
}

function produceSummary(title, text){
    var dict = get_sentences_ranks(title, text);
    var res = get_summary(text, dict);
    return res;

}





//# This is a naive text summarization algorithm
//# Created by Shlomi Babluki
//# April, 2013



function intersection(a, b)
{
    var result = new Array();
    while( a.length > 0 && b.length > 0 )
    {
        if      (a[0] < b[0] ){ a.shift(); }
        else if (a[0] > b[0] ){ b.shift(); }
        else /* they're equal */
        {
            result.push(a.shift());
            b.shift();
        }
    }

    return result;
}