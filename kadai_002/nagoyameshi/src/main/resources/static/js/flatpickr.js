let maxDate = new Date();
maxDate = maxDate.setMonth(maxDate.getMonth() + 3);

flatpickr('#birthday', {
// mode: "range",
 locale:	 'ja',
 enableTime: true,
 minDate:	 'today',
 maxDate:	 maxDate
});