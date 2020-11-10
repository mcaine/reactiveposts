import React, { Component } from 'react';
import axios from "axios";
import {Container, Table} from "react-bootstrap";
//import ThreadRow from "./ThreadRow";
import PostRow from "./PostRow";

class ShowThread extends Component {

    constructor() {
        super();
        this.state = {
            posts: []
        };

    }

    componentDidMount() {
        const threadId = this.props.match.params.threadId;
        const pageId = this.props.match.params.pageId;

        axios
            .get(`/api/thread/${threadId}/page/${pageId}`)
            .then(res => {
                if (res.status === 200) {
                    let posts = [...res.data];
                    this.setState({'posts' : posts});
                } else {
                    console.log("Got status " + res.status);
                }
            })
            .catch(function(error) {
                console.log(error)
            });
    }

    render() {
        return (

                <Table bordered>
                    <thead>
                    <tr>
                        <th>Author</th>
                        <th>Content</th>
                        <th>Posted</th>
                    </tr>
                    </thead>
                    <tbody>
                    {this.state.posts.map((post, i) => <PostRow key={post.id} post={post}/>)}
                    </tbody>
                </Table>

        );
    }
}

export default ShowThread;