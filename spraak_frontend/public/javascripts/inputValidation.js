

var inputValues = [];

$html = '<select name="items" id="items" multiple="multiple" size="1" class="chosenElement">';
$html += '<option value="difi">Difi</option>';
$html += '<option value="complex">Kompleksitet</option>';
$html += '<option value="agency">Etater</option>';
$html += '</select>';


$('.html-multi-chosen-select').chosen({ width: "210px" });


$('#button').on("click", function() {

    inputValues = $('.html-multi-chosen-select').chosen().val();

    if (inputValues === null) {
        inputValues = [];
    }

    console.log(inputValues);
    $('.main').addClass("hidden");

    console.log("før if:" + inputValues);

    if (inputValues.indexOf('difi') >= 0) {

        $('.graphContent1').addClass("piechartDifi")
    } else {
        $('.graphContent1').children('div').remove();
    }

    if (inputValues.indexOf('complex') >= 0) {

        $('.graphContent2').addClass("piechartFjas")
    } else {
        $('.graphContent2').children('div').remove();
    }

    if (inputValues.indexOf('agency') >= 0) {

        $('.graphContent3').addClass("piechartFjas2")
    } else {
        $('.graphContent3').children('div').remove();
    }

    $.getScript( "javascripts/comparisonPie.js", function( data, textStatus, xhr ) {

    });
});
