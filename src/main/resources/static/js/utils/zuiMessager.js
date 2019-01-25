var zuiSuccessMessager = new $.zui.Messager({type: 'success'});
var zuiErrorMessager = new $.zui.Messager({type: 'danger'});

function showZuiSuccessMessager(msg) {
    zuiSuccessMessager.show(msg);
}

function showZuiErrorMessager(msg) {
    zuiErrorMessager.show(msg);
}