function openUserEvent(e) {
    if (e.status == 'success') {
        $('#userDetailModal').modal({'show': true});
    }
}

function closeUserEvent(e) {
    if (e.status == 'success') {
        $('#userDetailModal').modal('hide');
    }
}

