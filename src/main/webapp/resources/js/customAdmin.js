$(document).ready(function(){
	
	updateCountdown();
	$('#textarea2').live('input', updateCountdown);
	$("#allParliamentGroupCheckbox").click(function () {
		if ($("#allParliamentGroupCheckbox").is(':checked')) {
			$(".groupParliamentCheckbox").prop('checked', true);
		}else{
			$(".groupParliamentCheckbox").prop('checked', false);
		}
	});
	
	$('#allElectionGroupCheckbox').click(function () {
		if ($('#allElectionGroupCheckbox').is(':checked')) {
			$(".groupElectionCheckbox").prop('checked', true);
		}else{
			$(".groupElectionCheckbox").prop('checked', false);
		}
	});

});

function updateCountdown() {
    // 160 is the max message length
    var remaining = 160 - $('#textarea2').val().length;
    $('.countdown').text(remaining + ' characters remaining.');
}