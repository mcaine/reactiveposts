import React, { Component } from 'react';
import ForumSubscribeButton from "./ForumSubscribeButton";

import "./App.css"

class ForumRow extends Component {
    constructor(props) {
        super(props);
        this.state = {
            forum : props.forum
        };
        this.updateSubscriptionStatus = this.updateSubscriptionStatus.bind(this);
    }

    updateSubscriptionStatus(newSubscriptionState) {
        this.setState({forum: {...this.state.forum, subscribed: newSubscriptionState}});
    }

    render() {
        return (
            <>
                <tr className={this.state.forum.topLevelForum ? "toplevelforum" : "subforum"}>
                    <td><a href={`/forum/${this.state.forum.id}`}>{this.state.forum.id}</a></td>
                    <td>{this.state.forum.name}</td>
                    <td>{this.state.forum.subscribed ? "YES" : ""}</td>
                    <td><ForumSubscribeButton updateSubscriptionStatus={this.updateSubscriptionStatus} forum={this.state.forum}/></td>
                </tr>
                {this.state.forum.subForums.map((subForum, i) => <ForumRow key={subForum.id} forum={subForum}/>)}
            </>
        )
    }
}

export default ForumRow;
