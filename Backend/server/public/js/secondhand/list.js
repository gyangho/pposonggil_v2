// document.addEventListener('DOMContentLoaded', () => {
//     const postList = document.getElementById('postList');

//     // 백엔드 API 호출하여 게시글 목록 가져오기
//     fetch('/api/posts')
//         .then(response => response.json())
//         .then(posts => {
//             // 게시글 목록을 렌더링
//             posts.forEach(post => {
//                 const postItem = document.createElement('div');
//                 postItem.className = 'post-item';

//                 const postTitle = document.createElement('h2');
//                 postTitle.textContent = post.title;

//                 const postContent = document.createElement('p');
//                 postContent.textContent = post.content;

//                 const postLocation = document.createElement('p');
//                 postLocation.className = 'location';
//                 postLocation.textContent = `위치: ${post.location}`;

//                 postItem.appendChild(postTitle);
//                 postItem.appendChild(postContent);
//                 postItem.appendChild(postLocation);

//                 postList.appendChild(postItem);
//             });
//         })
//         .catch(error => {
//             console.error('Error fetching posts:', error);
//             const errorMessage = document.createElement('p');
//             errorMessage.textContent = '게시글을 불러오는 데 오류가 발생했습니다.';
//             postList.appendChild(errorMessage);
//         });
// });

document.addEventListener('DOMContentLoaded', () => {
    const postList = document.getElementById('postList');

    // 로컬 스토리지에서 게시글 목록 가져오기
    const posts = JSON.parse(localStorage.getItem('posts')) || [];

    // 게시글 목록을 렌더링
    posts.forEach((post, index) => {
        const postItem = document.createElement('div');
        postItem.className = 'post-item';
        postItem.dataset.index = index;  // 각 게시글에 인덱스를 데이터 속성으로 저장

        const textContent = document.createElement('div');
        textContent.className = 'text-content';

        const postTitle = document.createElement('h2');
        postTitle.textContent = post.title;

        const postContent = document.createElement('p');
        postContent.textContent = post.content;

        const postLocation = document.createElement('p');
        postLocation.className = 'location';
        postLocation.textContent = `위치: ${post.location}`;

        textContent.appendChild(postTitle);
        textContent.appendChild(postContent);
        textContent.appendChild(postLocation);

        const postImages = document.createElement('div');
        if (post.image1) {
            const postImage1 = document.createElement('img');
            postImage1.src = post.image1;
            postImages.appendChild(postImage1);
        }
        if (post.image2) {
            const postImage2 = document.createElement('img');
            postImage2.src = post.image2;
            postImages.appendChild(postImage2);
        }

        postItem.appendChild(textContent);
        postItem.appendChild(postImages);
        postList.appendChild(postItem);

        postItem.addEventListener('click', () => {
            // 클릭한 게시글의 데이터를 로컬 스토리지에 저장
            localStorage.setItem('selectedPost', JSON.stringify(post));
            // 상세 페이지로 이동
            window.location.href = '/secondhand/show.html';
        });
    });
});