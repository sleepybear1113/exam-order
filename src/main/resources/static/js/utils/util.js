function addPrefixZero(num, n) {
    let pn = parseInt(n);
    let pNum = parseInt(num);
    if (pNum >= pn) {
        return "";
    }
    let value = "　".repeat(pn - pNum);
    return String(value);
}

let version = "1.0.0";