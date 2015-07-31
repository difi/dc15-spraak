/**
 * Created by camp-lsa on 29.07.2015.
 */


var query;
var a;
var b;
function search(){
        document.getElementById("resultbox").innerHTML = "";
        xml = new XMLHttpRequest();
        a = document.getElementById("kilde").value == "all" ? "" : "/"+document.getElementById("kilde").value;
        b = document.getElementById("type").value == "all" ? "" : "/"+document.getElementById("type").value;
        if(a != "" && b != "")
            xml.open("GET", "/api/v5/search/source/type/"+query+a+b+"/0/10", true);
        else if(a!="")
            xml.open("GET", "/api/v5/search/source/"+query+a+"/0/10", true);
        else if(b != "")
            xml.open("GET", "/api/v5/search/type/"+query+b+"/0/10", true);
        else
            xml.open("GET", "/api/v5/search/"+query+"/0/10", true);
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

    for(var i = 0; i < text.length; i++){
        var found = false;
        for(var j = 0; j < query_list.length; j++){
            if(text_split[i]) {
                text_split[i] = text_split[i].replace(/\s/g, "");
                if (text_split[i].startsWith(query_list[j]))
                {
                    newtext += "<span class='highlight'>" + text_split[i] + "</span> ";
                    found = true;
                    break;
                }
            }
        }
        if(!found  && text_split[i]){
            newtext += text_split[i] + " ";
        }
    }
    return newtext;
}

function handleResult(text){
    var obj = JSON.parse(text);
    var arr = obj["hits"]["hits"];
    if(arr.length == 0){
        var d = document.createElement("div");
        d.setAttribute("class","searchresult");
        var src = document.createElement("div");
        src.setAttribute("class","resultsrc");
        src.setAttribute("style","text-align: center;")
        src.innerHTML = "INGEN RESULTATER";
        var content = document.createElement("div");
        content.setAttribute("class","resultcontent");
        content.setAttribute("style","text-align: center;")
        content.innerHTML = "FANT INGEN RESULTATER FOR <br/>"+ query +"<br/> PÅ <br/>"+document.getElementById("type").options[document.getElementById("type").selectedIndex].innerHTML + "<br/> HOS <br/>"+ a.substring(1) ;
        d.appendChild(src);
        d.appendChild(content);
        document.getElementById("resultbox").appendChild(d);
    }

    arr = fix(arr);
    console.log(arr);
    var query_thing = query.split(" ");
    arr = arr.sort(function(a, b){
        var sum = 0;
        for(var i in query_thing ){
            var word = query_thing[i];
            sum += b["counts"][word]-a["counts"][word];
        }
        return sum;
    });
    console.log(arr);
    for(var i = 0; i < arr.length; i++){
        var sep = document.createElement("div");
        sep.setAttribute("class","separator");
        var cur = arr[i]["_source"];
        var d = document.createElement("div");
            d.setAttribute("class","searchresult");
        var src = document.createElement("div");
            src.setAttribute("class","resultsrc");
            src.innerHTML = "Kilde: <a href='"+cur["site"]+"'>"+cur["owner"] +"</a> - "+cur["type"];
        var content = document.createElement("div");
            content.setAttribute("class","resultcontent");
        var t = produceSummary(cur["title"], cur["text"]);
        t = highlightText(t);
        if(b=="twitter")
            content.innerHTML += t;
        else if(t.length > 50) {
            if(t.length < 600)
                content.innerHTML += "..." + t + "...";
            else
                content.innerHTML+= "..."+ t.substring(0,600)+"...";
        }
        else if(cur["text"] > 200)
            content.innerHTML+= "..."+ cur["text"].substring(0,600)+"...";
        else
            content.innerHTML += cur["text"];
        d.appendChild(src);
        d.appendChild(content);
        document.getElementById("resultbox").appendChild(d);
        if(i < arr.length -1)
            document.getElementById("resultbox").appendChild(sep);
    }
    document.getElementById("loading").style.visibility = "hidden";
}

function click(event){
    query = document.getElementById("searchtext").value;
    if(query == "")
        return;
    document.getElementById("loading").style.visibility = "visible";
    search();
}
window.onload=function(){

    var option = document.createElement("option");
    option.setAttribute("value", "all");
    option.innerText = "all";
    document.getElementById("kilde").appendChild(option);

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
        document.getElementById("thebutton").onclick = function(event){click(event);};
        document.getElementById("kilde");
    };
    xml.send();


}


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
    console.log("\n\n\n\n\n");
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