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
        userId,
        scheduledTime,
        timezone
      });
      return response.data;
    } catch (error) {
      console.error('Error scheduling user fetch:', error);
      throw error;
    }
  },
  
  // Schedule recurring job with timezone in recurrence
  scheduleRecurringJob: async (userId, recurrence) => {
    try {
      const response = await axios.post(`${API_BASE_URL}/recurring-jobs/schedule`, {
        userId,
        recurrence
      });
      return response.data;
    } catch (error) {
      console.error('Error scheduling recurring job:', error);
      throw error;
    }
  }
};

export default api;