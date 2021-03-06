let stompClient = null;
let intervalIDFriends;
let intervalIDPosts;
let usernameStart = {'username':'user'};

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
}

function connect(csrf) {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({ "X-CSRF-TOKEN": csrf }, function (frame) {
        setConnected(true);
        //alertify.success('Successfully connected.', 2);
        console.log('Connected: ' + frame);

        stompClient.subscribe('/user/client/friends', function (message) {
            showFriendsList(JSON.parse(message.body));
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
        stompClient.subscribe('/user/client/get-posts', function (message) {
            showPosts(JSON.parse(message.body));
        });
        stompClient.subscribe('/user/client/length', function (message) {
            tooLong(JSON.parse(message.body));
        });
        stompClient.subscribe('/user/client/my-post', function (message) {
            myPost(JSON.parse(message.body));
        });
        stompClient.subscribe('/user/client/unliked', function (message) {
            postUnliked(JSON.parse(message.body));
        });
        stompClient.subscribe('/user/client/liked', function (message) {
            postLiked(JSON.parse(message.body));
        });
    });

    intervalIDFriends = setInterval(friendsRequest, 1000);
    intervalIDPosts = setInterval(postsRequest, 1000);
}

function disconnect() {
    disconnectChat();
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    clearInterval(intervalIDFriends);
    clearInterval(intervalIDPosts);
    console.log("Disconnected");
    //alertify.success('Successfully disconnected.', 2);
    $("#friendsListTable").html("");
    $("#postTable").html("");
}

function friendsRequest() {
    if (stompClient !== null) {
        stompClient.send("/app/client/friends", {}, {});
    }
}

function postsRequest() {
    if (stompClient !== null) {
        stompClient.send("/app/client/get-posts", {}, {});
    }
}

function showFriendsList(message) {
    let friendsListTable = $("#friendsListTable");

    friendsListTable.html("");

    for (let i = 0; i < message.length; i++) {

        let username = message[i].username;
        let status = message[i].status;

        if (status) {
            friendsListTable.append(
                "<tr>" +
                "<td class='status-tab'><span title=" + (status === 0 ? ('Offline') : (status === 1 ? ('Online') : ('Playing'))) + " class=\"indicator " + (status === 0 ? 'offline' : (status === 1 ? 'online' : 'in-game')) + " \"/></td>" +
                "<td class='user-tab'>" + username + "</td>" +
                "<td class='button-chat-tab'><button " + ($("#btn-chat-hide").prop('disabled') === true ? 'disabled' : 'enabled') + " class='btn btn-primary btn-chat' onclick=\"inviteToChat('" + message[i].username + "')\">Chat</button></td>" +
                "</tr>");
        } else {
            friendsListTable.append(
                "<tr>" +
                "<td class='status-tab'><span title=" + (status === 0 ? ('Offline') : (status === 1 ? ('Online') : ('Playing'))) + " class=\"indicator " + (status === 0 ? 'offline' : (status === 1 ? 'online' : 'in-game')) + " \"/></td>" +
                "<td class='user-tab'>" + username + "</td>" +
                "</tr>");
        }

    }
}

function showPosts(message) {
    let postTable = $("#postTable");

    postTable.html("");

    for (let i = 0; i < message.length; i++) {

        let uuid = message[i].uuid;
        let username = message[i].username;
        let time = message[i].time;
        let text = message[i].text;
        let announcement = message[i].announcement;
        let userslikes = message[i].userslikes;
        let tableString = "<tr>";
        if (announcement) {
            tableString += "<td class='ann-tab'>" + username + " -Announcement- </td>";
        } else {
            tableString += "<td>" + username + "</td>";
        }
        tableString += "<td class='li-tab'><div class='dropdown'><a id='likes' class='fa fa-thumbs-up' type='submit' onclick=\"likeIt('" + uuid + "')\"/>" +
                                       "<div class='dropdown-content'>";
        for (let j = 0; j < userslikes.length; j++) {
            tableString += "<a>" + userslikes[j] + "</a>";
        }

        tableString += "</div></div><label>" + userslikes.length + "</label></td>" +
                       "<td>" + time + "</td>" +
                       "</tr>" +
                       "<tr>" +
                       "<td>" + text + "</td>" +
                       "</tr>";

        postTable.append(tableString);

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
    let text = message.val();

    if (message.val() === "") {
        return;
    }

    text = text.replace(/</g, "&lt;").replace(/>/g, "&gt;");

    let value = {
        'message': text,
        'user' : null
    };

    stompClient.send("/app/client/send", {}, JSON.stringify(value));

    getChatMessage({'message' : text, 'user' : 'You'});

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

function pinPost() {
    let message = $('#post-message');
    let announcement = $('#announcement');
    let text = message.val();
    if (message.val() === "") {
        return;
    }
    alertify.success("1", 3);


    text = text.replace(/</g, "&lt;").replace(/>/g, "&gt;");

    let value = {
        'uuid' : null,
        'text': text,
        'username' : null,
        'time' : null,
        'announcement' : announcement.is(":checked"),
        'userslikes' : null
    };

   alertify.success("1", 3);


    stompClient.send("/app/client/post", {}, JSON.stringify(value));

    message.val("");

    alertify.success("1", 3);


}

function tooLong(message) {
    alertify.error('Your post is too long! (max 255)', 3);
}

function myPost(message) {
    alertify.error('You cant like your post.', 3);
}

function postUnliked(message) {
    alertify.success('Post unliked', 3);
}

function postLiked(message) {
    alertify.success('Post liked', 3);
}

function likeIt(uuid) {
    let value = {'uuidstr' : uuid};

    stompClient.send("/app/client/like", {}, JSON.stringify(value));

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
        connect($("#connect").attr("data-csrf"));
    });
    $( "#disconnect" ).click(function() { disconnect(); });

});

