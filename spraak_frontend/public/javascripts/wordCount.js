/*
    Affects ordbruk.html and ordbruk_nn.html
 */
if(url == "/ordbruk") {
    $('#wordSearchButton').attr('disabled', 'disabled');
    var word = '';

    $('#wordSearch').keyup(function(event) {
        word = $('#wordSearch').val();

        /*
            Enter-button while in text field == click on wordSearchButton.
         */
        if(event.keyCode == 13) {
            $("#wordSearchButton").click();
        }

        /*
            Disable wordSearchButton if search field is empty.
         */
        if(word == '') {
            $('#wordSearchButton').attr('disabled', 'disabled');
        }
        else {
            $('#wordSearchButton').removeAttr('disabled');
        }
    });

    $("#wordSearchButton").click(function () {
        if (word == '') {
            return;
        }
        var list = $('<table class="pure-table"><thead><tr><th>Etat</th><th>Antall dokumenter som inneholder "' + word + '"</th></tr></thead><tbody></tbody></table>').attr('id', 'wordOwnersList');
        var wordList = [];
        var times = owners.length; // counter for API-calls
        $.each(owners, function(k, owner) {
            $.getJSON('/api/v4/searchwithname/' + word + '/' + owner, function(data) {
                var sum = 0;
                /*
                    We have to go through all the different kinds of documents and sum the doc_count for each of them to get the total
                 */
                $.each(data.toptags, function() {
                    sum += this.doc_count;
                });
                wordList.push({owner: capitalize(owner), sum: sum});
                times--; // One API-call done.

                /*
                All API-calls finished.
                 */
                if(times == 0) {
                    wordList = wordList.sort(function(a, b) {
                        return a.sum < b.sum; // Sort wordList on sum DESC
                    });

                    /*
                        Add ordered elements to table.
                     */
                    $.each(wordList, function() {
                        list.append('<tr><td>' + capitalize(this.owner) + '</td><td>' + this.sum + '</td></tr>')
                    })
                }
            })
        });
        $('#wordOwnersList').empty(); // Clear table to avoid duplication if button is clicked several times
        $('#wordOwnersList').append(list);
    });

    /*
    Prevent default form behavior.
    Without this, page refreshes on enter.
     */
    $("#searchForm").submit(function(e){
        return false;
    });
}