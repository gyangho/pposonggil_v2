document.addEventListener('DOMContentLoaded', () => {
    const postDetail = document.getElementById('postDetail');

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
    } else {
        const errorMessage = document.createElement('p');
        errorMessage.textContent = '게시글을 불러오는 데 오류가 발생했습니다.';
        postDetail.appendChild(errorMessage);
    }
});
