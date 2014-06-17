'use strict';

var tree = {};
var introChatIcon = "/assets/images/chat_blue.png";
var replyChatIcon = "/assets/images/chat_red.png";
var multiChatIcon = "/assets/images/chat_pair.png";

function renderTree(json) {
    $('#chat_tree').jstree({
        "core": {
            'data': json
        }
    });
}

function setMessageCount() {
    $.getJSON('/messageCount',
        function(count) {
            $('#messageCount').text('Number of messages: ' + count);
        });
}

var c = 0;

$(document).ready(function() {

    $.getJSON('/jsontree',
        function(data) {

            tree = data;

            renderTree(data);
            setMessageCount();

        });
});

var testMsg = {
    'sender': 'NXT-BPV3-837M-QZTQ-9DQ69',
    'recipient': 'NXT-UWKJ-GFEV-AGY4-5C4YS',
    'text': 'aha',
    'height': 158119
};

function addMessageToTree(msg) {

    // find the outer node address and return child node and other address in array
    function findOuterNode(from, to) {

        function isMatch(addr) {
            for (var j = 0; j < tree[i].children.length; j++) {
                if (tree[i].children[j].text === addr) {
                    return true;
                }
            }
            return false;
        }

        for (var i = 0; i < tree.length; i++) {
            if (tree[i].text === from) {
                if (isMatch(to, from)) {
                    return [to, i];
                } else if (tree[i].text !== from) {
                    return [undefined, i];
                }
            } else if (tree[i].text === to) {
                if (isMatch(from, to)) {
                    return [from, i];
                } else if (tree[i].text !== to) {
                    return [undefined, i];
                }
            }
        }
    }

    // find the inner node which matches the second address and return index
    function findInnerNode(addrIdxPair) {

        for (var i = 0; i < tree[addrIdxPair[1]].children.length; i++) {
            if (addrIdxPair[0] === tree[addrIdxPair[1]].children[i].text) {
                return i;
            }
        }
    }

    var addrIdxPair = findOuterNode(msg.sender, msg.recipient);
    if (typeof addrIdxPair === 'undefined') { // add new top node

        tree.splice(0, 0, {
            'icon': introChatIcon,
            'text': msg.sender,
            'children': [{
                'icon': replyChatIcon,
                'text': msg.recipient,
                'children': [{
                    'icon': introChatIcon,
                    'text': msg.text
                }]
            }]
        });
    } else {

        var j = findInnerNode(addrIdxPair);
        var i = addrIdxPair[1];

        tree[i].icon = multiChatIcon;

        if (typeof j === 'undefined') { // add new inner top node

            tree[i].children.splice(0, 0, {
                'icon': replyChatIcon,
                'text': msg.recipient,
                'children': [{
                    'icon': introChatIcon,
                    'text': msg.text
                }]
            });


        } else {

            tree[addrIdxPair[1]].children[j].children.splice(0, 0, {
                'icon': introChatIcon,
                'text': msg.text
            });

            tree[addrIdxPair[1]].children[j].icon = multiChatIcon;
        }
        //console.log(i);
        //console.log(j);
    }

    $('#chat_tree').jstree("destroy");
    renderTree(tree);
    setMessageCount();

    //$('#chat_tree').jstree.focused()._get_settings().json_data.data = tree;
    //$('#chat_tree').jstree.focused().refresh(-1);
}