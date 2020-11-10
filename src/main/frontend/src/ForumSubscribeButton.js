import React, { Component } from 'react';

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
        this.setState(state => ({
            subscribed: !state.subscribed
        }));
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

