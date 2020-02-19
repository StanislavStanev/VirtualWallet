const errorMessages = {
    recipient: "Such user does not exists",
    minimumSendAmount: "Amount must be greater than 0",
    balanceNotEnough: "Your balance is not enough.",
    emptyRecipient: "Recipient cannot be blank!"

};

let searchType = document.getElementById('searchtype');
let sendConfirmationButton = document.getElementById('send1');
let recepientInputField = document.getElementById('recipient');
let t = jQuery.noConflict(true);
const recipientErrorDiv = document.getElementById("recipient-error-div");
const preSendButton = document.getElementById('send1');
const disableButton = () => preSendButton.disabled = true;
const enableButton = () => preSendButton.disabled = false;
var amountErrorDiv = document.getElementById("amount-error-div");
var recipient = document.getElementById("recipient");
var sendAmountField = document.getElementById("sendAmount");
let wallet = document.getElementById('wallet');
let walletSend = document.getElementById('walletSend');
let confirmationButtonSend = document.getElementById('getConfirmationCode');
let tokenSend = document.getElementById('tokenSend');
let tokenSendLabel = document.getElementById('tokenSendLabel');


const searchExactUserFunction = function () {
    let test123 = jQuery("#recipient").val();
    $.get('/newtransaction/sendmoney/searchbyexactusername',
        "term=" + test123,
        function (xhr) {
            console.log(xhr.status);
        })
        .done(function (xhr) {
            console.log(xhr.status);
            document.getElementById("sendAmount").disabled = false;
            recipientErrorDiv.innerText = '';
            enableButton();
            return true;
        })
        .fail(function (xhr) {
            console.log(xhr.status);
            //document.getElementById("sendAmount").innerText="0.00";
            document.getElementById("sendAmount").disabled = true;
            recipientErrorDiv.innerText = errorMessages.recipient;
            disableButton();
            return false;
        });
};

const searchFunction = function () {
    $(document).ready(function () {

        if ($("#searchtype").val() === 'username') {
            $(function () {
                $("#recipient").autocomplete({
                    source: //"autocomplete.php",
                        '/newtransaction/sendmoney/searchbyusernamegetpicture',
                    minLength: 4,
                    select: function (event, ui) {
                        /*
                        var url = ui.item.id;
                        if(url != '') {
                          location.href = '...' + url;
                        }
                        */
                    },
                    html: true,
                    open: function (event, ui) {
                        $(".ui-autocomplete").css("max-height", 300);
                        $(".ui-autocomplete").css("overflow-y", "auto");
                        $(".ui-autocomplete").css("overflow-x", "hidden");
                        $(".ui-autocomplete").css("z-index", 1000);
                    }
                })
                    .autocomplete("instance")._renderItem = function (ul, item) {
                    return $("<li><div><img src='" + item.pictureUrl + "'><span class='text-exlight'>" + item.username + "</span></div></li>").appendTo(ul);
                };

            });
        }

        if ($("#searchtype").val() === 'email') {
            $(function () {
                $("#recipient").autocomplete({
                    source: //"autocomplete.php",
                        '/newtransaction/sendmoney/searchbyemailgetpicture',
                    minLength: 4,
                    select: function (event, ui) {
                        /*
                        var url = ui.item.id;
                        if(url != '') {
                          location.href = '...' + url;
                        }
                        */
                    },
                    html: true,
                    open: function (event, ui) {
                        $(".ui-autocomplete").css("max-height", 300);
                        $(".ui-autocomplete").css("overflow-y", "auto");
                        $(".ui-autocomplete").css("overflow-x", "hidden");
                        $(".ui-autocomplete").css("z-index", 1000);
                    }
                })
                    .autocomplete("instance")._renderItem = function (ul, item) {
                    return $("<li><div><img src='" + item.pictureUrl + "'><span class='text-exlight'>" + item.username + "</span></div></li>").appendTo(ul);
                };

            });
        }

        if ($("#searchtype").val() === 'phone') {
            $(function () {
                $("#recipient").autocomplete({
                    source: //"autocomplete.php",
                        '/newtransaction/sendmoney/searchbyphonegetpicture',
                    minLength: 4,
                    select: function (event, ui) {
                        /*
                        var url = ui.item.id;
                        if(url != '') {
                          location.href = '...' + url;
                        }
                        */
                    },
                    html: true,
                    open: function (event, ui) {
                        $(".ui-autocomplete").css("max-height", 300);
                        $(".ui-autocomplete").css("overflow-y", "auto");
                        $(".ui-autocomplete").css("overflow-x", "hidden");
                        $(".ui-autocomplete").css("z-index", 1000);
                    }
                })
                    .autocomplete("instance")._renderItem = function (ul, item) {
                    return $("<li><div><img src='" + item.pictureUrl + "'><span class='text-exlight'>" + item.username + "</span></div></li>").appendTo(ul);
                };

            });
        }

    });
};

