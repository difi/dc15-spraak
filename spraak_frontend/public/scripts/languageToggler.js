/*
 * Handles language toggling (between bokmål and nynorsk.
 * All pages must have the format:
 *
 * .../pagenameorwhatever for bokmål version
 * .../pagenameorwhatever_nn for nynorsk version
 * for this to work properly
 */

$(document).ready(function() {
    var currentURL = window.location.pathname;
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
