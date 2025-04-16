import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import UserDetails from './components/UserDetails';
import ScheduleUserFetch from './components/ScheduleUserFetch';
import RecurringJobScheduler from './components/RecurringJobScheduler';
import JobDashboard from './components/JobDashboard';

function App() {
  return (
    <Router>
      <div className="container mt-4">
        <h1 className="mb-4">User Management System</h1>
        
        <nav className="navbar navbar-expand-lg navbar-light bg-light mb-4">
          <div className="container-fluid">
            <div className="collapse navbar-collapse">
              <ul className="navbar-nav">
                <li className="nav-item">
                  <Link className="nav-link" to="/">User Details</Link>
                </li>
                <li className="nav-item">
                  <Link className="nav-link" to="/schedule-fetch">Schedule User Fetch</Link>
                </li>
                <li className="nav-item">
                  <Link className="nav-link" to="/recurring-jobs">Recurring Jobs</Link>
                </li>
              </ul>
            </div>
          </div>
        </nav>
        <div className="container mt-4">
          <JobDashboard />
        </div>

        <Routes>
          <Route path="/" element={<UserDetails />} />
          <Route path="/schedule-fetch" element={<ScheduleUserFetch />} />
          <Route path="/recurring-jobs" element={<RecurringJobScheduler />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;