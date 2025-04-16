import React, { useEffect, useState } from 'react';
import api from '../services/api';

const JobDashboard = () => {
  const [scheduledJobs, setScheduledJobs] = useState([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  // Mock loading of scheduled jobs
  useEffect(() => {
    const fetchScheduledJobs = async () => {
      try {
        const response = await api.getAllScheduledJobs();
        setScheduledJobs(response);
      } catch (err) {
        console.error("Failed to fetch scheduled jobs:", err);
      }
    };
  
    fetchScheduledJobs();
  }, []);
  

  const executeNow = async (userId) => {
    setLoading(true);
    setMessage('');
    try {
      const result = await api.getUserById(userId);
      setMessage(`✅ Executed job for user ${userId}`);
    } catch (err) {
      setMessage(`❌ Failed to execute job for user ${userId}`);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (userId) => {
    try {
      await api.deleteScheduledJob(userId);
      setScheduledJobs(prev => prev.filter(job => job.userId !== userId));
    } catch (err) {
      alert(err.message || "Error deleting job.");
    }
  };
  

  return (
    <div className="card">
      <div className="card-header"><h2>📋 Job Dashboard</h2></div>
      <div className="card-body">
        {scheduledJobs.length === 0 ? (
          <p>No scheduled jobs found.</p>
        ) : (
          <table className="table">
            <thead>
              <tr>
                <th>User ID</th>
                <th>Frequency</th>
                <th>Start Time</th>
                <th>Timezone</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {scheduledJobs.map(job => (
                <tr key={job.userId}>
                  <td>{job.userId}</td>
                  <td>{job.recurrence?.frequency ?? "ONE TIME"}</td>
                  <td>{job.recurrence.startTime}</td>
                  <td>{job.recurrence.timezone}</td>
                  <td>
                    <button className="btn btn-sm btn-success" onClick={() => executeNow(job.userId)} disabled={loading}>
                      Execute Now
                    </button>
                    {/* <button className="btn btn-sm btn-danger" onClick={async () => {
                        try {
                        await api.deleteScheduledJob(job.userId);
                        setScheduledJobs(prev => prev.filter(j => j.userId !== job.userId));
                        } catch (err) {
                        alert(err.message);
                        }
                    }}
                    >
                    Delete
                    </button> */}

                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
        {message && <div className="alert mt-3 alert-info">{message}</div>}
      </div>
    </div>
  );
};

export default JobDashboard;
