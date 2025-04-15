
import React, { useState, useEffect } from 'react';
import DatePicker from 'react-datepicker';
import "react-datepicker/dist/react-datepicker.css";
import api from '../services/api';
import moment from 'moment-timezone';

function ScheduleUserFetch() {
  const [userId, setUserId] = useState('');
  const [scheduledTime, setScheduledTime] = useState(new Date());
  const [timezone, setTimezone] = useState(moment.tz.guess()); // Default to user's browser timezone
  const [timezones, setTimezones] = useState([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState(null);
  const [error, setError] = useState(null);

  // Load timezones on component mount
  useEffect(() => {
    // Get common timezone names
    const commonTimezones = [
      'America/New_York',
      'America/Chicago',
      'America/Denver',
      'America/Los_Angeles',
      'Europe/London',
      'Europe/Paris',
      'Europe/Berlin',
      'Asia/Tokyo',
      'Asia/Shanghai',
      'Asia/Kolkata',
      'Australia/Sydney',
      'Pacific/Auckland'
    ];
    
    // Add user's local timezone if not in the list
    const userTimezone = moment.tz.guess();
    if (!commonTimezones.includes(userTimezone)) {
      commonTimezones.push(userTimezone);
    }
    
    // Sort timezones by offset
    const sortedTimezones = commonTimezones.sort((a, b) => {
      return moment.tz.zone(a).utcOffset(Date.now()) - moment.tz.zone(b).utcOffset(Date.now());
    });
    
    setTimezones(sortedTimezones);
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage(null);
    setError(null);
    try {
      // Get the hours and minutes from the selected date
      const hours = scheduledTime.getHours();
      const minutes = scheduledTime.getMinutes();
      const day = scheduledTime.getDate();
      const month = scheduledTime.getMonth();
      const year = scheduledTime.getFullYear();
      
      // Create a moment in your local timezone with these values
      const localMoment = moment().set({
        year, month, date: day, hour: hours, minute: minutes, second: 0, millisecond: 0
      });
      
      // Format without converting (just format the date as a string)
      const timeString = localMoment.format("YYYY-MM-DDTHH:mm:ss");
      
      console.log("Sending to backend:", {
        userId,
        scheduledTime: timeString,
        timezone
      });
      
      const response = await api.scheduleUserFetch(userId, timeString, timezone);
      setMessage(response);
    } catch (err) {
      console.error('Error details:', err.response?.data || err.message);
      setError('Error scheduling user fetch');
    } finally {
      setLoading(false);
    }
  };


  return (
    <div className="card mb-4">
      <div className="card-header">
        <h2>Schedule User Fetch</h2>
      </div>
      <div className="card-body">
        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label htmlFor="fetchUserId" className="form-label">User ID</label>
            <input
              type="number"
              className="form-control"
              id="fetchUserId"
              value={userId}
              onChange={(e) => setUserId(e.target.value)}
              required
            />
          </div>
          
          <div className="mb-3">
            <label htmlFor="scheduledTime" className="form-label">Scheduled Time</label>
            <DatePicker
              id="scheduledTime"
              className="form-control"
              selected={scheduledTime}
              onChange={(date) => setScheduledTime(date)}
              showTimeSelect
              timeFormat="HH:mm"
              timeIntervals={5}
              dateFormat="MMMM d, yyyy h:mm aa"
              required
            />
          </div>
          
          <div className="mb-3">
            <label htmlFor="timezone" className="form-label">Timezone</label>
            <select
              id="timezone"
              className="form-control"
              value={timezone}
              onChange={(e) => setTimezone(e.target.value)}
            >
              {timezones.map((tz) => (
                <option key={tz} value={tz}>
                  {tz} ({moment.tz(tz).format('Z')})
                </option>
              ))}
            </select>
            <small className="form-text text-muted">
              Current time in selected timezone: {moment().tz(timezone).format('YYYY-MM-DD HH:mm:ss')}
            </small>
          </div>
          
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Scheduling...' : 'Schedule Fetch'}
          </button>
        </form>

        {error && (
          <div className="alert alert-danger mt-3">
            {error}
          </div>
        )}

        {message && (
          <div className="alert alert-success mt-3">
            {message}
          </div>
        )}
      </div>
    </div>
  );
}

export default ScheduleUserFetch;