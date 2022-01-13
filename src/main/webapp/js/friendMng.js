let stompClient = null;
let intervalIDSentReq;
let intervalIDAcceptReq;
let intervalIDFriends;
let intervalIDBlocks;
let intervalIDAll;
let usernameStart = {'username':'username'};

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    //$("#greetings").html("");
}

function connect(csrf) {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({ "X-CSRF-TOKEN": csrf }, function (frame) {
        setConnected(true);
        //alertify.success('Successfully connected.', 2);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/client/all', function (message) {
            showAllPossibleUsers(JSON.parse(message.body));
        });
        stompClient.subscribe('/user/client/find', function (message) {
            showAllPossibleUsers(JSON.parse(message.body));
        });
        stompClient.subscribe('/user/client/check', function (message) {
            checkedFindUser(JSON.parse(message.body));
        });
        stompClient.subscribe('/user/client/sent-req', function (message) {
            showSentRequests(JSON.parse(message.body));
        });
        stompClient.subscribe('/user/client/accept-req', function (message) {
            showAcceptRequests(JSON.parse(message.body));
        });
        stompClient.subscribe('/user/client/friends', function (message) {
            showFriends(JSON.parse(message.body));
        });
        stompClient.subscribe('/user/client/blocked', function (message) {
            showBlocked(JSON.parse(message.body));
        });
        stompClient.subscribe('/user/friend/removed', function (message) {
            friendRemoved(JSON.parse(message.body));
        });
        stompClient.subscribe('/user/client/invite-chat', function (message) {
            chatFriend(JSON.parse(message.body));
        });
        stompClient.subscribe('/user/client/chatting', function (message) {
            alreadyChatting(JSON.parse(message.body));
        });
        stompClient.subscribe('/user/client/send', function (message) {
            getChatMessage(JSON.parse(message.body));
        });
        stompClient.subscribe('/user/client/chat-discon', function (message) {
            chatDisconAlert(JSON.parse(message.body));
        });

    });

    intervalIDSentReq = setInterval(sentReqRequest, 1000);
    intervalIDAcceptReq = setInterval(acceptReqRequest, 1000);
    intervalIDFriends = setInterval(friendsRequest, 1000);
    intervalIDBlocks = setInterval(blocksRequest, 1000);
    intervalIDAll = setInterval(allPossibleRequest, 1000);
}

function allPossibleRequest() {
    if (stompClient !== null) {
        if (Object.keys(usernameStart).length !== 0) {
            stompClient.send("/app/client/find", {}, JSON.stringify(usernameStart));
        }

    }
}

function blocksRequest() {
    if (stompClient !== null) {
        stompClient.send("/app/client/blocked", {}, {});
    }
}

function friendsRequest() {
    if (stompClient !== null) {
        stompClient.send("/app/client/friends", {}, {});
    }
}

function sentReqRequest() {
    if (stompClient !== null) {
        stompClient.send("/app/client/sent-req", {}, {});
    }
}

function acceptReqRequest() {
    if (stompClient !== null) {
        stompClient.send("/app/client/accept-req", {}, {});
    }
}

function friendRemoved(message) {
    alertify.warning('User ' + message.username + ' removed you from friend list!', 3);
}

function disconnect() {
    disconnectChat()
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    clearInterval(intervalIDSentReq);
    clearInterval(intervalIDAcceptReq);
    clearInterval(intervalIDFriends);
    clearInterval(intervalIDBlocks);
    clearInterval(intervalIDAll);
    console.log("Disconnected");
    //alertify.success('Successfully disconnected.', 2);
    $("#sentReqTable").html("");
    $("#acceptReqTable").html("");
    $("#allTable").html("");
    $("#friendsTable").html("");
    $("#blockedTable").html("");
}

function showSentRequests(message) {
    let sentReqTable = $("#sentReqTable");

    sentReqTable.html("");

    for (let i = 0; i < message.length; i++) {
        if (message[i].username.localeCompare($("#loggedUser").html()) === 0) continue;

        sentReqTable.append(
            "<tr>" +
            "<td class='user-tab'>" + message[i].username + "</td>" +
            "<td class='button-remove-request-tab'><button " + ($("#btn-remove-request-hide").prop('disabled') === true ? 'disabled' : 'enabled') + " class='btn btn-primary btn-remove-request' onclick=\"removeSentRequest('" + message[i].username + "')\">Remove request</button></td>" +
            "</tr>");
    }
}

