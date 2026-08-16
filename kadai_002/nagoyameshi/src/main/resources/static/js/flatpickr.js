let maxDate = new Date();
maxDate = maxDate.setMonth(maxDate.getMonth() + 3);

flatpickr("#reservedDatetime", {
    locale: "ja",

    // 時刻選択を有効にする
    enableTime: true,

    // 秒は選択しない
    enableSeconds: false,

    // 24時間表記
    time_24hr: true,

    // 30分刻み
    minuteIncrement: 30,

    // 今日以降
    minDate: "today",

    // Spring Bootに送る形式
    dateFormat: "Y-m-d H:i:S",

    disableMobile: true
});