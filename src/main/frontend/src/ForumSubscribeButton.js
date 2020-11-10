import React, { Component } from 'react';
import axios from 'axios';

class ForumSubscribeButton extends Component {
    constructor(props) {
        super(props);
        this.state = {
            forum: props.forum
        };

        this.handleClick = this.handleClick.bind(this);
    }

    handleClick() {
        let forumId = this.state.forum.id;
        let newSubscriptionStatus = !this.state.forum.subscribed;
        console.log(`Setting new subscription status to ${newSubscriptionStatus} for forum ${forumId}`)
        axios
            .post(`/api/forum/${forumId}/subscribe`, `subscribe=${newSubscriptionStatus}`)
            .then(res => {
                if (res.status === 200) {
                    let result = res.data.subscribed;
                    this.setState({forum : {...this.state.forum, subscribed: result}}); // why do I need to do this?
                    this.props.subscribe(result);
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
                {this.state.forum.subscribed ? 'UNSUBSCRIBE' : 'SUBSCRIBE'}
            </button>
        );
    }
}

export default ForumSubscribeButton;

