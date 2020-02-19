const typeConstants = {
    all: "ALL",
    incoming: "INCOMING",
    outgoing: "OUTGOING",
};

const errorMessages = {
    recipient: "You can only select a Recipient with Type - Outgoing. Please update your choice.",
    recipientAdmin : "You cannot select a Recipient with Type - Outgoing. Please update your choice.",
    sender: "You cannot select a Sender with Type - Incoming. Please update your choice.",
};

const senderSelect = document.getElementById("sender");
const recipientSelect = document.getElementById("recipient");
const typeSelect = document.getElementById("type");
const searchButton = document.getElementById("searchBtn");
const searchErrorDiv = document.getElementById("search-error-div");

const disableButton = (message) => {
    searchErrorDiv.innerText = message;
    searchErrorDiv.style.color = 'red';
    searchButton.disabled = true
};

const enableButton = () => {
    searchButton.disabled = false;
    searchErrorDiv.innerText = '';
};

const recipientSearchIsWrong = function () {
    let recipientSelectValue = recipientSelect.options[recipientSelect.selectedIndex].value;
    let typeSelectValue = typeSelect.options[typeSelect.selectedIndex].value;

    if (recipientSelectValue.localeCompare('') !== 0 &&
        typeSelectValue.localeCompare(typeConstants.outgoing) !== 0) {
        return true;
    }

    return false;
};

const adminRecipientSearchIsWrong = function () {
    let recipientSelectValue = recipientSelect.options[recipientSelect.selectedIndex].value;
    let senderSelectValue = senderSelect.options[senderSelect.selectedIndex].value;
    let typeSelectValue = typeSelect.options[typeSelect.selectedIndex].value;

    if (recipientSelectValue.localeCompare('') !== 0 &&
        senderSelectValue.localeCompare('') === 0 &&
        typeSelectValue.localeCompare(typeConstants.outgoing) === 0) {
        return true;
    }

    return false;
};

const adminSenderSearchIsWrong = function () {
    let recipientSelectValue = recipientSelect.options[recipientSelect.selectedIndex].value;
    let senderSelectValue = senderSelect.options[senderSelect.selectedIndex].value;
    let typeSelectValue = typeSelect.options[typeSelect.selectedIndex].value;

    if (senderSelectValue.localeCompare('') !== 0 &&
        recipientSelectValue.localeCompare('') === 0 &&
        typeSelectValue.localeCompare(typeConstants.incoming) === 0) {
        return true;
    } else {
        return false;
    }
    ;
};

const validateSearch = function () {
    if (senderSelect === null) {
        if (recipientSearchIsWrong()) {
            disableButton(errorMessages.recipient);
        } else {
            enableButton();
        }
    } else {
        let recipientCheck = adminRecipientSearchIsWrong();
        let senderCheck = adminSenderSearchIsWrong();

        if (recipientCheck) {
            disableButton(errorMessages.recipientAdmin);
        }

        if (senderCheck) {
            disableButton(errorMessages.sender);
        }

        if (!recipientCheck && !senderCheck) {
            enableButton();
        }
    }
};

searchButton.addEventListener('mouseover', validateSearch);