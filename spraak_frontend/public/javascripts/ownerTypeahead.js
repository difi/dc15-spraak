var loadTypeahead = function() {
    var owners = new Bloodhound({
        datumTokenizer: Bloodhound.tokenizers.whitespace,
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        prefetch: '/api/v3/owners'
    });

    $('#ownerSelectTypeahead').typeahead({
            hint: true,
            highlight: true,
            cursor: true,
            minLength: 1
        },
        {
            name: 'owners',
            source: owners
        });
};