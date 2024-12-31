// 컨텐트 페이지 전용 JavaScript
document.addEventListener('DOMContentLoaded', function () {
    const button = document.getElementById('test-button');
    const paragraph = document.getElementById('test-paragraph');

    // 버튼 클릭 이벤트
    button.addEventListener('click', function () {
        paragraph.classList.toggle('highlight'); // 하이라이트 클래스 추가/제거
        alert('버튼이 클릭되었습니다!');
    });
});