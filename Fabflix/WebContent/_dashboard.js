let add_form = $("#addForm");
let add_movie_form = $("#addMovieForm");

function handleMetaResult(resultData) {
    console.log("handleMoviesResult: populating movies table from resultData");

    $("#meta_table_body tr").remove();

    let metaTableBodyElement = jQuery("#meta_table_body");

    for (let i = 0; i < Math.min(100, resultData.length); i++) {

        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["table_name"] + "</th>";
        rowHTML += "<th>" + resultData[i]["column_name"] + "</th>";
        rowHTML += "<th>" + resultData[i]["column_type"] + "</th>";
        rowHTML += "</tr>";

        metaTableBodyElement.append(rowHTML);
    }
}

function handleAddResult(resultData) {
    let resultDataJson = resultData;

    alert(resultDataJson[resultDataJson.length - 1]["message"]);
}

function handleAddMovieResult(resultData) {
    let resultDataJson = resultData;

    alert(resultDataJson[resultDataJson.length - 1]["message"]);
}

jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/_dashboard", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMetaResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});


function submitAddForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();

    let str = add_form.serialize();

    $.ajax({
            dataType: "json",
            method: "GET",
            url: "api/_dashboard?" + str,
            success: (resultData) => handleAddResult(resultData)
        }
    );
}

function submitAddMovieForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();

    let str = add_movie_form.serialize();

    $.ajax({
            dataType: "json",
            method: "GET",
            url: "api/_dashboard?" + str,
            success: (resultData) => handleAddMovieResult(resultData)
        }
    );
}

// Bind the submit action of the form to a handler function
add_form.submit(submitAddForm);
add_movie_form.submit(submitAddMovieForm);