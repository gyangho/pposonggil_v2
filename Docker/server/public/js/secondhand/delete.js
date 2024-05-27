document.addEventListener('DOMContentLoaded', () => {
    const deleteButton = document.getElementById('deleteButton');
    deleteButton.addEventListener('click', () => {
        const post = JSON.parse(localStorage.getItem('selectedPost'));
        let posts = JSON.parse(localStorage.getItem('posts')) || [];
        posts = posts.filter(p => p.title !== post.title);
        localStorage.setItem('posts', JSON.stringify(posts));
        window.location.href = '/secondhand/list.html';
    });
});
