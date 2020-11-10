import './App.css';
import ThreadsPanel from "./ThreadsPanel";
import ForumsPanel from "./ForumsPanel";
import ShowThread from "./ShowThread";
import {BrowserRouter as Router, Route, Switch} from 'react-router-dom'
import {Container} from "react-bootstrap";

function App() {
    return (
        <Router>
            <div>
                <Switch>
                    <Route path="/forums" component={ForumsPanel} />
                    <Route path="/index/:forumId" component={ThreadsPanel} />

                    <Route path="/thread/:threadId/:pageId" component={ShowThread} />

                    <Route path="/" >
                        <Container>
                            <div><a href = "/forums">FORUMS INDEX</a></div>
                            <div><a href = "/index/219">YOSPOS</a></div>
                            <div><a href = "/index/269">C-SPAM</a></div>
                        </Container>
                    </Route>
                </Switch>
            </div>
        </Router>
    );
}

export default App;
