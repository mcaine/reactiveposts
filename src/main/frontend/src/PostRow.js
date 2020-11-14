import React, { Component } from 'react';

class PostRow extends Component {
    render() {
        const post = this.props.post;
        const postAnchorId = `post${post.id}`;
        const link = `https://forums.somethingawful.com/showthread.php?threadid=${post.thread.id}&userid=0&perpage=40&pagenumber=${post.pageNum}#post${post.id}`;
        return (
            <>
                <tr id={postAnchorId}>
                    <td>
                        <div><h4>{post.author.name}</h4></div>
                        <div><img src={post.author.titleURL} alt={"avatar for " + post.author.name} className="img-thumbnail"/></div>
                        <div><h4>{post.author.titleText}</h4></div>
                        <div>{post.postDate}</div>
                        <div><a href={link}>link</a></div>
                    </td>
                    <td><span className="post-text" dangerouslySetInnerHTML={{__html: post.html}} /></td>
                </tr>
            </>
        )
    }
}

export default PostRow;