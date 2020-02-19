const errorMessages = {
    name: "Your name must be between 5 and 128 characters.",
    phone: "Phone number must be between 10 and 25 characters.",
    email: "Email must be valid."
};

const updateProfileBtn = document.getElementById('updateProfileBtn');
const disableButton = () => updateProfileBtn.disabled = true;
const enableButton = () => updateProfileBtn.disabled = false;

let fullnameIsCorrect = false;
let phoneIsCorrect = false;
let emailIsCorrect = false;

const checkFullname = function () {
    const nameInputField = document.getElementById("full-name");
    const nameDiv  = document.getElementById("fullname-error-div");

    let nameValue = nameInputField.value;

    if ((nameValue.length >= 5 && nameValue.length <= 128) || nameValue === '') {
        nameDiv.innerText = '';
        fullnameIsCorrect = true;
    } else {
        nameDiv.innerText = errorMessages.name;
        nameDiv.style.color = 'red';
        fullnameIsCorrect = false;
    }
};

const checkPhone = function () {
    const phoneInputField = document.getElementById("mobile-number");
    const phoneErrorDiv  = document.getElementById("phone-error-div");

    let phoneValue = phoneInputField.value;

    if ((phoneValue.length >= 10 && phoneValue.length <= 25) || phoneValue === '') {
        phoneErrorDiv.innerText = '';
        phoneIsCorrect = true;
    } else {
        phoneErrorDiv.innerText = errorMessages.phone;
        phoneErrorDiv.style.color = 'red';
        phoneIsCorrect = false;
    }
};

const checkEmail = function () {
    const emailInputField = document.getElementById("email");
    const emailErrorDiv  = document.getElementById("email-error-div");

    let emailValue = emailInputField.value;

    if (validateEmail(emailValue) || emailValue === '') {
        emailErrorDiv.innerText = '';
        emailIsCorrect = true;
    } else {
        emailErrorDiv.innerText = errorMessages.email;
        emailErrorDiv.style.color = 'red';
        emailIsCorrect = false;
    }

};

function validateEmail(email) {
    let re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
}

const finalCheck = function () {
    checkFullname();
    checkPhone();
    checkEmail();

    if (fullnameIsCorrect && phoneIsCorrect && emailIsCorrect) {
        enableButton();
    } else {
        disableButton();
    }
};

updateProfileBtn.addEventListener('mouseover', finalCheck);