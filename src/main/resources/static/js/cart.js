function removeItem(cartItemId, event) {
    event.preventDefault();

    if (confirm("이 항목을 삭제하시겠습니까?")) {
        fetch(`/cart-items/cart/remove/${cartItemId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (response.ok) {
                const cartItem = document.querySelector(`[data-cart-item-id="${cartItemId}"]`);
                if (cartItem) {
                    cartItem.remove();
                }
                updateTotal();
                alert("항목이 삭제되었습니다.");
            } else {
                return response.text().then(text => {
                    throw new Error(text || "삭제 실패");
                });
            }
        })
        .catch(error => {
            console.error("삭제 오류:", error);
            alert(error.message || "삭제 중 오류가 발생했습니다.");
        });
    }
}

function goToProductDetail(productId) {
    window.location.href = '/product/' + productId;  // 상품 상세 페이지로 이동
}

function goToCheckout() {
    const selectedItems = document.querySelectorAll('input[name="selectedItemIds"]:checked');
    
    if (selectedItems.length === 0) {
        alert("구매할 상품을 선택하세요.");
        return;
    }

    // 선택된 상품의 ID와 수량을 Long 타입으로 추출
    const selectedProductData = Array.from(selectedItems)
        .map(item => {
            const cartItem = item.closest('.cart-item');
            const productId = cartItem.dataset.productId;  // 상품 ID
            const quantityInput = cartItem.querySelector('.quantity-value'); // 수량 입력창
            const quantity = quantityInput ? quantityInput.value : 1;  // 수량 (기본값은 1)
            
            return { productId, quantity };  // 상품 ID와 수량을 객체로 반환
        })
        .filter(data => data.productId !== null && data.productId !== undefined);  // null이나 undefined 제거

    // 상품 ID와 수량이 모두 포함된 데이터를 POST로 전송
    const form = document.createElement("form");
    form.method = "POST";
    form.action = "/order/orderPayment";  // 실제 서버 URL로 설정

    // selectedProductData 배열을 POST 데이터로 추가
    selectedProductData.forEach(data => {
        const productIdInput = document.createElement("input");
        productIdInput.type = "hidden";
        productIdInput.name = "selectedItemIds";  // 서버에서 받을 파라미터 이름
        productIdInput.value = data.productId;  // 상품 ID
        form.appendChild(productIdInput);
        
        const quantityInput = document.createElement("input");
        quantityInput.type = "hidden";
        quantityInput.name = "quantities";  // 수량 파라미터 이름
        quantityInput.value = data.quantity;  // 수량
        form.appendChild(quantityInput);
    });

    // 폼을 페이지에 추가하고 전송
    document.body.appendChild(form);
    form.submit();
}



function updateTotal() {
    let totalAmount = 0;
    document.querySelectorAll('.cart-item').forEach((cartItem) => {
        const checkbox = cartItem.querySelector('input[type="checkbox"]');
        const priceText = cartItem.querySelector('.item-price').textContent.trim();
        const price = parseInt(priceText.replace(/[^0-9]/g, ""), 10);
        const quantityInput = cartItem.querySelector('.quantity input');  // 수량 입력

        if (checkbox.checked && !isNaN(price)) {
            const quantity = parseInt(quantityInput.value, 10);  // 수량 값
            totalAmount += price * quantity;  // 수량과 가격을 곱한 값을 더함
        }
    });

    // 금액을 1의 자릿수까지 처리
    const finalAmount = totalAmount;
    const finalAmountWithoutDecimal = finalAmount % 1 === 0 ? finalAmount.toFixed(0) : finalAmount.toFixed(2);

    document.getElementById('totalAmount').innerText = finalAmountWithoutDecimal.toLocaleString();
}

document.addEventListener("DOMContentLoaded", function() {
    updateTotal();

    const checkboxes = document.querySelectorAll('.cart-item input[type="checkbox"]');
    checkboxes.forEach((checkbox) => {
        checkbox.addEventListener('change', updateTotal);
    });
});

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