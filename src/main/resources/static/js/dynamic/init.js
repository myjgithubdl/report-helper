var ReportHelper = {
    env: 'dev', //dev|prod
    version: '1.0',
    ctxPath: '',
    config: {
        pageSize: 50
    },
    global: {},
    utils: {
        debug: function (msg) {
            if (ReportHelper.env == "prod") {
                return;
            }
            var time = moment().format("YYYY MM DD HH:mm:ss:SSS")
            console.trace('[%s]:%s', time, msg);
        }
    },
    validate: {},
    temp: {}
};
