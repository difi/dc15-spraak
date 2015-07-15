


var url = window.location.href;

$(document).ready(function() {
    if(url === "http://localhost:3002/") {
        $("#home").addClass("active-trail active");
    } else if (url === "http://localhost:3002/total") {
        $("#difi").addClass("active-trail active");
    } else if (url === "http://localhost:3002/complex") {
        $("#complex").addClass("active-trail active");
    } else if (url === "http://localhost:3002/agency") {
        $("#agency").addClass("active-trail active");
    }

    //Same for nynorsk
    if(url === "http://localhost:3002/nn") {
        $("#home").addClass("active-trail active");
    } else if (url === "http://localhost:3002/total_nn") {
        $("#difi").addClass("active-trail active");
    } else if (url === "http://localhost:3002/complex_nn") {
        $("#complex").addClass("active-trail active");
    } else if (url === "http://localhost:3002/agency_nn") {
        $("#agency").addClass("active-trail active");
    }
});

