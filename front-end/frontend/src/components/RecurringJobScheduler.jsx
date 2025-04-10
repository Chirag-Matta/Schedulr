//WORKING

// import React, { useState } from 'react';
// import DatePicker from 'react-datepicker';
// import "react-datepicker/dist/react-datepicker.css";
// import api from '../services/api';

// function RecurringJobScheduler() {
//   const [userId, setUserId] = useState('');
//   const [startTime, setStartTime] = useState(new Date());
//   const [frequency, setFrequency] = useState('DAILY');
//   const [daysOfWeek, setDaysOfWeek] = useState([]);
//   const [daysOfMonth, setDaysOfMonth] = useState([]);
//   const [loading, setLoading] = useState(false);
//   const [message, setMessage] = useState(null);
//   const [error, setError] = useState(null);

//   const weekdays = [
//     { value: 'MONDAY', label: 'Monday' },
//     { value: 'TUESDAY', label: 'Tuesday' },
//     { value: 'WEDNESDAY', label: 'Wednesday' },
//     { value: 'THURSDAY', label: 'Thursday' },
//     { value: 'FRIDAY', label: 'Friday' },
//     { value: 'SATURDAY', label: 'Saturday' },
//     { value: 'SUNDAY', label: 'Sunday' }
//   ];

//   const monthDays = Array.from({ length: 31 }, (_, i) => i + 1);

//   const handleDayOfWeekChange = (day) => {
//     if (daysOfWeek.includes(day)) {
//       setDaysOfWeek(daysOfWeek.filter(d => d !== day));
//     } else {
//       setDaysOfWeek([...daysOfWeek, day]);
//     }
//   };

//   const handleDayOfMonthChange = (day) => {
//     if (daysOfMonth.includes(day)) {
//       setDaysOfMonth(daysOfMonth.filter(d => d !== day));
//     } else {
//       setDaysOfMonth([...daysOfMonth, day]);
//     }
//   };

//   const handleSubmit = async (e) => {
//     e.preventDefault();
//     setLoading(true);
//     setMessage(null);
//     setError(null);
    
//     try {
//       const recurrence = {
//         frequency,
//         startTime: startTime.toISOString(),
//         daysOfWeek,
//         daysOfMonth,
//         monthsOfYear: [] // You can add this if needed
//       };
      
//       const response = await api.scheduleRecurringJob(userId, recurrence);
//       setMessage(response);
//     } catch (err) {
//       setError('Error scheduling recurring job');
//     } finally {
//       setLoading(false);
//     }
//   };

//   return (
//     <div className="card mb-4">
//       <div className="card-header">
//         <h2>Schedule Recurring Job</h2>
//       </div>
//       <div className="card-body">
//         <form onSubmit={handleSubmit}>
//           <div className="mb-3">
//             <label htmlFor="recurringUserId" className="form-label">User ID</label>
//             <input
//               type="number"
//               className="form-control"
//               id="recurringUserId"
//               value={userId}
//               onChange={(e) => setUserId(e.target.value)}
//               required
//             />
//           </div>
          
//           <div className="mb-3">
//             <label htmlFor="frequency" className="form-label">Frequency</label>
//             <select 
//               className="form-control" 
//               id="frequency"
//               value={frequency}
//               onChange={(e) => setFrequency(e.target.value)}
//             >
//               <option value="HOURLY">Hourly</option>
//               <option value="DAILY">Daily</option>
//               <option value="WEEKLY">Weekly</option>
//               <option value="MONTHLY">Monthly</option>
//               <option value="YEARLY">Yearly</option>
//             </select>
//           </div>
          
//           <div className="mb-3">
//             <label htmlFor="startTime" className="form-label">Start Time</label>
//             <DatePicker
//               id="startTime"
//               className="form-control"
//               selected={startTime}
//               onChange={(date) => setStartTime(date)}
//               showTimeSelect
//               timeFormat="HH:mm"
//               timeIntervals={5}
//               dateFormat="MMMM d, yyyy h:mm aa"
//               required
//             />
//           </div>
          
//           {frequency === 'WEEKLY' && (
//             <div className="mb-3">
//               <label className="form-label">Days of Week</label>
//               <div className="d-flex flex-wrap">
//                 {weekdays.map(day => (
//                   <div className="form-check me-3" key={day.value}>
//                     <input
//                       className="form-check-input"
//                       type="checkbox"
//                       id={`day-${day.value}`}
//                       checked={daysOfWeek.includes(day.value)}
//                       onChange={() => handleDayOfWeekChange(day.value)}
//                     />
//                     <label className="form-check-label" htmlFor={`day-${day.value}`}>
//                       {day.label}
//                     </label>
//                   </div>
//                 ))}
//               </div>
//             </div>
//           )}
          
