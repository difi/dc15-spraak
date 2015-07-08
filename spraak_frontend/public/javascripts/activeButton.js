/*

$("#home").on('click', function () {
    $("#difi").removeClass("active");
    $("#complex").removeClass("active");
    $("#agency").removeClass("active");

    $(document).ready(function() {



    });
});


$("#difi").on('click', function () {
    $("#home").removeClass("active");
    $("#complex").removeClass("active");
    $("#agency").removeClass("active");

    $(document).ready(function() {

        $("#difi").addClass("active");

    });
});*/


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




/*$(function(){

 $('.pure-menu-link a').click(function(){

 $('.pure-menu-link.active').removeClass('active');
 $(this).addClass('active');

 });

 });*/

/*
 $(function(){
 $('.pure-menu-link .active a').filter(function(){return this.href==location.href}).parent().addClass('active').siblings().removeClass('active')
 $('.pure-menu-link .active a').click(function(){
 $(this).parent().addClass('active').siblings().removeClass('active')
 })
 });
 */
/*


 $( "#result" ).load( "ajax/test.html #container" , function () {

 });*/

/*


 $(document).ready(function(){
 $('.total').click(function(){
 $('.contentLoad').load('html/total.html');
 });
 });
 */


//$( ".contentLoad" ).load( "html/total.html", function() {
//});
