import React, { Component } from 'react';
import axios from 'axios';

class ForumSubscribeButton extends Component {
    constructor(props) {
        super(props);
        this.handleClick = this.handleClick.bind(this);
    }

    handleClick() {
        let forumId = this.props.forum.id;
        let newSubscriptionStatus = !this.props.forum.subscribed;
        console.log(`Setting new subscription status to ${newSubscriptionStatus} for forum ${forumId}`)
        axios
            .post(`/api/forum/${forumId}/subscribe`, `subscribe=${newSubscriptionStatus}`)
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
                {this.props.forum.subscribed ? 'UNSUBSCRIBE' : 'SUBSCRIBE'}
            </button>
        );
    }
}

export default ForumSubscribeButton;

