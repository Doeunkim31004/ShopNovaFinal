// 네이버 로그인 URL로 리다이렉션
function naverLogin() {
	const clientId = 'lB3HLLJuvztYmJr_soDB'; 
	const redirectUri = encodeURIComponent('http://localhost:8081/users/naverLoginCallback');
	const state = Math.random().toString(36).substring(7);
	// 템플릿 리터럴(backtick) 사용
	const url = `https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=${clientId}&redirect_uri=${redirectUri}&state=${state}`;        
	window.location.href = url;  // 네이버 로그인 페이지로 리디렉트
}