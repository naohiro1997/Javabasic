// AjaxでJSONを取得する
function executeAjax () {
	'use strict';
	var requestQuery = { q  : $('#q').val()};
	console.dir(requestQuery);
	$.ajax({
		type : 'GET',
		url : '/wt2/api/employees',
		dataType : 'json',
		data :requestQuery,
		success : function (json) {
			// DOM操作
			$('#image img').attr('src', json.image);
			$('#name').html(json.name + 'さん');
			$('#id td').html(json.id);
			$('#birthYmd td').html(json.birthYmd);
			$('#address td').html(json.address);
			$('#college td').html(json.college);
			$('#major td').html(json.major);
			$('#license td').html(json.license);
			$('#enteryymd td').html(json.enterYmd);
			$('#comment span').html(json.comment);
			$('#seibetsu td').html(json.seibetsu);
			$('#hobby_link').html(json.name + 'さんの趣味一覧');
			$('#hobby_link').attr('href', 'hobby.html?q=' + json.id);
					}
	});
}

$(document).ready(function () {
	'use strict';

	// 初期表示用
	executeAjax();

	// 更新ボタンにイベント設定
	$('#searchBtn').bind('click',executeAjax);

});