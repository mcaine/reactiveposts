import React, { Component } from 'react';
import {ResponsiveEmbed, Image} from 'react-bootstrap';
//import ThreadSubscribeButton from "./ThreadSubscribeButton";

class PostRow extends Component {
    constructor(props) {
        super(props);
        this.state= {
            post: props.post
        };
    }

    render() {
        let post = this.state.post;
        return (
            <>
                <tr>
                    <td>
                        <div><h4>{post.author.name}</h4></div>
                        <div><img src={post.author.titleURL} alt="..." className="img-thumbnail"/></div>
                        <div><h4>{post.author.titleText}</h4></div>
                        <div><h4>{post.postDate}</h4></div>
                    </td>
                    <td><span dangerouslySetInnerHTML={{__html: post.html}} /></td>
                    <td></td>
                    <td></td>
                </tr>
            </>
        )
    }


}

export default PostRow;