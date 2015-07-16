


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
    } else if (url === "http://localhost:3002/nynorsk_o_meter") {
        $("#nynorsk_o_meter").addClass("active-trail active");
    } else if (url === "http://localhost:3002/about") {
        $("#about").addClass("active-trail active");
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
    } else if (url === "http://localhost:3002/nynorsk_o_meter_nn") {
        $("#nynorsk_o_meter").addClass("active-trail active");
    } else if (url === "http://localhost:3002/about_nn") {
        $("#about").addClass("active-trail active");
    }
});

