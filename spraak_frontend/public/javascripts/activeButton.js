


var url = window.location.href;

$(document).ready(function() {
    if(url === "http://localhost:3002/") {
        $("#home").addClass("active");
    } else if (url === "http://localhost:3002/total") {
        $("#difi").addClass("active");
    } else if (url === "http://localhost:3002/complex") {
        $("#complex").addClass("active");
    } else if (url === "http://localhost:3002/agency") {
        $("#agency").addClass("active");
    }
});

