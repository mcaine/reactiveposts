import React, { Component } from 'react';
import ThreadSubscribeButton from "./ThreadSubscribeButton";

import "./App.css"

class ThreadRow extends Component {
    constructor(props) {
        super(props);
        this.state = {
            thread : props.thread
        };
        this.subscribe = this.subscribe.bind(this);
    }

    subscribe(newSubscriptionState) {
        this.setState({thread: {...this.state.thread, subscribed: newSubscriptionState}});
    }

    render() {
        return (
            <>
                <tr>
                    <td>{this.state.thread.id}</td>
                    <td>{this.state.thread.name}</td>
                    <td>{this.state.thread.maxPageNumber}</td>
                    <td>{this.state.thread.pagesGot}</td>
                    <td>{this.state.thread.subscribed ? "YES" : ""}</td>
                    <td><ThreadSubscribeButton subscribe={this.subscribe} thread={this.state.thread}/></td>
                </tr>
            </>
        )
    }
}

export default ThreadRow;