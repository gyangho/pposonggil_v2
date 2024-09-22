document.addEventListener('DOMContentLoaded', () => {
    const chatContainer = document.getElementById('chatContainer');
    const chatInput = document.getElementById('chatInput');
    const sendButton = document.getElementById('sendButton');

    // 채팅 메시지 배열
    let messages = JSON.parse(localStorage.getItem('chatMessages')) || [];

    // 채팅 메시지 렌더링 함수
    const renderMessages = () => {
        chatContainer.innerHTML = '';
        messages.forEach(msg => {
            const messageDiv = document.createElement('div');
            messageDiv.className = `chat-message ${msg.type}`;
            messageDiv.textContent = msg.content;
            chatContainer.appendChild(messageDiv);
        });
    };

    // 메시지 보내기 버튼 클릭 이벤트
    sendButton.addEventListener('click', () => {
        const message = chatInput.value.trim();
        if (message) {
            // 새로운 메시지를 배열에 추가
            messages.push({ type: 'sent', content: message });
            localStorage.setItem('chatMessages', JSON.stringify(messages));
            renderMessages();
            chatInput.value = '';
            chatContainer.scrollTop = chatContainer.scrollHeight;
        }
    });

    // 초기 렌더링
    renderMessages();
});
