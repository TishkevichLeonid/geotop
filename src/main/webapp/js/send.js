function sendMailToGetFeedback(){
    var email = document.getElementById("email").value;
    var subject = document.getElementById("subject").value;
    var content = document.getElementById("content").value;

    $.ajax({
        url: '/sendEmail',
        type: 'POST',
        data: 'email=' + email + '&subject=' + subject + '&content=' + content,
        success: function (data) {
            alert(data);
        }
    });
}