document.addEventListener('DOMContentLoaded', () => {
    const postDetail = document.getElementById('postDetail');
    const buttonsContainer = document.getElementById('buttons-container');

    // 로컬 스토리지에서 선택한 게시글 데이터 가져오기
    const post = JSON.parse(localStorage.getItem('selectedPost'));

    if (post) {
        const postTitle = document.createElement('h2');
        postTitle.textContent = post.title;

        const postContent = document.createElement('p');
        postContent.textContent = post.content;

        const postLocation = document.createElement('p');
        postLocation.className = 'location';
        postLocation.textContent = `위치: ${post.location}`;

        postDetail.appendChild(postTitle);
        postDetail.appendChild(postContent);
        postDetail.appendChild(postLocation);

        if (post.image1) {
            const postImage1 = document.createElement('img');
            postImage1.src = post.image1;
            postDetail.appendChild(postImage1);
        }
        if (post.image2) {
            const postImage2 = document.createElement('img');
            postImage2.src = post.image2;
            postDetail.appendChild(postImage2);
        }

        //여기는 나중에 수정(작성자인지 확인)
        // 작성자와 현재 사용자를 비교하여 버튼 렌더링
        // const currentUser = JSON.parse(localStorage.getItem('currentUser'));
        // if (currentUser && currentUser.userId === post.userId) {
        // 작성자와 현재 사용자가 동일하면 수정 및 삭제 버튼 표시

        const editButton = document.createElement('button');
        editButton.textContent = '게시글 수정';
        editButton.addEventListener('click', () => {
            // 게시글 수정 페이지로 이동
            window.location.href = '/secondhand/edit.html';
        });

        const deleteButton = document.createElement('button');
        deleteButton.textContent = '게시글 삭제';
        deleteButton.addEventListener('click', () => {
            // 게시글 삭제 처리
            const posts = JSON.parse(localStorage.getItem('posts')) || [];
            const updatedPosts = posts.filter(p => p.title !== post.title);
            localStorage.setItem('posts', JSON.stringify(updatedPosts));
            // 리스트 페이지로 이동
            window.location.href = '/secondhand/list.html';
        });

        buttonsContainer.appendChild(editButton);
        buttonsContainer.appendChild(deleteButton);
        // } else {
        // 작성자가 아니면 채팅하기 버튼 표시
        const chatButton = document.createElement('button');
        chatButton.textContent = '채팅하기';
        chatButton.addEventListener('click', () => {
            // 채팅 페이지로 이동
            window.location.href = '/secondhand/chat.html';
        });

        buttonsContainer.appendChild(chatButton);
        // }


    } else {
        const errorMessage = document.createElement('p');
        errorMessage.textContent = '게시글을 불러오는 데 오류가 발생했습니다.';
        postDetail.appendChild(errorMessage);
    }
});
