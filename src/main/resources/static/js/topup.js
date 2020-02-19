const errorMessages = {
    emptyMessage: "Bank statement note cannot be empty.",
    minimumTopUpAmount: "Minimum Top up amount is 10 TLK.",
    csvCannotBeEmpty: "CSV must be 3 numbers."

};

let wallet = document.getElementById('wallet');
let amount = document.getElementById('amount');
let bankNoteDescription = document.getElementById('description');
let noteErrorDiv = document.getElementById("note-error-div");
let amountErrorDiv = document.getElementById("amount-error-div");
let csvErrorDiv = document.getElementById("csv-error-div");
let topUpButton = document.getElementById('topUp');
let csv = document.getElementById('csv');
const disableButton = () => topUpButton.disabled = true;
const enableButton = () => topUpButton.disabled = false;

const getWalletBalance = function () {
    let walletName = jQuery("#wallet").val();
    $.get('/newtransaction/topup/searchbywalletname',
        "term=" + walletName,
        function (xhr) {
        })
        .done(function (xhr) {
            document.getElementById("currentBalance").value = xhr;
            return true;
        })
        .fail(function (xhr) {
            console.log(xhr.status);
            return false;
        });
};

function topUpFields() {

    amount.value = Number(amount.value);

    if (isNaN(amount.value)) {
        amount.value = 0;
    }

    if (amount.value < 10) {
        amountErrorDiv.innerText = errorMessages.minimumTopUpAmount;
        disableButton();
        return false;
    }

    amountErrorDiv.innerText = '';

    if (csv.value.length != 3) {
        csvErrorDiv.innerText = errorMessages.csvCannotBeEmpty;
        disableButton();
        return false;
    }

    csvErrorDiv.innerText = '';

    if (bankNoteDescription.value === "") {
        noteErrorDiv.innerText = errorMessages.emptyMessage;
        disableButton();
        return false;
    }

    noteErrorDiv.innerText = '';

    enableButton();
    return true;

}


wallet.addEventListener('change', getWalletBalance);
amount.addEventListener('mouseover', topUpFields);
amount.addEventListener('change', topUpFields);
bankNoteDescription.addEventListener('mouseover', topUpFields);
bankNoteDescription.addEventListener('change', topUpFields);
csv.addEventListener('mouseover', topUpFields);
csv.addEventListener('change', topUpFields);
topUpButton.addEventListener('click', topUpFields);