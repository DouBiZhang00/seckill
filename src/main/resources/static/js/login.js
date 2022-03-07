
var salt = "1a2b3c4d"

function tologin(){
    var username = $("#username").val();
    var password = $("#password").val();
    password = hex_md5(password + username);    //盐是username
    console.log("加盐加密后的密码===>" + password);
    // var bool = false;
    $.post({
        url: "/tologin",
        async: false,//同步方式
        data: {
            "username": username,
            "password": password
        },
        success:function (result){  // result状态码
            console.log(result);
            if (result.code === 500210) {
                $("#error").text(result.message);
                return false;
            } else {
                location.href = "/goods/toList";
                return false;   // location.href后面要跟return false
            }
        }
    });
    return false;
}

function toregister(){
    var username = $("#username").val();
    var password = $("#password").val();
    password = hex_md5(password + username);    //盐是username
    console.log("加盐加密后的密码===>" + password);
    $.post({
        url: "/toregister",
        async: false,//同步方式
        data: {
            "username": username,
            "password": password
        },
        success:function (result){  // result状态码
            if (result.code === 500211) {
                $("#error").text(result.message);
                return false;
            } else if (result.code === 500) {
                $("#error").text(result.message);
                return false;
            } else {
                location.href = "/login";
                return false;
            }
        }
    });
    return false;
}
