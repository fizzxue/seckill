var seckill = {
    URL : {
        now : "/seckill/time/now",
        exposer : function (seckillId) {
            return "/seckill/"+seckillId+"/exposer";
        },
        execution : function (seckillId, md5) {
            return "/seckill/"+seckillId+"/"+md5+"/execution";
        }
    },
    validatePhone : function (phone) {
        if (phone && phone.length == 11 && !isNaN(phone)) {
            return true;
        }
        return false;
    },
    handleSeckill : function (seckillId, node) {
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        $.post(seckill.URL.exposer(seckillId), {}, function (result) {
            if (result && result.success) {
                var exposer = result.data;
                if (exposer.exposed) {
                    var md5 = exposer.md5;
                    var killUrl = seckill.URL.execution(seckillId, md5);
                    console.log(killUrl);
                    $("#killBtn").one('click', function () {
                        $(this).addClass('disabled');
                        $.post(seckill.URL.execution(seckillId, md5), {}, function (result) {
                            if (result ) {
                                var killResult = result.data;
                                var state = killResult.state;
                                var stateInfo = killResult.stateInfo;
                                console.log(stateInfo);
                                node.html('<span class="label label-success">' + stateInfo +'</span>');
                            }
                        });
                    });
                    node.show();
                } else {//处理不同机器和服务器之间计时偏差
                    var now = exposer.now;
                    var start = exposer.start;
                    var end = exposer.end;
                    seckill.countdown(seckillId, now, start, end);
                }
            }
        });
    },
    countdown : function (seckillId, nowtime, startTime, endTime) {
        var seckillBox = $('#seckill-box');
        if (nowtime > endTime) {
            seckillBox.html('秒杀结束!');
        } else if (nowtime < startTime) {
            var killTime = new Date(startTime + 1000);
            seckillBox.countdown(killTime, function (event) {
                console.log(event);//todo
                var format = event.strftime('秒杀倒计时: %D天 %H时 %M分 %S秒');
                seckillBox.html(format);
            }).on('finishi.countdown', function () {
                seckill.handleSeckill(seckillId, seckillBox);
            });
        } else {
            seckill.handleSeckill(seckillId, seckillBox);
        }
    },
    detail : {
        init: function (params) {
            var killPhone = $.cookie("killPhone");
            var seckillId = params.seckillId;
            var startTime = params.startTime;
            var endTime = params.endTime;
            if (!seckill.validatePhone(killPhone)) {
                var killPhoneModal = $("#killPhoneModel");
                killPhoneModal.modal({
                    show: true,
                    backdrop: "static",
                    keyboard: false
                });
                $("#killPhoneBtn").click(function () {
                    var inputPhone = $("#killPhonekey").val();
                    if (seckill.validatePhone(inputPhone)) {
                        $.cookie("killPhone", inputPhone, {expires: 7, path: '/seckill'});
                        window.location.reload();
                    } else {
                        $("#killPhoneMessage").hide().html("<label class='label label-danger'>手机号错误!</label>").show(300);
                    }
                });
            }
            $.get(seckill.URL.now, {}, function (result) {
                if (result && result.success) {
                    var nowTime = result.data;
                    seckill.countdown(seckillId, nowTime, startTime, endTime);
                }
            });

        }
    }
}
