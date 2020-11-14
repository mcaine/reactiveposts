import React, { Component } from 'react';
import ThreadSubscribeButton from "./ThreadSubscribeButton";

import "./App.css"

class ThreadRow extends Component {
    constructor(props) {
        super(props);
        this.state = {
            thread : props.thread
        };
        this.updateSubscriptionStatus = this.updateSubscriptionStatus.bind(this);
    }

    updateSubscriptionStatus(newSubscriptionState) {
        this.setState({thread: {...this.state.thread, subscribed: newSubscriptionState}});
    }

    render() {
        const thread = this.state.thread;
        const threadId = thread.id;
        const pagesGot = thread.pagesGot;
        const lastPage = Math.max(1, parseInt(pagesGot));
        let link = `/thread/${threadId}/page/${lastPage}`;
        return (
            <>
                <tr>
                    <td><a href={link}>{threadId}</a></td>
                    <td>{thread.name}</td>
                    <td>{thread.maxPageNumber}</td>
                    <td>{thread.pagesGot}</td>
                    <td>{thread.subscribed ? "YES" : ""}</td>
                    <td><ThreadSubscribeButton updateSubscriptionStatus={this.updateSubscriptionStatus} thread={thread}/></td>
                </tr>
            </>
        )
    }
}

export default ThreadRow;