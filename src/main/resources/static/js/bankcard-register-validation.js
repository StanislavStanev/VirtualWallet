const errorMessages ={
    cardNumber : "Card number is invalid. (Ex. 1111-1111-1111-1111)",
    cardHolder: "Please enter your name as a Card Holder.",
    expDate : "Please enter a valid Expiration Date. (Ex. 01/01)",
    cardIssuer : "Please enter the Bank Name.",
    cardCsv : "Please enter a valid CSV Number. Ex. (111)"
};

let cardNumberIsValid = false;
let cardHolderIsValid = false;
let cardExpDateIsValid = false;
let cardIssuerIsValid = false;
let cardCsvIsValid = false;

const registerCardBtn = document.getElementById("createCard");

const enableRegisterBtn = () => registerCardBtn.disabled = false;
const disableRegisterBtn = () => registerCardBtn.disabled = true;

const errorDivActivate = (div, message) => div.innerText = message;
const errorDivDeactivate = (div) => div.innerText = '';


const validateCardNumber = function () {
    let cardNumberErrorDiv = document.getElementById("card-number-error");
    let cardNumberValue = document.getElementById("addCardNumber").value;
    let cardNumberRegExPattern = /^[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}$/;
    let match = cardNumberValue.match(cardNumberRegExPattern);

    if (!match) {
        errorDivActivate(cardNumberErrorDiv, errorMessages.cardNumber);
        return false;
    } else {
        errorDivDeactivate(cardNumberErrorDiv);
        return true;
    }
};

const validateCardHolder = function () {
    let cardHolderErrorDiv = document.getElementById("card-holder-error");
    let cardHolderValue = document.getElementById("addCardHolder").value;
    let cardHolderRegExPattern = /^[A-Z][a-zA-Z]{0,25} [A-Z][a-zA-Z]{4,25}$/;
    let match = cardHolderValue.match(cardHolderRegExPattern);

    if (cardHolderValue.localeCompare('') === 0 || !match) {
        errorDivActivate(cardHolderErrorDiv, errorMessages.cardHolder);
        return false;
    } else {
        errorDivDeactivate(cardHolderErrorDiv);
        return true;
    }
};

const validateCardExpDate = function () {
    let cardExpDateErrorDiv = document.getElementById("expiration-date-error");
    let cardExpDateValue = document.getElementById("addExpirationDate").value;
    let cardExpDateRegExPattern = /^[0-9]{2}\/[0-9]{2}$/;
    let match = cardExpDateValue.match(cardExpDateRegExPattern);

    if (cardExpDateValue.localeCompare('') === 0 || !match) {
        errorDivActivate(cardExpDateErrorDiv, errorMessages.expDate);
        return false;
    } else {
        errorDivDeactivate(cardExpDateErrorDiv);
        return true;
    }
};

const validateCardIssuer = function () {
    let cardIssuerErrorDiv = document.getElementById("issuer-error");
    let cardIssuerValue = document.getElementById("addIssuer").value;

    if (cardIssuerValue.localeCompare("") === 0) {
        errorDivActivate(cardIssuerErrorDiv, errorMessages.cardIssuer);
        return false;
    } else {
        errorDivDeactivate(cardIssuerErrorDiv);
        return true;
    }
};

const validateCardCsv = function () {
    let cardCsvErrorDiv = document.getElementById("csv-error");
    let cardCsvValue = document.getElementById("addCsv").value;
    let cardCsvRegExPattern = /^[0-9]{3}$/;
    let match = cardCsvValue.match(cardCsvRegExPattern);

    if (!match) {
        errorDivActivate(cardCsvErrorDiv, errorMessages.cardCsv);
        return false;
    } else {
        errorDivDeactivate(cardCsvErrorDiv);
        return true;
    }
};

const validateCardRegister = function () {
    cardNumberIsValid = validateCardNumber();
    cardHolderIsValid = validateCardHolder();
    cardExpDateIsValid = validateCardExpDate();
    cardIssuerIsValid = validateCardIssuer();
    cardCsvIsValid = validateCardCsv();

    if (!cardNumberIsValid || !cardHolderIsValid || !cardExpDateIsValid || !cardIssuerIsValid || !cardCsvIsValid) {
        disableRegisterBtn();
    } else {
        enableRegisterBtn();
    }
};

registerCardBtn.addEventListener('mouseover', validateCardRegister);