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
                    let id = res.data.id;
                    console.log("Got id: " + id);
                    console.log("Got result: " + result);
                    this.setState({forum : {...this.state.forum, subscribed: result}});
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

