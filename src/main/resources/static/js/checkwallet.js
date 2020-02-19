const errorMessages = {
    emptyWalletName: "Wallet name cannot be empty",

};
var walletName = document.getElementById("walletName");
const walletNameErrorDiv = document.getElementById("walletName-error-div");
const createButton = document.getElementById('create');
const disableButton = () => createButton.disabled = true;
const enableButton = () => createButton.disabled = false;

function checkWalletName() {

    if (walletName.value == "") {
        walletNameErrorDiv.innerText = errorMessages.emptyWalletName;
        walletName.focus();
        disableButton();
        return false;
    }

    enableButton();
    walletNameErrorDiv.innerText = "";

    return true;
}

walletName.addEventListener('change', checkWalletName)
createButton.addEventListener('click', checkWalletName);