import React, { Component } from 'react';
import axios from 'axios';
import { Table, Container } from "react-bootstrap"
import ForumRow from './ForumRow'

import 'bootstrap/dist/css/bootstrap.css'
import './App.css'

class ForumsPanel extends Component {
    constructor(props) {
        super(props);
        this.state = { 'forums' : []};
    }

    componentDidMount() {
        axios
            .get('/api/forums')
            .then(res => {
                if (res.status === 200) {
                    let forums = [...res.data];
                    this.setState({'forums' : forums});
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
            <Table bordered size="sm">
                <thead>
                <tr>
                    <th> Forum Id </th>
                    <th> Name </th>
                    <th> Caching </th>
                </tr>
                </thead>
                <tbody>
                    {this.state.forums.map((forum, i) => <ForumRow key={i} forum={forum}/>)}
                </tbody>
            </Table>
            </Container>
        );
    }
}

export default ForumsPanel;