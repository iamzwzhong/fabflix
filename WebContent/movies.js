let searchForm = $("#searchForm");

function handleMoviesResult(resultData) {
    console.log("handleMoviesResult: populating movies table from resultData");

    $("#movies_table_body tr").remove();

    let moviesTableBodyElement = jQuery("#movies_table_body");

    for (let i = 0; i < Math.min(100, resultData.length); i++) {

        let arrActor = String(resultData[i]["movies_actors"]).split(",");
        let arrSid = String(resultData[i]["movies_starIds"]).split(",");

        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + '<a href="single-movie.html?id=' + resultData[i]["movies_id"] + '">' + resultData[i]["movies_title"] + '</a>'+ "</th>";
        rowHTML += "<th>" + resultData[i]["movies_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movies_director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movies_genres"] + "</th>";
        rowHTML += "<th>";
        for (let j =0; j < arrActor.length;j++) {
            rowHTML += '<a href="single-star.html?id=' + arrSid[j] + '">' + arrActor[j] + '</a>' + ','
        }
        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[i]["movies_ratings"] + "</th>";
        rowHTML += "</tr>";

        moviesTableBodyElement.append(rowHTML);
    }
}
/*
jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/movies",
    success: (resultData) => handleMoviesResult(resultData)
});*/

/**
 * Submit the form content with GET method
 * @param formSubmitEvent
 */
function submitSearchForm(formSubmitEvent) {
    console.log("submit search form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    let str = searchForm.serialize();

    $.ajax({
            method: "GET",
            // Serialize the login form to the data sent by POST request
            url: "api/movies?" + str,
            success: (resultData) => handleMoviesResult(resultData)
        }
    );
}

// Bind the submit action of the form to a handler function
searchForm.submit(submitSearchForm);