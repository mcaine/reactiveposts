import React, { Component } from 'react';
import axios from 'axios';

class ForumSubscribeButton extends Component {
    constructor(props) {
        super(props);
        this.state = {
            subscribed: this.props.subscribed,
            forumId: this.props.forumId
        };

        this.handleClick = this.handleClick.bind(this);
    }

    handleClick() {
        let forumId = this.props.forumId;
        let newSubscriptionStatus = !this.state.subscribed;
        console.log(`Setting new subscription status to ${newSubscriptionStatus} for forum ${forumId}`)
        axios
            .post(`/api/forum/${forumId}/subscribe`, `subscribe=${newSubscriptionStatus}`)
            .then(res => {
                if (res.status === 200) {
                    let result = res.data.subscribed;
                    let id = res.data.id;
                    console.log("Got id: " + id);
                    console.log("Got result: " + result);
                    this.setState({subscribed : result});
                    this.props.subscribe(result);
                } else {
                    console.log("Got status " + res.status);
                }
            })
            .catch(function(error) {
                console.log(error)
            });

        // this.setState(state => ({
        //     subscribed: !state.subscribed
        // }));
    }

    render() {
        return (
            <button onClick={this.handleClick}>
                {this.state.subscribed ? 'UNSUBSCRIBE' : 'SUBSCRIBE'}
            </button>
        );
    }
}

export default ForumSubscribeButton;