function showAcceptRequests(message) {
    let acceptReqTable = $("#acceptReqTable");

    acceptReqTable.html("");

    for (let i = 0; i < message.length; i++) {
        if (message[i].username.localeCompare($("#loggedUser").html()) === 0) continue;

        acceptReqTable.append(
            "<tr>" +
            "<td class='user-tab'>" + message[i].username + "</td>" +
            "<td class='button-accept-request-tab'><button " + ($("#btn-accept-request-hide").prop('disabled') === true ? 'disabled' : 'enabled') + " class='btn btn-primary btn-accept-request' onclick=\"acceptFriend('" + message[i].username + "')\">Accept friendship</button></td>" +
            "<td class='button-decline-request-tab'><button " + ($("#btn-decline-request-hide").prop('disabled') === true ? 'disabled' : 'enabled') + " class='btn btn-primary btn-decline-request' onclick=\"declineFriend('" + message[i].username + "')\">Decline</button></td>" +
            "</tr>");
    }
}

function showAllPossibleUsers(message) {
    let allTable = $("#allTable");

    allTable.html("");

    for (let i = 0; i < message.length; i++) {
            if (message[i].username.localeCompare($("#loggedUser").html()) === 0) continue;

            let asking = message[i].asking;
            let requested = message[i].requested;

            if (asking) {
                allTable.append(
                    "<tr>" +
                    "<td class='user-tab'>" + message[i].username + "</td>" +
                    "<td class='button-accept-request-tab'><button " + ($("#btn-accept-request-hide").prop('disabled') === true ? 'disabled' : 'enabled') + " class='btn btn-primary btn-accept-request' onclick=\"acceptFriend('" + message[i].username + "')\">Accept friendship</button></td>" +
                    "<td class='button-block-tab'><button " + ($("#btn-block-hide").prop('disabled') === true ? 'disabled' : 'enabled') + " class='btn btn-primary btn-block' onclick=\"blockUser('" + message[i].username + "')\">Block user</button></td>" +
                    "</tr>");
            } else if (requested) {
                allTable.append(
                    "<tr>" +
                    "<td class='user-tab'>" + message[i].username + "</td>" +
                    "<td class='button-already-friend-tab'><button disabled class='btn btn-primary btn-already-friend' onclick=\"alreadyFriend('" + message[i].username + "')\">Already requested</button></td>" +
                    "<td class='button-block-tab'><button " + ($("#btn-block-hide").prop('disabled') === true ? 'disabled' : 'enabled') + " class='btn btn-primary btn-block' onclick=\"blockUser('" + message[i].username + "')\">Block user</button></td>" +
                    "</tr>");
            } else {
                allTable.append(
                    "<tr>" +
                    "<td class='user-tab'>" + message[i].username + "</td>" +
                    "<td class='button-add-friend-tab'><button " + ($("#btn-add-friend-hide").prop('disabled') === true ? 'disabled' : 'enabled') + " class='btn btn-primary btn-add-friend' onclick=\"addFriend('" + message[i].username + "')\">Add friend</button></td>" +
                    "<td class='button-block-tab'><button " + ($("#btn-block-hide").prop('disabled') === true ? 'disabled' : 'enabled') + " class='btn btn-primary btn-block' onclick=\"blockUser('" + message[i].username + "')\">Block user</button></td>" +
                    "</tr>");
            }

    }
}

function showFriends(message) {
    let friendsTable = $("#friendsTable");

    friendsTable.html("");

    for (let i = 0; i < message.length; i++) {

        let username = message[i].username;
        let status = message[i].status;

        friendsTable.append(
            "<tr>" +
            "<td class='status-tab'><span title=" + (status === 0 ? ('Offline') : (status === 1 ? ('Online') : ('Playing'))) + " class=\"indicator " + (status === 0 ? 'offline' : (status === 1 ? 'online' : 'in-game')) + " \"/></td>" +
            "<td class='user-tab'>" + username + "</td>" +
            "<td class='button-remove-friend-tab'><button " + ($("#btn-remove-friend-hide").prop('disabled') === true ? 'disabled' : 'enabled') + " class='btn btn-primary btn-remove-friend' onclick=\"removeFriend('" + message[i].username + "')\">Remove</button></td>" +
            "</tr>");
    }
}

function showBlocked(message) {
    let blockedTable = $("#blockedTable");

    blockedTable.html("");

    for (let i = 0; i < message.length; i++) {

        let username = message[i].username;

        blockedTable.append(
            "<tr>" +
            "<td class='user-tab'>" + username + "</td>" +
            "<td class='button-remove-block-tab'><button " + ($("#btn-remove-block-hide").prop('disabled') === true ? 'disabled' : 'enabled') + " class='btn btn-primary btn-remove-block-hide' onclick=\"removeBlock('" + message[i].username + "')\">Remove</button></td>" +
            "</tr>");
    }
}

