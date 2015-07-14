

var array = [];

$html = '<select name="items" id="items" multiple="multiple" size="1" class="chosenElement">';
$html += '<option value="difi">Difi</option>';
$html += '<option value="complex">Kompleksitet</option>';
$html += '<option value="agency">Etater</option>';
$html += '</select>';


$('.html-multi-chosen-select').chosen({ width: "210px" });





$('.html-multi-chosen-select').on('change', function() {

    array = $('.html-multi-chosen-select').chosen().val();

});



$('#button').on("click", function() {
    console.log(array);
    $('.main').addClass("hidden");

    if (array === null) {
        array = [];
        console.log(array.length)
        $('.graphContent1').removeClass('piechartFjas2 piechartFjas piechartDifi');
        document.getElementsByClassName("highcharts-container").parentNode.removeChild(document.getElementsByClassName("graphContent1"));

    }


    $.getScript( "javascripts/comparisonPie.js", function( data, textStatus, jqxhr ) {
        if (array.indexOf('difi') >= 0) {
            console.log("Difi posisjon: "+array.indexOf('difi'));
            $('.graphContent1').addClass("piechartDifi")
        }
        if (array.indexOf('complex') >= 0) {
            console.log("Fjas posisjon: "+array.indexOf('complex'));
            $('.graphContent2').addClass("piechartFjas")
        }
        if (array.indexOf('agency') >= 0) {
            console.log("Fjas2 posisjon: "+array.indexOf('agency'));
            $('.graphContent3').addClass("piechartFjas2")
        }


    });

});
