/* / barcode.js
function startBarcodeScanner() {
    // 바코드 스캔 기능 시작 (POC에서는 단순 버튼 클릭만 처리)
    document.getElementById('barcode-result').innerHTML = '<p>바코드 스캔 결과: 1234567890</p>';

    document.getElementById('barcode-form').onsubmit = function (event) {
        event.preventDefault();

        let formData = new FormData(document.getElementById('barcode-form'));

        fetch('/api/scanBarcode', {
            method: 'POST',
            body: formData,
        })
            .then(response => response.text())
            .then(data => {
                document.getElementById('barcode-result').innerHTML = '<p>' + data + '</p>';
            })
            .catch(error => {
                document.getElementById('barcode-result').innerHTML = '<p>오류 발생: ' + error + '</p>';
            });
    };
} */

const form = document.getElementById('barcode-form');
const result = document.getElementById('barcode-result');

form.addEventListener('submit', async function (e) {
    e.preventDefault(); // 페이지 새로고침 막기

    const formData = new FormData(form);

    try {
        const response = await fetch('/api/scanBarcode', {
            method: 'POST',
            body: formData
        });

        const text = await response.text();
        result.innerHTML = `<p>${text}</p>`;
    } catch (err) {
        result.innerHTML = `<p style="color:red">에러 발생: ${err.message}</p>`;
    }
});