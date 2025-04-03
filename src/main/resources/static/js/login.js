// 페이지 로드 시 로그인 상태 확인
window.onload = function() {
	const currentUser = sessionStorage.getItem("currentUser");  // 세션에 저장된 사용자 정보 확인
	if (currentUser) {
    // 사용자가 로그인 되어 있으면, 로그아웃 버튼 보이기
    	document.getElementById("logout-btn").style.display = "inline-block";
	} else {
    	// 사용자가 로그인되어 있지 않으면, 로그아웃 버튼 숨기기
        document.getElementById("logout-btn").style.display = "none";
	}
};

// 로그아웃 처리
function logout() {
	sessionStorage.removeItem("currentUser");  // 세션에서 사용자 정보 삭제
	// 페이지 새로고침하여 로그아웃 처리
	window.location.href = '/users/login';  // 로그인 페이지로 리디렉션
}