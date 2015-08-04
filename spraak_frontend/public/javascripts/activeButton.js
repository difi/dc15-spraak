$(document).ready(function() {
    var url = window.location.pathname.replace('_nn','');

    if(url === "/") {
        $("#home").addClass("active-trail active");
    } else if (url === "/total") {
        $("#difi").addClass("active-trail active");
    } else if (url === "/complex") {
        $("#complex").addClass("active-trail active");
    } else if (url === "/agency") {
        $("#agency").addClass("active-trail active");
    } else if (url === "/nynorsk_o_meter") {
        $("#nynorsk_o_meter").addClass("active-trail active");
    } else if (url === "/about") {
        $("#about").addClass("active-trail active");
    } else if (url === "/ordbruk") {
        $("#ordbruk").addClass("active-trail active");
    } else if (url === "/search") {
        $("#search").addClass("active-trail active");
    }
});
