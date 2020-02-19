const registerModalBtn = document.getElementById('register_Modal');
const updateModalBtn = document.getElementById('update_Modal');

const addUpdated = function (element) {
    if (!element.innerHTML.includes('updated')) {
        element.innerHTML = element.innerHTML + ' (updated)';
    }
};

const removeUpdated = function (element) {
    let newValue = element.innerHTML.split(" (updated)");
    element.innerHTML = newValue[0];
};

const fillModalData = function (e) {
    let operation = e.target.id.split('_')[0];
    let modalLabels = $(`form#${operation}Modal label`);
    let modalInputFields = $(`form#${operation}Modal input`);
    let currentCardInputFields = $('input.current-card');

    $(`form#${operation} input`).each(function (index, element) {
        let value = element.value === "" ? currentCardInputFields[index].value : element.value;

        if (element.value !== "") {
            addUpdated(modalLabels[index]);
        } else {
            removeUpdated(modalLabels[index]);
        }

        modalInputFields[index].setAttribute("value", value);
    });
};

updateModalBtn.addEventListener('click', fillModalData);
registerModalBtn.addEventListener('click', fillModalData);