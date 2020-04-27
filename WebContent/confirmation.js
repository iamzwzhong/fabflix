function handleConfirmResult(resultData) {

    let total = 0;

    let cfTableBodyElement = jQuery("#cf_table_body");
    $("#cf_table_body tr").remove();
    for (let i = 0; i < resultData.length; i++) {
        total += parseFloat(resultData[i]["quantity"]) * 4.99;
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["sales_id"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movies_title"] + "</th>";
        rowHTML += "<th>" + "4.99" + "</th>";
        rowHTML += "<th>" + resultData[i]["quantity"] + "</th>";
        rowHTML += "</tr>";

        cfTableBodyElement.append(rowHTML);
    }

    $("#total").text("Total: $"+total.toFixed(2));
}

jQuery.ajax({
    dataType: "json",
    method: "POST",
    url: "api/confirmation",
    success: (resultData) => handleConfirmResult(resultData)
});