$(document).ready(function(){
	
	updateCountdown();
	$('#textarea2').live('input', updateCountdown);

});

function updateCountdown() {
    // 140 is the max message length
    var remaining = 160 - $('#textarea2').val().length;
    $('.countdown').text(remaining + ' characters remaining.');
}