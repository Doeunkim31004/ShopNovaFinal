// 로그아웃 처리 함수
function logout() {
	// 로그아웃 API 호출 또는 세션 종료 처리
	fetch('/logout', { method: 'POST' }) // 예시: 로그아웃 엔드포인트로 POST 요청
	.then(response => {
    	if (response.ok) {
        	alert("로그아웃 되었습니다.");
        	window.location.href = "/"; // 메인 페이지로 리디렉션
     	} else {
        	alert("로그아웃에 실패했습니다.");
		}
	})
    .catch(error => {
    	alert("로그아웃 중 오류가 발생했습니다.");
    });
}

function sortProducts() {
	const sortOption = document.getElementById('sortPrice').value;
    window.location.href = `/food2?sort=${sortOption}`;
}

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