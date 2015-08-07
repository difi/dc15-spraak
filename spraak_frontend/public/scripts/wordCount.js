/*
 Affects ordbruk.html and ordbruk_nn.html
 */
if(url == "/ordbruk") {
    var wordSearchButton = $('#wordSearchButton');
    var wordOwnersList = $('#wordOwnersList');
    var head = $('#wordOwnersListHead');
    var headNN = $('#wordOwnersListHeadNN');
    var word = '';
    wordSearchButton.attr('disabled', 'disabled');
    $('#wordSearch').keyup(function(event) {
        word = $('#wordSearch').val();

        /*
         Enter-button while in text field == click on wordSearchButton.
         */
        if(event.keyCode == 13) {
            wordSearchButton.click();
        }

        /*
         Disable wordSearchButton if search field is empty.
         */
        if(word == '') {
            wordSearchButton.attr('disabled', 'disabled');
        }
        else {
            wordSearchButton.removeAttr('disabled');
        }
    });

    wordSearchButton.click(function () {
        if (word == '') {
            return;
        }

        var list= $('<div></div>');
        $.getJSON('/api/v4/search/' + word, function(data) {

            if(jQuery.isEmptyObject(data.toptags)) {
                head.empty();
                headNN.empty();
                wordOwnersList.append('<p>Ingen treff</p>');
                return;
            }
            $.each(data.toptags, function(owner) {
                list.before('<div class=pure-g><div class="pure-u-1-2"><p>' + capitalize(owner) + '</p></div><div class="pure-u-1-2"><p>' + this.doc_count + '</p></div></div>');
            });
        });

        // Clear list to avoid duplication if button is clicked several times
        head.empty();
        headNN.empty();
        head.append('<div class=pure-g><div class="pure-u-1-2"><p><b>Etat</b></p></div><div class="pure-u-1-2"><p><b>Antall dokumenter som inneholder "' + word + '"</b></p></div></div>');
        headNN.append('<div class=pure-g><div class="pure-u-1-2"><p><b>Etat</b></p></div><div class="pure-u-1-2"><p><b>Antall dokument som inneheld "' + word + '"</b></p></div></div>');
        wordOwnersList.empty();
        wordOwnersList.append(list);
    });

    /*
     Prevent default form behavior.
     Without this, page refreshes on enter.
     */
    $("#searchForm").submit(function(e){
        return false;
    });
}