function removeFriend(username) {
    $(".btn-remove-friend").attr('disabled', true);

    let value = {'username': username};
    stompClient.send("/app/friend/remove", {}, JSON.stringify(value));

    alertify.success("User " + username + " has been removed from friend list.", 3);
}

function addFriend(username) {
    $("#btn-add-friend-hide").attr('disabled', true);
    $(".btn-add-friend").attr('disabled', true);

    let value = {'username': username};
    stompClient.send("/app/friend/add", {}, JSON.stringify(value));

    alertify.success("Friend request to user " + username + " was sent.", 3);

}

function acceptFriend(username) {
    $(".btn-accept-request").attr('disabled', true);

    let value = {'username': username};
    stompClient.send("/app/friend/accept", {}, JSON.stringify(value));

    alertify.success("User " + username + " has been added to friends.", 3);

}

function declineFriend(username) {
    $(".btn-decline-request").attr('disabled', true);

    let value = {'username': username};
    stompClient.send("/app/friend/decline", {}, JSON.stringify(value));

    alertify.success("Friendship from user " + username + " has been declined.", 3);

}

function removeSentRequest(username) {
    $(".btn-remove-request").attr('disabled', true);

    let value = {'username': username};
    stompClient.send("/app/friend/remove-req", {}, JSON.stringify(value));

    alertify.success("Request in user " + username + " has been removed.", 3);

}

function alreadyFriend(username) {
    alertify.success("Already friends.", 3);
}

function removeBlock(username) {
    $(".btn-remove-block").attr('disabled', true);

    let value = {'username': username};
    stompClient.send("/app/friend/remove-block", {}, JSON.stringify(value));

    alertify.success("User " + username + " has been removed from block list.", 3);
}

function blockUser(username) {
    $("#btn-block-hide").attr('disabled', true);
    $(".btn-block").attr('disabled', true);

    let value = {'username': username};
    stompClient.send("/app/friend/block", {}, JSON.stringify(value));

    alertify.success('User ' + username + ' was blocked!', 3);
}

function findUsers(e) {
    if (e) {
        e.preventDefault();
    }

    let value = {
        'username': $('#username').val(),
    };

    stompClient.send("/app/client/check", {}, JSON.stringify(value));

}

function checkedFindUser(message) {

    let success = message.accepted;

    if (success === true) {
        usernameStart = {
            'username': message.usernameStart
        };
    } else {
        alertify.error('Search value must have at least three characters!', 3);
    }
}

function disconnectChat() {
    stompClient.send("/app/client/chat-discon", {}, {});
    $("#chat-window").attr('hidden', true);
    let chatSelector = $("#chat");
    chatSelector.html("");

}

function chatDisconAlert(message) {
    $("#chat-window").attr('hidden', true);
    let chatSelector = $("#chat");
    chatSelector.html("");
    alertify.warning(message.username + ' ended your chat.', 2);

}

function alreadyChatting(username) {
    alertify.warning('You are already chatting.', 2);
}

function inviteToChat(username) {
    let value = {'username': username};

    stompClient.send("/app/client/invite-chat", {}, JSON.stringify(value));
}

function chatFriend(message) {
    if (message.chatting) {
        alertify.warning('User ' + message.username + ' is already chatting.', 2);
    } else {
        $("#chat-window").attr('hidden', false);
    }

}

function sendMessage() {
    let message = $("#message");

    if (message.val() === "") {
        return;
    }

    let value = {
        'message': message.val(),
        'user' : null
    };

    stompClient.send("/app/client/send", {}, JSON.stringify(value));

    getChatMessage({'message' : message.val(), 'user' : 'You'});

    message.val("");
}

function getChatMessage(message) {
    let chatSelector = $("#chat");
    let chat = chatSelector.html();

    chatSelector.html("<tr>" +
        "<td>" + message.user + ": " + message.message + "</td>" +
        "</tr>");

    chatSelector.append(chat);
}

function endChat() {
    $("#chat-window").attr('hidden', true);

}


$(function () {
    window.onbeforeunload = function(){
        disconnect();
    };
    connect($("#connect").attr("data-csrf"));
    disconnectChat();

    $(".ws-form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() {
        connect($(this).attr("data-csrf"));
    });
    $( "#disconnect" ).click(function() { disconnect(); });
});

