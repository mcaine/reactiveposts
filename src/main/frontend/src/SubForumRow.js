import React, { Component } from 'react';
import './App.css'

class SubForumRow extends Component {
    render() {
        return (
            <tr className="subforum">
                <td>{this.props.forum.id}</td>
                <td>{this.props.forum.name}</td>
               <td>{this.props.forum.subscribed ? "YES" : ""}</td>
            </tr>
        )
    }
}

export default SubForumRow;