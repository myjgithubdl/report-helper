$(function () {
    MembershipLogin.init();
});

var MembershipLogin = {
    init: function () {
        LoginMVC.View.initControl();
        LoginMVC.View.bindEvent();
        LoginMVC.View.bindValidate();
    }
};

var LoginMVC = {
    URLs: {
        login: {
            url: ReportHelper.ctxPath + '/member/authenticate',
            method: 'POST'
        },
        checkValidationCode: {
            url: ReportHelper.ctxPath + '/member/checkValidationCodeV2',
            method: 'POST'
        },
        success: {
            url: ReportHelper.ctxPath + '/home/index',
            method: 'POST'
        },
        refreshValidationCode: {
            url: ReportHelper.ctxPath + '/member/pcrimg_V2',
            method: 'POST'
        }
    },
    View: {
        initControl: function () {
            $("#account").focus();
            $("#loginPageValidationCode").val("");
            LoginMVC.Controller.refreshValidationCode();
        },
        bindEvent: function () {
            document.onkeydown = function (e) {
                var evt = e ? e : (window.event ? window.event : null)
                if (evt.keyCode == 13) {
                    LoginMVC.Controller.login();
                }
            };
            $('#loginPageLoginBtn').click(LoginMVC.Controller.login);
            $('#loginPageValidationCode').keyup(LoginMVC.Controller.checkValidationCode);
        },
        bindValidate: function () {
            $("#login-form").validate({
                rules: {
                    account: {
                        required: true,
                    },
                    password: {
                        required: true,
                        minlength: 3,
                        maxlength: 20
                    }
                },
                messages: {
                    account: {
                        required: ''
                    },
                    password: {
                        required: '',
                        minlength: 6
                    }
                },
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                }
            });
        }
    },
    Controller: {
        login: function () {
            console.log("login")
            var account=$.trim($("#account").val());
            var password=$.trim($("#password").val());
            var validationCode=$.trim($("#loginPageValidationCode").val());
            var rememberMe=$.trim($("#rememberMe").val());
            if(account == '' ){
                $("#loginPageLoginErrorMsg").text("账号不能为空");
                return;
            }
            if(password == '' ){
                $("#loginPageLoginErrorMsg").text("密码不能为空");
                return;
            }
            if(validationCode == '' ){
                $("#loginPageLoginErrorMsg").text("验证码不能为空");
                return;
            }
            if(validationCode.length !=4  ){
                $("#loginPageLoginErrorMsg").text("请输入合法验证码");
                return;
            }

            var validationCode = $.trim($("#loginPageValidationCode").val());

            var data = {
                "account": account,
                "password": password,
                "rememberMe": rememberMe,
                "validationCode": validationCode
            };

            if(LoginMVC.Controller.checkValidationCode(true)){

                $("#loginPageLoginBtn").text("登陆中...").attr("disabled","disabled");

                $.post(LoginMVC.URLs.login.url, data, function (result) {
                    if (result.respCode == '100') {
                        window.location = LoginMVC.URLs.success.url;
                    } else {
                        $("#loginPageLoginErrorMsg").text(result.respDesc);
                        LoginMVC.Controller.refreshValidationCode();
                        $("#loginPageLoginBtn").text("登陆").removeAttr("disabled");
                    }
                }, 'json');
            }

        },
        checkValidationCode: function (isRefreshValidationCode) {
            var isSuccess=false;
            var validationCode = $.trim($("#loginPageValidationCode").val());
            if (validationCode && validationCode.length == 4) {
                var result = LoginMVC.Controller.doCheckValidationCode();
                if (result.respCode == 100) {
                    $("#loginPageLoginErrorMsg").text("");
                    isSuccess= true;
                } else {
                    $("#loginPageLoginErrorMsg").text("验证码错误！");
                    console.info(isRefreshValidationCode == true)
                    if(isRefreshValidationCode == true){
                        LoginMVC.Controller.refreshValidationCode();
                    }
                }
            } else {
                $("#loginPageLoginErrorMsg").text("");
            }
            return isSuccess;
        },
        doCheckValidationCode: function () {
            var result = null;
            var validationCode = $("#loginPageValidationCode").val();
            $.ajax({
                type: "POST",
                url: LoginMVC.URLs.checkValidationCode.url,
                async: false,
                data: {"validationCode": validationCode},
                complete: function (response) {
                    var data = response.responseText;
                    if (data) {
                        result = JSON.parse(data)
                    }
                }
            });
            return result;
        },
        refreshValidationCode: function () {
            $("#loginPageValidationCode").val("");
            var patchcaDate = new Date().getTime();
            $("#loginPageValidationCodeImg").attr("src", LoginMVC.URLs.refreshValidationCode.url+"?t=" + patchcaDate);
        }
    }
};