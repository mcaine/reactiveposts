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

                    <td><div><h3>{post.author.name}</h3></div><div><img src={post.author.titleURL} alt="..." className="img-thumbnail"/></div></td>
                    <td></td>
                    <td><span dangerouslySetInnerHTML={{__html: post.html}} /></td>
                    <td></td>
                    <td></td>
                </tr>
            </>
        )
    }


}

export default PostRow;