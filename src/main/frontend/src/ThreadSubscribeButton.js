import React, { Component } from 'react';
import axios from 'axios';

class ThreadSubscribeButton extends Component {
    constructor(props) {
        super(props);
        this.handleClick = this.handleClick.bind(this);
    }

    handleClick() {
        let threadId = this.props.thread.id;
        let newSubscriptionStatus = !this.props.thread.subscribed;
        console.log(`Setting new subscription status to ${newSubscriptionStatus} for thread ${threadId}`)
        axios
            .post(`/api/thread/${threadId}/subscribe`, `subscribe=${newSubscriptionStatus}`)
            .then(res => {
                if (res.status === 200) {
                    let result = res.data.subscribed;
                    this.props.updateSubscriptionStatus(result);
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
            <button onClick={this.handleClick}>
                {this.props.thread.subscribed ? 'UNSUBSCRIBE' : 'SUBSCRIBE'}
            </button>
        );
    }
}

export default ThreadSubscribeButton;