//           {frequency === 'MONTHLY' && (
//             <div className="mb-3">
//               <label className="form-label">Days of Month</label>
//               <div className="d-flex flex-wrap">
//                 {monthDays.map(day => (
//                   <div className="form-check me-2" key={day}>
//                     <input
//                       className="form-check-input"
//                       type="checkbox"
//                       id={`month-day-${day}`}
//                       checked={daysOfMonth.includes(day)}
//                       onChange={() => handleDayOfMonthChange(day)}
//                     />
//                     <label className="form-check-label" htmlFor={`month-day-${day}`}>
//                       {day}
//                     </label>
//                   </div>
//                 ))}
//               </div>
//             </div>
//           )}
          
//           <button type="submit" className="btn btn-primary" disabled={loading}>
//             {loading ? 'Scheduling...' : 'Schedule Recurring Job'}
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

// export default RecurringJobScheduler;



//CONVERTED INTO ISOSTRING
// import React, { useState } from 'react';
// import DatePicker from 'react-datepicker';
// import "react-datepicker/dist/react-datepicker.css";
// import moment from 'moment-timezone';
// import api from '../services/api';

// function RecurringJobScheduler() {
//   const [userId, setUserId] = useState('');
//   const [startTime, setStartTime] = useState(new Date());
//   const [selectedTimezone, setSelectedTimezone] = useState('UTC');
//   const [frequency, setFrequency] = useState('DAILY');
//   const [daysOfWeek, setDaysOfWeek] = useState([]);
//   const [daysOfMonth, setDaysOfMonth] = useState([]);
//   const [loading, setLoading] = useState(false);
//   const [message, setMessage] = useState(null);
//   const [error, setError] = useState(null);

//   // List of common timezones
//   const timezones = [
//     'UTC',
//     'America/New_York',      // Eastern Time
//     'America/Chicago',       // Central Time
//     'America/Denver',        // Mountain Time
//     'America/Los_Angeles',   // Pacific Time
//     'Europe/London',         // GMT/BST
//     'Europe/Paris',          // Central European Time
//     'Asia/Tokyo',            // Japan
//     'Asia/Shanghai',         // China
//     'Asia/Kolkata',          // India (IST)
//     'Australia/Sydney',      // Australia Eastern
//     'Pacific/Auckland',      // New Zealand
//   ];

//   const weekdays = [
//     { value: 'MONDAY', label: 'Monday' },
//     { value: 'TUESDAY', label: 'Tuesday' },
//     { value: 'WEDNESDAY', label: 'Wednesday' },
//     { value: 'THURSDAY', label: 'Thursday' },
//     { value: 'FRIDAY', label: 'Friday' },
//     { value: 'SATURDAY', label: 'Saturday' },
//     { value: 'SUNDAY', label: 'Sunday' }
//   ];

//   const monthDays = Array.from({ length: 31 }, (_, i) => i + 1);

//   const handleDayOfWeekChange = (day) => {
//     if (daysOfWeek.includes(day)) {
//       setDaysOfWeek(daysOfWeek.filter(d => d !== day));
//     } else {
//       setDaysOfWeek([...daysOfWeek, day]);
//     }
//   };

//   const handleDayOfMonthChange = (day) => {
//     if (daysOfMonth.includes(day)) {
//       setDaysOfMonth(daysOfMonth.filter(d => d !== day));
//     } else {
//       setDaysOfMonth([...daysOfMonth, day]);
//     }
//   };

//   const handleSubmit = async (e) => {
//     e.preventDefault();
//     setLoading(true);
//     setMessage(null);
//     setError(null);
    
//     try {
//       // Convert the selected time from the user's chosen timezone to IST
//       const localTime = moment(startTime).tz(selectedTimezone);
//       const istTime = localTime.clone().tz('Asia/Kolkata');
      
//       // Format the date to ISO string for the backend
//       const formattedDate = istTime.format();
      
//       console.log(`Original time: ${localTime.format()}`);
//       console.log(`Converted to IST: ${formattedDate}`);
      
//       const recurrence = {
//         frequency,
//         startTime: formattedDate,
//         daysOfWeek,
//         daysOfMonth,
//         monthsOfYear: [] // You can add this if needed
//       };
      
//       const response = await api.scheduleRecurringJob(userId, recurrence);
//       setMessage(`${response} (Scheduled in IST: ${istTime.format('MMMM D, YYYY h:mm A')})`);
//     } catch (err) {
//       setError('Error scheduling recurring job');
//       console.error(err);
//     } finally {
//       setLoading(false);
//     }
//   };

//   return (
//     <div className="card mb-4">
//       <div className="card-header">
//         <h2>Schedule Recurring Job</h2>
//       </div>
//       <div className="card-body">
//         <form onSubmit={handleSubmit}>
//           <div className="mb-3">
//             <label htmlFor="recurringUserId" className="form-label">User ID</label>
//             <input
//               type="number"
//               className="form-control"
//               id="recurringUserId"
//               value={userId}
//               onChange={(e) => setUserId(e.target.value)}
//               required
//             />
//           </div>
          
//           <div className="mb-3">
//             <label htmlFor="frequency" className="form-label">Frequency</label>
//             <select 
//               className="form-control" 
//               id="frequency"
//               value={frequency}
//               onChange={(e) => setFrequency(e.target.value)}
//             >
//               <option value="HOURLY">Hourly</option>
//               <option value="DAILY">Daily</option>
//               <option value="WEEKLY">Weekly</option>
//               <option value="MONTHLY">Monthly</option>
//               <option value="YEARLY">Yearly</option>
//             </select>
//           </div>
          
