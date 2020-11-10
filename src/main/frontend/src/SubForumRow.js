import React, { Component } from 'react';
import './App.css'
import ForumSubscribeButton from "./ForumSubscribeButton";

class SubForumRow extends Component {
    render() {
        return (
            <tr className="subforum">
                <td>{this.props.forum.id}</td>
                <td>{this.props.forum.name}</td>
                <td>{this.props.forum.subscribed ? "YES" : ""}</td>
                <td><ForumSubscribeButton subscribed={this.props.forum.subscribed}/></td>
            </tr>
        )
    }
}

export default SubForumRow;