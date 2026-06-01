/**
 * 一般情况下，只需要改动self,other,websocket服务器地址即可使用
 */
let self = "";
let other = "";
const message = {attr: ""};

// const URL = "ws://127.0.0.1:7777/person/"
// const URL = "wss://127.0.0.1:7443/person/"
// const URL = "wss://10.0.0.241:7443/person/"
const URL = "wss://darcycui.com.cn:7443/person/"

let websocket = null

//初始化websocket
function webSocketInit(self1, other1) {
    // self赋值给全局变量self
    self = self1;
    other = other1;

    if (websocket) {
        websocket.close(1000, 'close before init');
        websocket = null;
    }
    websocket = new WebSocket(URL + self);

    //成功建立连接
    websocket.onopen = function () {
        linkSuccess(self, self, "链接成功");
        // 开启心跳
        startHeartbeat(websocket)
    };
    //接收到消息
    websocket.onmessage = function (event) {
        const chatMsg = document.getElementById("chatMsg");
        if (event.data === "pong") {
            console.log("客户端收到心跳: pong...")
            clearPongTimeout()
            chatMsg.value = chatMsg.value + self + " >>> " + event.data + "\n";
        } else {
            const data = JSON.parse(event.data);
            chatMsg.value = chatMsg.value + data.from + " >>> " + data.message + "\n";
        }
    };
    //连接发生错误
    websocket.onerror = function (err) {
        console.log(`WebSocket连接发生错误${JSON.stringify(err)}`);
        // 停止心跳
        stopHeartbeat()
    };
    //连接关闭
    websocket.onclose = function () {
        alert("WebSocket连接关闭");
        // 停止心跳
        stopHeartbeat()
    };

    // 添加详细错误监听
    websocket.addEventListener("error", (event) => {
        console.error("WebSocket Error:", event); // 查看完整错误对象
        console.error("WebSocket 错误详情:", {
            type: event.type,
            error: event.error,  // 错误对象捕获
            url: websocket.url,
            readyState: websocket.readyState
        });
        alert(`连接失败: ${event.error?.message || '未知错误'}`);
        // 停止心跳
        stopHeartbeat()
    });
    //监听窗口关闭事件，当窗口关闭时，主动关闭websocket连接
    window.onbeforeunload = function () {
        websocket.close()
    };
}

//对message.attr进行绑定
Object.defineProperty(message, "attr", {
    configurable: true,
    enumerable: true,
    set: function (newValue) {
        attr = newValue;
        const input = document.getElementById("inputMsg");
        input.value = newValue
    },
    get: function () {
        return attr;
    },
});

function inputChange(newValue) {
    message.attr = newValue
    if (event.keyCode === 13 &&
        message.attr !== undefined && message.attr !== null && message.attr.length > 0) {
        sendMsg();
    }
}

//发送消息
function sendMsg() {
    if (message.attr.length <= 0) {
        return
    }
    const msg = {
        from: self,
        to: other,
        createTime: new Date(),
        message: message.attr
    };
    chatMsg.value = chatMsg.value + msg.from + " >>> " + msg.message + "\n";
    websocket.send(JSON.stringify(msg))
    message.attr = ""
}

//链接成功
function linkSuccess(from, to, msg) {
    const successMsg = {
        from: from,
        to: to,
        createTime: new Date(),
        message: msg
    };
    websocket.send(JSON.stringify(successMsg))
}