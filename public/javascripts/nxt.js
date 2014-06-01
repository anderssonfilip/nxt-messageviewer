function renderTree(json) {
    $('#nxt_tree').jstree({
        "core": {
            'data': json
        }
    });
}