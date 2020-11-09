import React, { Component } from 'react';
import SubForumRow from "./SubForumRow";
import "./App.css"

class ForumRow extends Component {
    render() {
        return (
            <>
                <tr className="toplevelforum">
                    <td>{this.props.forum.id}</td>
                    <td>{this.props.forum.name}</td>
                    <td>{this.props.forum.subscribed ? "YES" : ""}</td>
                </tr>
                {this.props.forum.subForums.map((subForum, i) => <SubForumRow key={1000 + i} forum={subForum}/>)}
            </>
        )
    }
}

export default ForumRow;
