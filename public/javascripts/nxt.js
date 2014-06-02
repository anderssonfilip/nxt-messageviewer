function renderTree(json) {
    $('#chat_tree').jstree({
        "core": {
            'data': json
        }
    });
}