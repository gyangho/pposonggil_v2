document.addEventListener('DOMContentLoaded', () => {
    const editFrm = document.querySelector("#editFrm");

    // 로컬 스토리지에서 선택한 게시글 데이터 가져오기
    const post = JSON.parse(localStorage.getItem('selectedPost'));

    if (post) {
        document.getElementById('title-box').value = post.title;
        document.getElementById('content-box').value = post.content;
        document.getElementById('location-box').value = post.location;
        if (post.image1) {
            document.getElementById('preview1').src = post.image1;
            document.getElementById('preview1').style.display = 'block';
        }
        if (post.image2) {
            document.getElementById('preview2').src = post.image2;
            document.getElementById('preview2').style.display = 'block';
        }
    }

    const submitHandler = (e) => {
        e.preventDefault();
        const subject = e.target.subject.value;
        const writer = e.target.writer.value;
        const location = e.target.location.value;
        const image1 = document.getElementById('preview1').src;
        const image2 = document.getElementById('preview2').src;

        // 게시글 수정하여 로컬 스토리지에 저장
        const updatedPost = { title: subject, content: writer, location: location, image1: image1, image2: image2 };
        let posts = JSON.parse(localStorage.getItem('posts')) || [];
        posts = posts.map(p => p.title === post.title ? updatedPost : p);
        localStorage.setItem('posts', JSON.stringify(posts));

        // 게시글 리스트 페이지로 이동
        window.location.href = '/secondhand/list.html';
    };

    editFrm.addEventListener("submit", submitHandler);

    const imageUpload1 = document.getElementById('image-upload1');
    const imageUpload2 = document.getElementById('image-upload2');
    const fileInput1 = document.getElementById('file-input1');
    const fileInput2 = document.getElementById('file-input2');
    const preview1 = document.getElementById('preview1');
    const preview2 = document.getElementById('preview2');

    imageUpload1.addEventListener('click', () => fileInput1.click());
    imageUpload2.addEventListener('click', () => fileInput2.click());

    fileInput1.addEventListener('change', () => {
        const file = fileInput1.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                preview1.src = e.target.result;
                preview1.style.display = 'block';
            };
            reader.readAsDataURL(file);
        }
    });

    fileInput2.addEventListener('change', () => {
        const file = fileInput2.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                preview2.src = e.target.result;
                preview2.style.display = 'block';
            };
            reader.readAsDataURL(file);
        }
    });
});
