function handleMoviesResult(resultData) {
    console.log("handleMoviesResult: populating movies table from resultData");

    let moviesTableBodyElement = jQuery("#movies_table_body");

    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["movies_title"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movies_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movies_director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movies_genres"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movies_actors"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movies_ratings"] + "</th>";
        rowHTML += "</tr>";

        moviesTableBodyElement.append(rowHTML);
    }
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/movies",
    success: (resultData) => handleMoviesResult(resultData)
});