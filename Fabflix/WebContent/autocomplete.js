
/*
 * This function is called by the library when it needs to lookup a query.
 *
 * The parameter query is the query string.
 * The doneCallback is a callback function provided by the library, after you get the
 *   suggestion list from AJAX, you need to call this function to let the library know.
 */
function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated on query: " + query)

    if (sessionStorage.getItem(query)) {
        console.log("retrieving suggestions from cached results")
        let data = sessionStorage.getItem(query)
        let jsonData = JSON.parse(data)
        console.log("Using the following suggestion list: \n" + data)
        doneCallback({suggestions: jsonData})
    }
    else {
        // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
        // with the query data
        console.log("sending AJAX request to backend Java Servlet")
        jQuery.ajax({
            "method": "GET",
            // generate the request url from the query.
            // escape the query string to avoid errors caused by special characters
            "url": "api/autocomplete?query=" + escape(query),
            "success": function(data) {
                // pass the data, query, and doneCallback function into the success handler
                handleLookupAjaxSuccess(data, query, doneCallback)
            },
            "error": function(errorData) {
                console.log("lookup ajax error")
                console.log(errorData)
            }
        })
    }
}


/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 *
 * data is the JSON data string you get from your Java Servlet
 *
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
    console.log("lookup ajax successful")

    // parse the string into JSON
    let jsonData = JSON.parse(data);
    console.log("Using the following suggestion list: \n" + data)

    sessionStorage.setItem(query,data)

    let l = (sessionStorage.length) //keeps cache at 100
    if (l > 100) {
        let s = sessionStorage.key(0)
        sessionStorage.removeItem(s)
    }

    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback( { suggestions: jsonData } );
}


/*
 * This function is the select suggestion handler function.
 * When a suggestion is selected, this function is called by the library.
 *
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
    console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["movieId"])
    window.location.replace("single-movie.html?id=" + suggestion["data"]["movieId"])
}

$('#autocomplete').autocomplete({
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    deferRequestBy: 300,
    minChars: 3,
});


/*
 * do normal full text search if no suggestion is selected
 */
function handleNormalSearch(query) {
    console.log("doing normal search with query: " + query);
        $.ajax({
            method: "GET",
            // Serialize the login form to the data sent by POST request
            url: "api/mainsearch?main=" + query,
            success: (resultData) => handleMoviesResult(resultData)
        }
    );
}

// bind pressing enter key to a handler function
$('#autocomplete').keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode == 13) {
        // pass the value of the input box to the handler function
        handleNormalSearch($('#autocomplete').val())
    }
})
