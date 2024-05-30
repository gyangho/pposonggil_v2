import React, { useState, useEffect } from 'react';

function Posting({ onSave, editingPost, onUpdate }) {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');

  useEffect(() => {
    if (editingPost) {
      setTitle(editingPost.title);
      setContent(editingPost.content);
    }
  }, [editingPost]);

  const handleSubmit = (e) => {
    e.preventDefault();
    const post = { title, content };

    if (editingPost) {
      onUpdate({ ...editingPost, ...post });
    } else {
      onSave(post);
    }

    setTitle('');
    setContent('');
  };

  return (
    <form onSubmit={handleSubmit}>
      <div>
        <label>Title</label>
        <input
          type="text"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
        />
      </div>
      <div>
        <label>Content</label>
        <textarea
          value={content}
          onChange={(e) => setContent(e.target.value)}
          required
        />
      </div>
      <button type="submit">작성 완료</button>
    </form>
  );
}

export default Posting;
