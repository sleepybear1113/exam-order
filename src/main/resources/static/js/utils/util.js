function addPrefixZero(num, n) {
    let pn = parseInt(n);
    let pNum = parseInt(num);
    if (pNum >= pn) {
        return "";
    }
    let value = "　".repeat(pn - pNum);
    return String(value);
}

// axios 全局拦截器
axios.interceptors.response.use(
    // 如果返回的状态码为 200，说明接口请求成功，可以正常拿到数据
    (response) => {
        return response;
    },
    // 否则的话抛出错误
    (error) => {
        let response = error.response;
        if (response == null) {
            alert(`请求失败，请检查程序是否启动`);
            return Promise.reject(error);
        }
        let status = response.status;
        let request = error.request;
        alert(`请求失败，status = ${status}\n url：${request.responseURL}`);
        return Promise.reject(error);
    }
);