const searchFunctionOnLoad = function () {
    $(function () {
        $("#recipient").autocomplete({
            source: //"autocomplete.php",
                '/newtransaction/sendmoney/searchbyusernamegetpicture',
            minLength: 4,
            select: function (event, ui) {
                /*
                var url = ui.item.id;
                if(url != '') {
                  location.href = '...' + url;
                }
                */
            },
            html: true,
            open: function (event, ui) {
                $(".ui-autocomplete").css("max-height", 300);
                $(".ui-autocomplete").css("overflow-y", "auto");
                $(".ui-autocomplete").css("overflow-x", "hidden");
                $(".ui-autocomplete").css("z-index", 1000);
            }
        })
            .autocomplete("instance")._renderItem = function (ul, item) {
            return $("<li><div><img src='" + item.pictureUrl + "'><span class='text-exlight'>" + item.username + "</span></div></li>").appendTo(ul);
        };

    });
};

//Copy transaction form data to into confirmation Modal
$('#recipient').change(function () {
    $('#recipientSend').val($(this).val());
});

//Copy transaction form data to into confirmation Modal
// $('#recipient').change(function () {
//
//     if($('#userFromSession').text==$('#recipient').value) {
//         $("#walletReceiverSection").show();
//         console.log($('#userFromSession').text);
//         console.log($('#recipient').val);
//     } else {
//         $("#walletReceiverSection").hide();
//         console.log($('#userFromSession')[0].innerText);
//         console.log($('#recipient')[0].text);
//
//     }
// });

$('#recipient').mouseover(function () {
    $('#recipientSend').val($(this).val());
});

$('#recipient').click(function () {
    $('#recipientSend').val($(this).val());
});

$('#recipient').mouseout(function () {
    $('#recipientSend').val($(this).val());
});

$('#searchTypeSend').val("username");

$('#searchtype').change(function () {
    if ($(this).val()) {
        $('#searchTypeSend').val($(this).val());
    }
});

$('#walletReceiver').change(function () {
    $('#walletToReceive').val($(this).val()).selected;
});

$('#wallet').change(function () {
    $('#walletSend').val($(this).val()).selected;
});

function changeWalletName() {
    walletSend.value = wallet.options[wallet.selectedIndex].value;
}

$('#currentBalanceEdit').change(function () {
    if ($(this).val()) {
        $('#currentBalance').val($(this).val());
    }
});


$('#sendAmount').change(function () {
    $('#sendAmountSend').val($(this).val());
});

$('#note').change(function () {
    $('#noteSend').val($(this).val());
});

$('#send1').on("click", function (e) {
    if (!checkAvailability()) {
        e.stopPropagation();
    }
});

