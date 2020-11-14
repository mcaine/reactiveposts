import React, { Component } from 'react';
import axios from "axios";
import {Table} from "react-bootstrap";
import PostRow from "./PostRow";

class ShowThread extends Component {

    constructor() {
        super();
        this.state = {
            posts: [],
            loaded: false
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
                    this.setState({'posts' : posts, loaded: true});
                } else {
                    console.log("Got status " + res.status);
                }
            })
            .catch(function(error) {
                console.log(error)
            });
    }

    render() {
        const threadId = this.props.match.params.threadId;
        const pageId = parseInt(this.props.match.params.pageId);
        let prevPage = pageId -1 ;
        let nextPage = pageId + 1;
        let backLink = `/thread/${threadId}/page/${prevPage}`;
        let forwardLink = `/thread/${threadId}/page/${nextPage}`;
        let back = prevPage > 0 ? <a href={backLink}>◄{prevPage}</a> : <span/>;
        let forward = this.state.loaded && nextPage <= this.state.posts[0].thread.pagesGot ? <a href={forwardLink}>{nextPage}►</a> : <span/>;
        let pageTitle = this.state.loaded ? this.state.posts[0].thread.name + ' page ' + this.props.match.params.pageId : "";
        let forumId = this.state.loaded ?  this.state.posts[0].thread.forum.id: -1;
        let forumName = this.state.loaded ?  this.state.posts[0].thread.forum.name: -1;
        let forumLink = `/forum/${forumId}`;

        let controls =
            <Table bordered>
            <tr>
                <td>
                    <div><a href ="/">FORUMS</a></div>
                    <div>
                    <span>{back}</span>   <span>{forward}</span>
                    </div>
                </td>
                <div><a href={forumLink}>{forumName}</a></div>
                <span><h3>{pageTitle}</h3></span>
            </tr>
            </Table>

        return (
            <div>
                {controls}

                 <Table bordered>
                    <tbody>
                    {this.state.posts.map((post, i) => <PostRow key={post.id} post={post}/>)}
                    </tbody>
                </Table>
                {controls}
            </div>
        );
    }
}

export default ShowThread;