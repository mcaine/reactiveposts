import React, { Component } from 'react';
import SubForumRow from "./SubForumRow";
import ForumSubscribeButton from "./ForumSubscribeButton";

import "./App.css"

class ForumRow extends Component {
    constructor(props) {
        super(props);
        this.state = {
            forum : props.forum
        };
        this.subscribe = this.subscribe.bind(this);
    }

    subscribe(newSubscriptionState) {
        this.setState({forum: {...this.state.forum, subscribed: newSubscriptionState}});
    }

    render() {
        return (
            <>
                <tr className={this.state.forum.topLevelForum ? "toplevelforum" : "subforum"}>
                    <td>{this.state.forum.id}</td>
                    <td>{this.state.forum.name}</td>
                    <td>{this.state.forum.subscribed ? "YES" : ""}</td>
                    <td><ForumSubscribeButton subscribe={this.subscribe} forumId={this.props.forum.id} subscribed={this.props.forum.subscribed}/></td>
                </tr>
                {this.props.forum.subForums.map((subForum, i) => <ForumRow key={this.props.key * 1000 + i} forum={subForum}/>)}
            </>
        )
    }
}

export default ForumRow;
