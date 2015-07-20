/**
 * Created by camp-aka on 20.07.2015.
 */
$(document).ready(function() {
    var currentURL = window.location.href;
    var currentlyAtNynorsk = currentURL.substr(-3) === '_nn';
    $('#nn_toggle').click(function() {
        $('#nn_toggle').attr('href',currentlyAtNynorsk ? '' : currentURL + '_nn');
    });

    $('#nb_toggle').click(function() {
        $('#nb_toggle').attr('href',currentlyAtNynorsk ? currentURL.replace('_nn', '') : '');
    });

    $('#lang_links').find('a').each(function() {
        $(this).toggleClass('active', ($(this).attr('id') == 'nn_toggle' && currentlyAtNynorsk) || ($(this).attr('id') == 'nb_toggle' && !currentlyAtNynorsk));
    });

    $('#home_logo').attr('href', currentlyAtNynorsk ? '/_nn' : '/');
});