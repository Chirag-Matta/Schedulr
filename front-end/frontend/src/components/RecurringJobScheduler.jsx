import React, { useState, useEffect } from 'react';
import DatePicker from 'react-datepicker';
import "react-datepicker/dist/react-datepicker.css";
import api from '../services/api';
import moment from 'moment-timezone';

function RecurringJobScheduler() {
  const [userId, setUserId] = useState('');
  const [startTime, setStartTime] = useState(new Date());
  const [frequency, setFrequency] = useState('DAILY');
  const [daysOfWeek, setDaysOfWeek] = useState([]);
  const [daysOfMonth, setDaysOfMonth] = useState([]);
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

  const weekdays = [
    { value: 'MONDAY', label: 'Monday' },
    { value: 'TUESDAY', label: 'Tuesday' },
    { value: 'WEDNESDAY', label: 'Wednesday' },
    { value: 'THURSDAY', label: 'Thursday' },
    { value: 'FRIDAY', label: 'Friday' },
    { value: 'SATURDAY', label: 'Saturday' },
    { value: 'SUNDAY', label: 'Sunday' }
  ];

  const monthDays = Array.from({ length: 31 }, (_, i) => i + 1);

  const handleDayOfWeekChange = (day) => {
    if (daysOfWeek.includes(day)) {
      setDaysOfWeek(daysOfWeek.filter(d => d !== day));
    } else {
      setDaysOfWeek([...daysOfWeek, day]);
    }
  };

  const handleDayOfMonthChange = (day) => {
    if (daysOfMonth.includes(day)) {
      setDaysOfMonth(daysOfMonth.filter(d => d !== day));
    } else {
      setDaysOfMonth([...daysOfMonth, day]);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage(null);
    setError(null);
    
    try {
      // Convert the selected time to the selected timezone
      const timeInSelectedTimezone = moment.tz(startTime, timezone).format();
      
      const recurrence = {
        frequency,
        startTime: timeInSelectedTimezone,
        daysOfWeek,
        daysOfMonth,
        monthsOfYear: [], // You can add this if needed
        timezone // Include timezone in the request
      };
      
      const response = await api.scheduleRecurringJob(userId, recurrence);
      setMessage(response);
    } catch (err) {
      setError('Error scheduling recurring job');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="card mb-4">
      <div className="card-header">
        <h2>Schedule Recurring Job</h2>
      </div>
      <div className="card-body">
        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label htmlFor="recurringUserId" className="form-label">User ID</label>
            <input
              type="number"
              className="form-control"
              id="recurringUserId"
              value={userId}
              onChange={(e) => setUserId(e.target.value)}
              required
            />
          </div>
          
          <div className="mb-3">
            <label htmlFor="frequency" className="form-label">Frequency</label>
            <select 
              className="form-control" 
              id="frequency"
              value={frequency}
              onChange={(e) => setFrequency(e.target.value)}
            >
              <option value="HOURLY">Hourly</option>
              <option value="DAILY">Daily</option>
              <option value="WEEKLY">Weekly</option>
              <option value="MONTHLY">Monthly</option>
              <option value="YEARLY">Yearly</option>
            </select>
          </div>
          
          <div className="mb-3">
            <label htmlFor="startTime" className="form-label">Start Time</label>
            <DatePicker
              id="startTime"
              className="form-control"
              selected={startTime}
              onChange={(date) => setStartTime(date)}
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
          
          {frequency === 'WEEKLY' && (
            <div className="mb-3">
              <label className="form-label">Days of Week</label>
              <div className="d-flex flex-wrap">
                {weekdays.map(day => (
                  <div className="form-check me-3" key={day.value}>
                    <input
                      className="form-check-input"
                      type="checkbox"
                      id={`day-${day.value}`}
                      checked={daysOfWeek.includes(day.value)}
                      onChange={() => handleDayOfWeekChange(day.value)}
                    />
                    <label className="form-check-label" htmlFor={`day-${day.value}`}>
                      {day.label}
                    </label>
                  </div>
                ))}
              </div>
            </div>
          )}
          
          {frequency === 'MONTHLY' && (
            <div className="mb-3">
              <label className="form-label">Days of Month</label>
              <div className="d-flex flex-wrap">
                {monthDays.map(day => (
                  <div className="form-check me-2" key={day}>
                    <input
                      className="form-check-input"
                      type="checkbox"
                      id={`month-day-${day}`}
                      checked={daysOfMonth.includes(day)}
                      onChange={() => handleDayOfMonthChange(day)}
                    />
                    <label className="form-check-label" htmlFor={`month-day-${day}`}>
                      {day}
                    </label>
                  </div>
                ))}
              </div>
            </div>
          )}
          
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Scheduling...' : 'Schedule Recurring Job'}
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

export default RecurringJobScheduler;