//           <div className="row mb-3">
//             <div className="col-md-6">
//               <label htmlFor="startTime" className="form-label">Start Time</label>
//               <DatePicker
//                 id="startTime"
//                 className="form-control"
//                 selected={startTime}
//                 onChange={(date) => setStartTime(date)}
//                 showTimeSelect
//                 timeFormat="HH:mm"
//                 timeIntervals={5}
//                 dateFormat="MMMM d, yyyy h:mm aa"
//                 required
//               />
//             </div>
            
//             <div className="col-md-6">
//               <label htmlFor="recTimezone" className="form-label">Timezone</label>
//               <select 
//                 id="recTimezone"
//                 className="form-control"
//                 value={selectedTimezone}
//                 onChange={(e) => setSelectedTimezone(e.target.value)}
//               >
//                 {timezones.map(tz => (
//                   <option key={tz} value={tz}>
//                     {tz.replace('_', ' ')}
//                   </option>
//                 ))}
//               </select>
//             </div>
//           </div>
          
//           {frequency === 'WEEKLY' && (
//             <div className="mb-3">
//               <label className="form-label">Days of Week</label>
//               <div className="d-flex flex-wrap">
//                 {weekdays.map(day => (
//                   <div className="form-check me-3" key={day.value}>
//                     <input
//                       className="form-check-input"
//                       type="checkbox"
//                       id={`day-${day.value}`}
//                       checked={daysOfWeek.includes(day.value)}
//                       onChange={() => handleDayOfWeekChange(day.value)}
//                     />
//                     <label className="form-check-label" htmlFor={`day-${day.value}`}>
//                       {day.label}
//                     </label>
//                   </div>
//                 ))}
//               </div>
//             </div>
//           )}
          
//           {frequency === 'MONTHLY' && (
//             <div className="mb-3">
//               <label className="form-label">Days of Month</label>
//               <div className="d-flex flex-wrap">
//                 {monthDays.map(day => (
//                   <div className="form-check me-2" key={day}>
//                     <input
//                       className="form-check-input"
//                       type="checkbox"
//                       id={`month-day-${day}`}
//                       checked={daysOfMonth.includes(day)}
//                       onChange={() => handleDayOfMonthChange(day)}
//                     />
//                     <label className="form-check-label" htmlFor={`month-day-${day}`}>
//                       {day}
//                     </label>
//                   </div>
//                 ))}
//               </div>
//             </div>
//           )}
          
//           <button type="submit" className="btn btn-primary" disabled={loading}>
//             {loading ? 'Scheduling...' : 'Schedule Recurring Job'}
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

// export default RecurringJobScheduler;


// converted into IST and working
import React, { useState } from 'react';
import DatePicker from 'react-datepicker';
import "react-datepicker/dist/react-datepicker.css";
import moment from 'moment-timezone';
import api from '../services/api';

function RecurringJobScheduler() {
  const [userId, setUserId] = useState('');
  const [startTime, setStartTime] = useState(new Date());
  const [selectedTimezone, setSelectedTimezone] = useState('America/New_York');
  const [frequency, setFrequency] = useState('DAILY');
  const [daysOfWeek, setDaysOfWeek] = useState([]);
  const [daysOfMonth, setDaysOfMonth] = useState([]);
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
      // Convert the selected time from the chosen timezone to IST
    //   const localTime = moment.tz(startTime, selectedTimezone);

      const naive = moment(startTime); // the raw Date picked by user, treated as browser local
      const localTime = moment.tz(
      naive.format('YYYY-MM-DD HH:mm:ss'),
        'YYYY-MM-DD HH:mm:ss',
        selectedTimezone
        );
      const istTime = localTime.clone().tz('Asia/Kolkata');
      const istTimeWithoutOffset = istTime.format('YYYY-MM-DDTHH:mm:ss');


    //   const istTime = localTime.clone().tz('Asia/Kolkata');
      
      console.log(`Converting time from ${selectedTimezone} to IST:`);
      console.log(`- Original: ${localTime.format('YYYY-MM-DD HH:mm:ss')}`);
      console.log(`- IST: ${istTime.format('YYYY-MM-DD HH:mm:ss')}`);
      
      const recurrence = {
        frequency,
        // startTime: istTime.format(), // Send IST time to backend
        startTime: istTime.format('YYYY-MM-DDTHH:mm:ss'),
        daysOfWeek,
        daysOfMonth,
        monthsOfYear: [] // You can add this if needed
      };
      
      const response = await api.scheduleRecurringJob(userId, recurrence);
      setMessage(`${response} (Converted to IST: ${istTime.format('YYYY-MM-DD HH:mm:ss')})`);
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
            <label htmlFor="startTime" className="form-label">
              Start Time ({selectedTimezone})
            </label>
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
            <small className="form-text text-muted">
              This time will be converted to IST before scheduling
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


