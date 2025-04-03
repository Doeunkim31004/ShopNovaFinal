// 검색창에서 엔터를 눌렀을 때 폼 전송
document.getElementById('search-input').addEventListener('keypress', function(event) {
	if (event.key === 'Enter') {
    	document.getElementById('search-form').submit();
 	}
});

// 검색 버튼 클릭 시 폼 제출
document.getElementById('headerSearchBtn').addEventListener('click', function() {
	document.getElementById('search-form').submit();
});