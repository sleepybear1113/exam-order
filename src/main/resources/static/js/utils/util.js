function addPrefixZero(num, n) {
    return String("0".repeat(n) + num).slice(-n);
}