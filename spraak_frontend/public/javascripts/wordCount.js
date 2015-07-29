/*
 Affects ordbruk.html and ordbruk_nn.html
 */
if(url == "/ordbruk") {
    var wordSearchButton = $('#wordSearchButton');
    var wordOwnersList = $('#wordOwnersList');
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
        var list = $('<table class="pure-table"><thead><tr><th>Etat</th><th>Antall dokumenter som inneholder "' + word + '"</th></tr></thead><tbody></tbody></table>').attr('id', 'wordOwnersList');
        $.getJSON('/api/v4/search/' + word, function(data) {

            if(jQuery.isEmptyObject(data.toptags)) {
                wordOwnersList.append('<p>Ingen treff</p>');
                return;
            }
            $.each(data.toptags, function(owner) {
                list.append('<tr><td>' + capitalize(owner) + '</td><td>' + this.doc_count + '</td></tr>');
            })
        });
        wordOwnersList.empty(); // Clear table to avoid duplication if button is clicked several times
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