function checkAvailability() {
    var currentBalance = document.getElementById("currentBalance").value;
    var sendAmount = document.getElementById("sendAmount").value;
    currentBalance = Number(currentBalance);
    sendAmount = Number(sendAmount);

    if (isNaN(sendAmount)) {
        sendAmount = 0;
    }

    if (currentBalance < sendAmount) {
        amountErrorDiv.innerText = errorMessages.balanceNotEnough;
        document.getElementById("sendAmount").focus();
        disableButton();
        return false;
    }

    if (recipient.value == "") {
        recipientErrorDiv.innerText = errorMessages.emptyRecipient;
        recipient.focus();
        disableButton();
        return false;
    }

    if (sendAmount < 0.01) {
        amountErrorDiv.innerText = errorMessages.minimumSendAmount;
        document.getElementById("sendAmount").focus();
        disableButton();
        return false;
    }
    enableButton();
    amountErrorDiv.innerText = '';
    recipientErrorDiv.innerText = '';
    return true;
}

function hideReceiverWallet() {
    var currentUserFromSession = document.getElementById("userFromSession");
    var walletReceiverSection = document.getElementById("walletReceiverSection");
    var senderWalletLabel = document.getElementById("senderWalletText");
    var walletToReceiveModal = document.getElementById("walletToReceiveModal");
    var walletToReceive = document.getElementById("walletToReceive");
    if (recepientInputField.value === currentUserFromSession.innerHTML) {
        walletReceiverSection.removeAttribute("hidden");
        senderWalletLabel.innerText = "From";
        walletToReceiveModal.removeAttribute("hidden");
    } else {
        walletReceiverSection.hidden = true;
        walletToReceiveModal.hidden = true;
        senderWalletLabel.innerText = "Choose Wallet";
    }
}

function addChecksum() {
    var checksum = document.getElementById("checksum");
    checksum.value = md5(userFromSession.innerText + recipient.value + sendAmountField.value);
    console.log(checksum.value);
}

const getWalletBalance = function () {
    let walletName = jQuery("#wallet").val();
    $.get('/newtransaction/topup/searchbywalletname',
        "term=" + walletName,
        function (xhr) {
        })
        .done(function (xhr) {
            document.getElementById("currentBalanceEdit").value = xhr;
            document.getElementById("currentBalance").value = xhr;
            return true;
        })
        .fail(function (xhr) {
            console.log(xhr.status);
            return false;
        });
};

const postLargeTransactionCodeRequest = function () {
    $.post('/newtransaction/send-verification-email');
};

const showGetConfirmationButton = function() {
    var sendAmount = document.getElementById("sendAmount").value;
    sendAmount = Number(sendAmount);

    if (isNaN(sendAmount)) {
        sendAmount = 0;
    }

    if(sendAmount>9999.99) {
        confirmationButtonSend.removeAttribute("hidden");
        tokenSend.removeAttribute("hidden");
        tokenSendLabel.removeAttribute("hidden");
        disableButton();
    } else {
        confirmationButtonSend.hidden = true;
        tokenSend.hidden = true;
        tokenSendLabel.hidden = true;
        enableButton();
    }

};

const enableButtonPreSend = function() {
    enableButton();
};

this.addEventListener('load', searchFunctionOnLoad);
searchType.addEventListener('change', searchFunction);
recepientInputField.addEventListener('change', searchExactUserFunction);
recepientInputField.addEventListener('mouseover', searchExactUserFunction);
recepientInputField.addEventListener('change', hideReceiverWallet);
recepientInputField.addEventListener('mouseover', hideReceiverWallet);
preSendButton.addEventListener('mouseover', checkAvailability);
preSendButton.addEventListener('click', changeWalletName);
preSendButton.addEventListener('click', addChecksum);
sendAmountField.addEventListener('change', checkAvailability);
sendAmountField.addEventListener('change', showGetConfirmationButton);
wallet.addEventListener('change', getWalletBalance);
confirmationButtonSend.addEventListener('click', postLargeTransactionCodeRequest);
confirmationButtonSend.addEventListener('click',enableButtonPreSend);

