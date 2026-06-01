// 心跳相关变量
let pingInterval = null;
let pongTimeout = null;

// 30秒一次心跳
const PING_INTERVAL = 10_000;
// 5秒内未收到pong响应视为断连
const PONG_TIMEOUT = 5_000;

function startHeartbeat(websocket) {
    pingInterval = setInterval(() => {
        if (websocket.readyState === WebSocket.OPEN) {
            console.log('客户端发送心跳: ping...')
            websocket.send('ping');
            pongTimeout = setTimeout(() => {
                websocket.close(1000, 'Pong timeout');
                console.log('Pong timeout Close')
            }, PONG_TIMEOUT);
        }
    }, PING_INTERVAL)
}

function clearPongTimeout() {
    clearTimeout(pongTimeout);
}

function clearPingInterval() {
    clearInterval(pingInterval);
}

function stopHeartbeat() {
    clearPingInterval()
    clearPongTimeout()
}