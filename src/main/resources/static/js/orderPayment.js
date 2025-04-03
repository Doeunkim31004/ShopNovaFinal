// 결제 요청 함수
function startPayment(productNames, totalAmount, recipientName, phoneNumber, address) {
    var IMP = window.IMP;
    IMP.init("imp81656118"); // 본인의 가맹점 코드로 교체
	
	var merchantUid = `payment-${crypto.randomUUID()}`
	console.log(merchantUid)
    // 결제 요청
    IMP.request_pay({
        channelKey: "channel-key-112cb04a-bdbd-4d13-963d-486f3e9b2645", // KG이니시스 채널 키
        pay_method: "card", // 결제 방법
        merchant_uid: merchantUid, // 고유 결제 ID
        name: productNames.join(", "), // 상품명 (여러 상품일 경우 합쳐서 전달)
        amount: totalAmount, // 총 결제 금액
        buyer_name: recipientName, // 구매자 이름
        buyer_tel: phoneNumber, // 구매자 전화번호
        buyer_addr: address, // 구매자 주소
    }, function(response) {
        if (response.success) {
            alert("결제 성공");
            console.log(response); // 성공 응답 처리

            // 결제 성공 후 페이지 이동
            document.querySelector('form').submit();
        } else {
            alert("결제 실패: " + response.error_msg);
            console.log(response); // 실패 응답 처리

            // 결제 실패 시 페이지 이동하지 않음
            return false;
        }
    });
}

// 결제하기 버튼 클릭 시 실행되는 함수
$("#checkout-button").on("click", function(event) {
    event.preventDefault(); // 폼이 바로 제출되지 않도록 방지
	
    var recipientName = $("input[name='recipientName']").val();
    var address = $("input[name='address']").val();
    var phoneNumber = $("input[name='phoneNumber']").val();
    var totalAmount = $("input[name='totalAmount']").val();

    // 상품 정보를 저장할 리스트들
    let productIds = [];
    let productNames = [];
    let productQuantities = [];
    let productPrices = [];

    // 선택된 상품들 계산 (상품의 수량과 가격을 합산)
    $(".order-item").each(function() {
        const productId = $(this).find("input[name='productIds[]']").val();  // 상품 ID 추출
        const productName = $(this).find(".order-item-details p:first-child").text().trim();  // 상품명
        const priceText = $(this).find(".order-item-details p span").first().text().trim();  // 가격
        const quantityText = $(this).find("span[id^='product-'][id$='-quantity']").text().trim();  // 수량

        // 수량을 정수로 변환
        const quantity = parseInt(quantityText, 10);

        // 상품 가격과 수량이 숫자인지 확인하고 리스트에 저장
        const price = parseInt(priceText.replace(/[^0-9]/g, ""), 10);
        if (!isNaN(price) && !isNaN(quantity)) {
            productIds.push(productId);
            productNames.push(productName);
            productQuantities.push(quantity);
            productPrices.push(price);
        }
    });

    // 결제 금액 확인
    if (totalAmount <= 0) {
        alert("결제할 상품이 없습니다.");
        return false;
    }

    // 필수 입력값 체크
    if (recipientName == "") {
        alert('받는 사람 이름을 입력해주세요.');
        return false;
    }
    if (address == "") {
        alert('주소를 입력해주세요.');
        return false;
    }
    if (phoneNumber == "") {
        alert('휴대폰 번호를 입력해주세요.');
        return false;
    }

    // 결제 진행
    alert("총 결제 금액은 " + totalAmount.toLocaleString() + " 원 입니다.");

    // 결제 진행 함수 호출
    startPayment(productNames, totalAmount, recipientName, phoneNumber, address);
});
