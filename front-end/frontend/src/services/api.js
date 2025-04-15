import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = {
  // User endpoints
  getUserById: async (userId) => {
    try {
      const response = await axios.get(`${API_BASE_URL}/user_details/${userId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching user:', error);
      throw error;
    }
  },

  // Schedule user fetch with timezone
  scheduleUserFetch: async (userId, scheduledTime, timezone) => {
    try {
      const response = await axios.post(`${API_BASE_URL}/user_details/schedule-fetch`, {
        userId: parseInt(userId, 10), // Convert to number
        scheduledTime,
        timezone
      });
      return response.data;
    } catch (error) {
      console.error('Error scheduling user fetch:', error);
      console.error('Response data:', error.response?.data);
      throw error;
    }
  },

  // Schedule recurring job with timezone in recurrence
  scheduleRecurringJob: async (userId, recurrence) => {
    try {
      // Ensure userId is a number
      const userIdNum = parseInt(userId, 10);
      
      // Format days of month as numbers
      const daysOfMonth = recurrence.daysOfMonth.map(day => 
        typeof day === 'string' ? parseInt(day, 10) : day
      );
      
      // Create base request without monthsOfYear
      const requestData = {
        userId: userIdNum,
        recurrence: {
          frequency: recurrence.frequency,
          startTime: recurrence.startTime,
          timezone: recurrence.timezone,
          daysOfWeek: recurrence.daysOfWeek || [],
          daysOfMonth: daysOfMonth || []
        }
      };
      
      // Only add monthsOfYear for YEARLY frequency
      if (recurrence.frequency === 'YEARLY' && recurrence.monthsOfYear && recurrence.monthsOfYear.length > 0) {
        requestData.recurrence.monthsOfYear = recurrence.monthsOfYear;
      }

      console.log('Sending request to backend:', JSON.stringify(requestData, null, 2));
      
      const response = await axios.post(`${API_BASE_URL}/recurring-jobs/schedule`, requestData);
      return response.data;
    } catch (error) {
      console.error('Error scheduling recurring job:', error);
      console.error('Response details:', error.response?.data);
      throw error;
    }
  },

  getAllJobs: async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/jobs`);
      return response.data;
    } catch (error) {
      console.error('Error fetching jobs:', error);
      throw error;
    }
  },
  executeJob: async (jobId) => {
    try {
      const response = await axios.post(`${API_BASE_URL}/jobs/${jobId}/execute`);
      return response.data;
    } catch (error) {
      console.error('Error executing job:', error);
      throw error;
    }
  },
};

export default api;