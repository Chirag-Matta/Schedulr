// import React, { useState } from 'react';
// import DatePicker from 'react-datepicker';
// import "react-datepicker/dist/react-datepicker.css";
// import api from '../services/api';

// function ScheduleUserFetch() {
//   const [userId, setUserId] = useState('');
//   const [scheduledTime, setScheduledTime] = useState(new Date());
//   const [loading, setLoading] = useState(false);
//   const [message, setMessage] = useState(null);
//   const [error, setError] = useState(null);

//   const handleSubmit = async (e) => {
//     e.preventDefault();
//     setLoading(true);
//     setMessage(null);
//     setError(null);
    
//     try {
//       // Format the date to ISO string for the backend
//       const formattedDate = scheduledTime.toISOString();
      
//       const response = await api.scheduleUserFetch(userId, formattedDate);
//       setMessage(response);
//     } catch (err) {
//       setError('Error scheduling user fetch');
//     } finally {
//       setLoading(false);
//     }
//   };

//   return (
//     <div className="card mb-4">
//       <div className="card-header">
//         <h2>Schedule User Fetch</h2>
//       </div>
//       <div className="card-body">
//         <form onSubmit={handleSubmit}>
//           <div className="mb-3">
//             <label htmlFor="fetchUserId" className="form-label">User ID</label>
//             <input
//               type="number"
//               className="form-control"
//               id="fetchUserId"
//               value={userId}
//               onChange={(e) => setUserId(e.target.value)}
//               required
//             />
//           </div>
          
//           <div className="mb-3">
//             <label htmlFor="scheduledTime" className="form-label">Scheduled Time</label>
//             <DatePicker
//               id="scheduledTime"
//               className="form-control"
//               selected={scheduledTime}
//               onChange={(date) => setScheduledTime(date)}
//               showTimeSelect
//               timeFormat="HH:mm"
//               timeIntervals={5}
//               dateFormat="MMMM d, yyyy h:mm aa"
//               required
//             />
//           </div>
          
//           <button type="submit" className="btn btn-primary" disabled={loading}>
//             {loading ? 'Scheduling...' : 'Schedule Fetch'}
//           </button>
//         </form>

//         {error && (
//           <div className="alert alert-danger mt-3">
//             {error}
//           </div>
//         )}

//         {message && (
//           <div className="alert alert-success mt-3">
//             {message}
//           </div>
//         )}
//       </div>
//     </div>
//   );
// }

// export default ScheduleUserFetch;


 

import React, { useState } from 'react';
import DatePicker from 'react-datepicker';
import "react-datepicker/dist/react-datepicker.css";
import moment from 'moment-timezone';
import api from '../services/api';

function ScheduleUserFetch() {
  const [userId, setUserId] = useState('');
  const [scheduledTime, setScheduledTime] = useState(new Date());
  const [selectedTimezone, setSelectedTimezone] = useState('America/New_York');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState(null);
  const [error, setError] = useState(null);

  // List of common timezones
  const timezones = [
    'America/New_York',
    'America/Chicago',
    'America/Denver',
    'America/Los_Angeles',
    'Europe/London',
    'Europe/Paris',
    'Asia/Tokyo',
    'Asia/Shanghai',
    'Asia/Kolkata',
    'Australia/Sydney',
    'Pacific/Auckland'
  ];

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage(null);
    setError(null);
    
    try {
      // Convert the selected time from the chosen timezone to IST
    //   const localTime = moment.tz(scheduledTime, selectedTimezone);
      const naive = moment(scheduledTime); // the raw Date picked by user, treated as browser local
const localTime = moment.tz(
  naive.format('YYYY-MM-DD HH:mm:ss'),
  'YYYY-MM-DD HH:mm:ss',
  selectedTimezone
);
      const istTime = localTime.clone().tz('Asia/Kolkata');
      
      // Format in ISO string for backend
    //   const formattedDate = istTime.format();
    const formattedDate = istTime.format('YYYY-MM-DDTHH:mm:ss');

      
      console.log(`Converting time from ${selectedTimezone} to IST:`);
      console.log(`- Original: ${localTime.format('YYYY-MM-DD HH:mm:ss')}`);
      console.log(`- IST: ${istTime.format('YYYY-MM-DD HH:mm:ss')}`);
      
      const response = await api.scheduleUserFetch(userId, formattedDate);
      setMessage(`${response} (Converted to IST: ${istTime.format('YYYY-MM-DD HH:mm:ss')})`);
    } catch (err) {
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
            <label htmlFor="timezone" className="form-label">Time Zone</label>
            <select
              id="timezone"
              className="form-control"
              value={selectedTimezone}
              onChange={(e) => setSelectedTimezone(e.target.value)}
            >
              {timezones.map((timezone) => (
                <option key={timezone} value={timezone}>
                  {timezone}
                </option>
              ))}
            </select>
          </div>
          
          <div className="mb-3">
            <label htmlFor="scheduledTime" className="form-label">
              Scheduled Time ({selectedTimezone})
            </label>
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

