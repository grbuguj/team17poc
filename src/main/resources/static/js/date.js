/* / date.js
function startDateScanner() {
    // 날짜 인식 기능 시작 (POC에서는 단순 버튼 클릭만 처리)
    document.getElementById('date-result').innerHTML = '<p>인식된 날짜: 2025-04-12</p>';
}
*/

const form = document.getElementById('date-form');
const result = document.getElementById('date-result');

form.addEventListener('submit', async function (e) {
    e.preventDefault(); // 페이지 새로고침 막기

    const formData = new FormData(form);

    try {
        const response = await fetch('/ocr/upload', {
            method: 'POST',
            body: formData
        });

        const text = await response.text();
        result.innerHTML = `<p>${text}</p>`;
    } catch (err) {
        result.innerHTML = `<p style="color:red">에러 발생: ${err.message}</p>`;
    }
});

