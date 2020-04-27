function updateCart() {
    let str = $(this).attr('id');
    let mid = str.substring(1,10);
    let old_qtn = str.substring(10);
    let old_val = mid + "_" + old_qtn;
    let qtn = document.getElementById("btn"+mid).value;
    let n = mid + "_" + qtn;
    let data = [{name:"new",value:n},{name:"old",value:old_val}];
    console.log(data);

    $.ajax("api/cart", {
        method: "POST",
        data: $.param(data),
        success: (resultData) => handleCartResult(resultData)
    });
}

function deleteCart() {
    let str = $(this).attr('id');
    let mid = str.substring(1,10);
    let old_qtn = str.substring(10);
    let old_val = mid + "_" + old_qtn;
    let data = [{name:"old",value:old_val}];
    console.log(data);

    $.ajax("api/cart", {
        method: "POST",
        data: $.param(data),
        success: (resultData) => handleCartResult(resultData)
    });
}

function handleCartResult(resultData) {

    console.log("handling cart stuff");

    let total = 0;

    let cartTableBodyElement = jQuery("#cart_table_body");
    $("#cart_table_body tr").remove();
    for (let i = 0; i < resultData.length; i++) {
        total += parseFloat(resultData[i]["quantity"]) * 4.99;
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["movies_title"] + "</th>";
        rowHTML += "<th>" + resultData[i]["quantity"] + "</th>";
        rowHTML += "<th>" + "4.99" + "</th>";
        rowHTML += "<th>" + "<input type='number' id='btn" + resultData[i]["movie_id"] + "'>"
            + '<button id="O' + resultData[i]["movie_id"] + resultData[i]["quantity"] + '" class="change"' + '>O</button>'
            + '<button id="X' + resultData[i]["movie_id"] + resultData[i]["quantity"] +  '" class="delete"' + '>X</button>'
            +"</th>";
        rowHTML += "</tr>";

        cartTableBodyElement.append(rowHTML);
    }

    $('button.change').click(updateCart);
    $('button.delete').click(deleteCart);
    $("#total").text("Total: $"+total.toFixed(2));
}

jQuery.ajax({
    dataType: "json",
    method: "POST",
    url: "api/cart",
    success: (resultData) => handleCartResult(resultData)
});

let payment = $('#payment');

function handlePaymentResult(resultDataString) {
    let resultDataJson = resultDataString;

    if (resultDataJson["status"] === "success") {
        window.location.replace("confirmation.html");
    }
    else {
        $("#success").text("PLEASE INPUT VALID INFORMATION");
    }
}

function handlePaymentInfo(paymentEvent) {
    paymentEvent.preventDefault();

    $.ajax("api/payment", {
        method: "POST",
        data: payment.serialize(),
        success: handlePaymentResult
    });
}

payment.submit(handlePaymentInfo);