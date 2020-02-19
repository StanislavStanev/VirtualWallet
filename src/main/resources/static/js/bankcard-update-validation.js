const errorMessagesForUpdate ={
    cardNumber : "Card number is invalid. (Ex. 1111-1111-1111-1111)",
    cardHolder: "Please enter your name as a Card Holder.",
    expDate : "Please enter a valid Expiration Date. (Ex. 01/01)",
    cardIssuer : "Bank Name cannot be longer than 50 characters.",
    cardCsv : "Please enter a valid CSV Number. Ex. (111)"
};

let cardNumberIsValidForUpdate = false;
let cardHolderIsValidForUpdate = false;
let cardExpDateIsValidForUpdate = false;
let cardIssuerIsValidForUpdate = false;
let cardCsvIsValidForUpdate = false;

const updateCardBtn = document.getElementById("update_Modal");

const enableUpdateBtn = () => updateCardBtn.disabled = false;
const disableUpdateBtn = () => updateCardBtn.disabled = true;

const errorDivActivateForUpdate = (div, message) => div.innerText = message;
const errorDivDeactivateForUpdate = (div) => div.innerText = '';


const validateCardNumberForUpdate = function () {
    let cardNumberErrorDiv = document.getElementById("card-number-error-update");
    let cardNumberValue = document.getElementById("updateCardNumber").value;
    let cardNumberRegExPattern = /^[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}$/;
    let match = cardNumberValue.match(cardNumberRegExPattern);

    if (cardNumberValue.localeCompare("") !== 0 && !match) {
        errorDivActivateForUpdate(cardNumberErrorDiv, errorMessagesForUpdate.cardNumber);
        return false;
    } else {
        errorDivDeactivateForUpdate(cardNumberErrorDiv);
        return true;
    }
};

const validateCardHolderForUpdate = function () {
    let cardHolderErrorDiv = document.getElementById("card-holder-error-update");
    let cardHolderValue = document.getElementById("updateCardHolder").value;
    let cardHolderRegExPattern = /^[A-Z][a-zA-Z]{0,25} [A-Z][a-zA-Z]{2,25}$/;
    let match = cardHolderValue.match(cardHolderRegExPattern);

    if (cardHolderValue.localeCompare('') !== 0 && !match) {
        errorDivActivateForUpdate(cardHolderErrorDiv, errorMessagesForUpdate.cardHolder);
        return false;
    } else {
        errorDivDeactivateForUpdate(cardHolderErrorDiv);
        return true;
    }
};

const validateCardExpDateForUpdate = function () {
    let cardExpDateErrorDiv = document.getElementById("expiration-date-error-update");
    let cardExpDateValue = document.getElementById("updateExpirationDate").value;
    let cardExpDateRegExPattern = /^[0-9]{2}\/[0-9]{2}$/;
    let match = cardExpDateValue.match(cardExpDateRegExPattern);

    if (cardExpDateValue.localeCompare('') !== 0 && !match) {
        errorDivActivateForUpdate(cardExpDateErrorDiv, errorMessagesForUpdate.expDate);
        return false;
    } else {
        errorDivDeactivateForUpdate(cardExpDateErrorDiv);
        return true;
    }
};

const validateCardIssuerForUpdate = function () {
    let cardIssuerErrorDiv = document.getElementById("issuer-error-update");
    let cardIssuerValue = document.getElementById("updateIssuer").value;

    if (cardIssuerValue.localeCompare("") !== 0 && cardIssuerValue.length > 50) {
        errorDivActivateForUpdate(cardIssuerErrorDiv, errorMessagesForUpdate.cardIssuer);
        return false;
    } else {
        errorDivDeactivateForUpdate(cardIssuerErrorDiv);
        return true;
    }
};

const validateCardCsvForUpdate = function () {
    let cardCsvErrorDiv = document.getElementById("csv-error-update");
    let cardCsvValue = document.getElementById("updateCsv").value;
    let cardCsvRegExPattern = /^[0-9]{3}$/;
    let match = cardCsvValue.match(cardCsvRegExPattern);

    if (cardCsvValue.localeCompare("") !== 0 && !match) {
        errorDivActivateForUpdate(cardCsvErrorDiv, errorMessagesForUpdate.cardCsv);
        return false;
    } else {
        errorDivDeactivateForUpdate(cardCsvErrorDiv);
        return true;
    }
};

const validateCardUpdate = function () {
    cardNumberIsValidForUpdate = validateCardNumberForUpdate();
    cardHolderIsValidForUpdate = validateCardHolderForUpdate();
    cardExpDateIsValidForUpdate = validateCardExpDateForUpdate();
    cardIssuerIsValidForUpdate = validateCardIssuerForUpdate();
    cardCsvIsValidForUpdate = validateCardCsvForUpdate();

    if (!cardNumberIsValidForUpdate || !cardHolderIsValidForUpdate || !cardExpDateIsValidForUpdate || !cardIssuerIsValidForUpdate || !cardCsvIsValidForUpdate) {
        disableUpdateBtn();
    } else {
        enableUpdateBtn();
    }
};

updateCardBtn.addEventListener('mouseover', validateCardUpdate);