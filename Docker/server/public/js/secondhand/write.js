
// //입력칸 console창에 뜨게끔
// const writeFrm = document.querySelector("#writeFrm");

// const submitHandler = (e) => {
//     e.preventDefault();
//     const subject = e.target.subject.value;
//     const writer = e.target.writer.value;
//     const location = e.target.location.value;

//     console.log(subject);
//     console.log(writer);
//     console.log(location);
// };

// writeFrm.addEventListener("submit", submitHandler);

// //사진 첨부하고 화면에 이미지 들어가게끔 하기
// document.addEventListener('DOMContentLoaded', () => {
//     const imageUpload1 = document.getElementById('image-upload1');
//     const imageUpload2 = document.getElementById('image-upload2');
//     const fileInput1 = document.getElementById('file-input1');
//     const fileInput2 = document.getElementById('file-input2');
//     const preview1 = document.getElementById('preview1');
//     const preview2 = document.getElementById('preview2');

//     imageUpload1.addEventListener('click', () => fileInput1.click());
//     imageUpload2.addEventListener('click', () => fileInput2.click());

//     fileInput1.addEventListener('change', () => {
//         const file = fileInput1.files[0];
//         if (file) {
//             const reader = new FileReader();
//             reader.onload = (e) => {
//                 preview1.src = e.target.result;
//                 preview1.style.display = 'block';
//             };
//             reader.readAsDataURL(file);
//         }
//     });

//     fileInput2.addEventListener('change', () => {
//         const file = fileInput2.files[0];
//         if (file) {
//             const reader = new FileReader();
//             reader.onload = (e) => {
//                 preview2.src = e.target.result;
//                 preview2.style.display = 'block';
//             };
//             reader.readAsDataURL(file);
//         }
//     });
// });

document.addEventListener('DOMContentLoaded', () => {
    const writeFrm = document.querySelector("#writeFrm");

    const submitHandler = (e) => {
        e.preventDefault();
        const subject = e.target.subject.value;
        const writer = e.target.writer.value;
        const location = e.target.location.value;
        const image1 = document.getElementById('preview1').src;//사진 저장
        const image2 = document.getElementById('preview2').src;

        // 작성한 게시글을 로컬 스토리지에 저장
        const newPost = { title: subject, content: writer, location: location, image1: image1, image2: image2 };
        let posts = JSON.parse(localStorage.getItem('posts')) || [];
        posts.unshift(newPost); // 새 게시글을 맨 앞에 추가
        localStorage.setItem('posts', JSON.stringify(posts));

        // 게시글 리스트 페이지로 이동
        window.location.href = '/secondhand/list.html';
    };

    writeFrm.addEventListener("submit", submitHandler);

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
