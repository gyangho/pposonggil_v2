import { fetchUserInfo } from './utils.js';

document.addEventListener('DOMContentLoaded', async () => {
    const writeFrm = document.querySelector("#writeFrm");

    // 사용자 정보 가져오기
    const userInfo = await fetchUserInfo();
    console.log('Logged in user info:', userInfo);  // 사용자 정보 콘솔에 출력

    const submitHandler = (e) => {
        e.preventDefault();
        const subject = e.target.subject.value;
        const writer = e.target.writer.value;
        const location = e.target.location.value;

        const images = [];
        for (let i = 1; i <= 10; i++) {
            const preview = document.getElementById(`preview${i}`);
            if (preview && preview.src) {
                images.push(preview.src);
            }
        }
        // const memberId = document.getElementById('memberId'); // 작성자 ID를 로컬스토리지에서 가져오기


        // 작성한 게시글을 로컬 스토리지에 저장
        const newPost = { title: subject, content: writer, location: location, images: images };//나중에 사용자 ID 받아오면 추가하기
        let posts = JSON.parse(localStorage.getItem('posts')) || [];
        posts.unshift(newPost); // 새 게시글을 맨 앞에 추가
        localStorage.setItem('posts', JSON.stringify(posts));

        // 게시글 리스트 페이지로 이동
        window.location.href = '/secondhand/list.html';
    };

    writeFrm.addEventListener("submit", submitHandler);

    const imageContainer = document.getElementById('image-container');

    const addImageUpload = (index) => {
        const imageUpload = document.createElement('div');
        imageUpload.classList.add('image-upload');
        imageUpload.id = `image-upload${index}`;

        const icon = document.createElement('i');
        icon.classList.add('fa-solid', 'fa-camera');

        const fileInput = document.createElement('input');
        fileInput.type = 'file';
        fileInput.id = `file-input${index}`;
        fileInput.accept = 'image/*';
        fileInput.style.display = 'none';

        const preview = document.createElement('img');
        preview.id = `preview${index}`;
        preview.style.display = 'none';
        preview.style.width = '100%';
        preview.style.height = '100%';

        imageUpload.appendChild(icon);
        imageUpload.appendChild(fileInput);
        imageUpload.appendChild(preview);

        imageContainer.appendChild(imageUpload);

        imageUpload.addEventListener('click', () => fileInput.click());

        fileInput.addEventListener('change', () => {
            const file = fileInput.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = (e) => {
                    preview.src = e.target.result;
                    preview.style.display = 'block';
                    if (index < 10 && !document.getElementById(`image-upload${index + 1}`)) {
                        addImageUpload(index + 1);
                    }
                    updateImageContainerLayout();
                };
                reader.readAsDataURL(file);
            }
        });
    };
    const updateImageContainerLayout = () => {
        const imageUploads = document.querySelectorAll('.image-upload');
        const container = document.querySelector('.image-container');
        if (imageUploads.length === 1) {
            container.style.justifyContent = 'center';
        } else {
            container.style.justifyContent = 'flex-start';
        }
    };

    addImageUpload(1);
    updateImageContainerLayout();
});