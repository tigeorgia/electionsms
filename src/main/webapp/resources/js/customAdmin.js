$(document).ready(function(){
	
	updateCountdown();
	$('#textarea2').live('input', updateCountdown);
	
	$('#allGroupCheckbox').click(function () {
		if ($('#allGroupCheckbox').is(':checked')) {
			$(".groupCheckbox").prop('checked', true);
		}else{
			$(".groupCheckbox").prop('checked', false);
		}
	});

});

function updateCountdown() {
    // 140 is the max message length
    var remaining = 160 - $('#textarea2').val().length;
    $('.countdown').text(remaining + ' characters remaining.');
}