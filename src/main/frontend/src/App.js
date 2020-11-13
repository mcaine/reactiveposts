import './App.css';
import ThreadsPanel from "./ThreadsPanel";
import ForumsPanel from "./ForumsPanel";
import ShowThread from "./ShowThread";
import {BrowserRouter as Router, Route, Switch} from 'react-router-dom'

function App() {
    return (
        <Router>
            <div>
                <Switch>

                    <Route path="/forum/:forumId" component={ThreadsPanel} />

                    <Route path="/thread/:threadId/page/:pageId" component={ShowThread} />

                    <Route path="/" component={ForumsPanel} />

                </Switch>
            </div>
        </Router>
    );
}

export default App;
