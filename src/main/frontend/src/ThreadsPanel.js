import React, { Component } from 'react';
import axios from 'axios';
import { Table, Container } from "react-bootstrap";
//import ForumRow from './ForumRow';
import ThreadRow from "./ThreadRow";

import 'bootstrap/dist/css/bootstrap.css'
import './App.css'

class ThreadsPanel extends Component {

    constructor(props) {
        super(props);
        this.state = {
            //forumId: props.forumId,
            //forumId: 269,
            threads: []
        };
    }

    componentDidMount() {
        const forumId = this.props.match.params.forumId;

        axios
            .get(`/api/forum/${forumId}/threads`)
            .then(res => {
                if (res.status === 200) {
                    let threads = [...res.data];
                    this.setState({'threads' : threads});
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
            <Container>
                <Table bordered>
                    <thead>
                    <tr>
                        <th>Thread Id</th>
                        <th>Name</th>
                        <th>Pages</th>
                        <th>Got</th>
                        <th>Caching</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    {this.state.threads.map((thread, i) => <ThreadRow key={thread.id} thread={thread}/>)}
                    </tbody>
                </Table>
            </Container>
        );
    }



}

export default ThreadsPanel;