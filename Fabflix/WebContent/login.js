let login_form = $("#login_form");

function handleLoginResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    if (resultDataJson["status"] === "success") {
        window.location.replace("movies.html");
    } else {
        $("#login_error_message").text(resultDataJson["message"]);
    }
}

function submitLoginForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/login", {
            method: "POST",
            data: login_form.serialize(),
            success: handleLoginResult
        }
    );
}

// Bind the submit action of the form to a handler function
login_form.submit(submitLoginForm);