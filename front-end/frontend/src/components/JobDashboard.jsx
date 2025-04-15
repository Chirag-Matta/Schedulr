import React, { useState, useEffect } from 'react';
import api from '../services/api';
import moment from 'moment-timezone';

function JobDashboard() {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [message, setMessage] = useState(null);
  const [executingJobId, setExecutingJobId] = useState(null);
  
  // Fetch jobs when component mounts
  useEffect(() => {
    fetchJobs();
  }, []);
  
  const fetchJobs = async () => {
    setLoading(true);
    setError(null);
    try {
      console.log("Fetching all jobs...");
      const jobsData = await api.getAllJobs();
      console.log("Jobs fetched successfully:", jobsData);
      setJobs(jobsData);
    } catch (err) {
      console.error('Error fetching jobs:', err);
      setError('Failed to load jobs: ' + (err.message || err));
    } finally {
      setLoading(false);
    }
  };
  
  const executeJob = async (jobId) => {
    setExecutingJobId(jobId);
    setMessage(null);
    setError(null);
  
    try {
      console.log(`Executing job ${jobId}...`);
  
      // First, fetch user details by calling the new /api/user_details/{id} API
      const job = jobs.find((job) => job.id === jobId);  // Find the job to get the userId
      if (job) {
        const userResponse = await api.getUserById(job.userId);  // Fetch user by ID
        console.log("Fetched user:", userResponse);
      } else {
        throw new Error("Job not found");
      }
  
      // Now trigger the job execution
      const response = await api.executeJob(jobId);
      console.log(`Job ${jobId} execution response:`, response);
      setMessage(`Job ${jobId} triggered successfully`);
  
      // Refresh the job list after a short delay to see the updated status
      setTimeout(() => {
        fetchJobs();
      }, 1000);
    } catch (err) {
      console.error(`Error executing job ${jobId}:`, err);
      setError(`Failed to execute job ${jobId}: ${err.message || JSON.stringify(err)}`);
    } finally {
      setExecutingJobId(null);
    }
  };
  
  
  // Format date with timezone
  const formatDate = (dateStr, timezone) => {
    if (!dateStr) return 'N/A';
    return moment(dateStr).tz(timezone || 'UTC').format('YYYY-MM-DD HH:mm:ss z');
  };
  
  // Status badge component
  const StatusBadge = ({ status }) => {
    let badgeClass = '';
    
    switch(status) {
      case 'PENDING':
        badgeClass = 'bg-warning';
        break;
      case 'COMPLETED':
        badgeClass = 'bg-success';
        break;
      case 'FAILED':
        badgeClass = 'bg-danger';
        break;
      default:
        badgeClass = 'bg-secondary';
    }
    
    return <span className={`badge ${badgeClass}`}>{status}</span>;
  };
  
  return (
    <div className="card mb-4">
      <div className="card-header d-flex justify-content-between align-items-center">
        <h2>Job Dashboard</h2>
        <button 
          className="btn btn-outline-primary btn-sm" 
          onClick={fetchJobs}
          disabled={loading}
        >
          <i className="bi bi-arrow-clockwise me-1"></i>
          Refresh
        </button>
      </div>
      <div className="card-body">
        {error && (
          <div className="alert alert-danger">
            {error}
          </div>
        )}
        {message && (
          <div className="alert alert-success">
            {message}
          </div>
        )}
        
        {loading ? (
          <div className="d-flex justify-content-center">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Loading...</span>
            </div>
          </div>
        ) : (
          <>
            {jobs.length === 0 ? (
              <div className="alert alert-info">No jobs found</div>
            ) : (
              <div className="table-responsive">
                <table className="table table-striped table-hover">
                  <thead>
                    <tr>
                      <th>Job ID</th>
                      <th>Type</th>
                      <th>User ID</th>
                      <th>Scheduled Time</th>
                      <th>Last Execution</th>
                      <th>Status</th>
                      <th>Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    {jobs.map(job => (
                      <tr key={job.id}>
                        <td>{job.id}</td>
                        <td>{job.jobType}</td>
                        <td>{job.userId}</td>
                        <td>{formatDate(job.scheduledTime, job.timezone)}</td>
                        <td>{job.lastExecutionTime ? formatDate(job.lastExecutionTime, job.timezone) : 'Never'}</td>
                        <td><StatusBadge status={job.status} /></td>
                        <td>
                          {job.status === 'PENDING' && (
                            <button 
                              className="btn btn-sm btn-primary"
                              onClick={() => executeJob(job.id)}
                              disabled={loading || executingJobId === job.id}
                            >
                              {executingJobId === job.id ? (
                                <>
                                  <span className="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>
                                  Executing...
                                </>
                              ) : 'Execute Now'}
                            </button>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}

export default JobDashboard;