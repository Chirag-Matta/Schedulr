export interface RecurrencePattern {
  frequency: 'HOURLY' | 'DAILY' | 'WEEKLY' | 'MONTHLY' | 'YEARLY';
  startTime: string;
  daysOfWeek?: string[];
  daysOfMonth?: number[];
  monthsOfYear?: string[];
}

export interface JobPayload {
  userId: number;
  jobType?: 'ONE_TIME' | 'RECURRING' | 'DELAYED';
  scheduledTime?: string;
  startTime?: string;
  frequency?: string;
  weekDays?: string[];
  monthDays?: number[];
  monthsOfYear?: string[];
  delayDuration?: number;
  metadata?: Record<string, unknown>;
  kafkaTopic?: string;
  // New fields from curl commands
  recurrence?: {
    frequency: string;
    startTime: string;
    daysOfWeek?: string[];
    daysOfMonth?: number[];
    monthsOfYear?: string[];
  };
}

export const scheduleJob = async (payload: JobPayload): Promise<Response> => {
  let endpoint = '';
  let apiPayload = { ...payload };
  
  // Determine endpoint based on jobType first (backward compatibility)
  if (payload.jobType) {
    switch (payload.jobType) {
      case 'ONE_TIME':
        endpoint = '/api/user_details/schedule-fetch';
        // Ensure scheduledTime is set for ONE_TIME jobs
        if (payload.startTime && !payload.scheduledTime) {
          apiPayload.scheduledTime = payload.startTime;
        }
        break;
      case 'RECURRING':
        endpoint = '/api/recurring-jobs/schedule';
        // Convert to recurrence format if needed
        if (!payload.recurrence && payload.frequency) {
          apiPayload.recurrence = {
            frequency: payload.frequency,
            startTime: payload.startTime || new Date().toISOString(),
            daysOfWeek: payload.weekDays,
            daysOfMonth: payload.monthDays?.map(Number),
            monthsOfYear: payload.monthsOfYear
          };
        }
        break;
      case 'DELAYED':
        endpoint = '/api/reminders/schedule';
        break;
      default:
        throw new Error(`Invalid job type: ${payload.jobType}`);
    }
  } 
  // If no jobType but has recurrence, it's a recurring job
  else if (payload.recurrence) {
    endpoint = '/api/recurring-jobs/schedule';
  } 
  // If scheduledTime exists, it's a one-time job
  else if (payload.scheduledTime) {
    endpoint = '/api/user_details/schedule-fetch';
  }
  // If startTime exists (but not scheduledTime), treat as scheduledTime
  else if (payload.startTime) {
    endpoint = '/api/user_details/schedule-fetch';
    apiPayload.scheduledTime = payload.startTime;
  }
  // If frequency exists, it's a recurring job
  else if (payload.frequency) {
    endpoint = '/api/recurring-jobs/schedule';
    apiPayload.recurrence = {
      frequency: payload.frequency,
      startTime: payload.startTime || new Date().toISOString(),
      daysOfWeek: payload.weekDays,
      daysOfMonth: payload.monthDays?.map(Number),
      monthsOfYear: payload.monthsOfYear
    };
  }
  else {
    throw new Error('Invalid job configuration: must provide either jobType, scheduledTime, recurrence, or frequency+startTime');
  }

  // Make API call to the correct endpoint
  const apiUrl = `http://localhost:8080${endpoint}`;
  console.log(`Making API call to: ${apiUrl}`, apiPayload);
  
  try {
    const response = await fetch(apiUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(apiPayload)
    });
    
    return response;
  } catch (error) {
    console.error('Error scheduling job:', error);
    throw error;
  }
};

export const generateCurlCommand = (payload: JobPayload): string => {
  let endpoint = '';
  
  if (payload.scheduledTime) {
    // ONE_TIME job
    endpoint = '/api/user_details/schedule-fetch';
  } else if (payload.recurrence) {
    // RECURRING job
    endpoint = '/api/recurring-jobs/schedule';
  } else {
    throw new Error('Invalid job configuration: must provide either scheduledTime or recurrence');
  }
  
  const jsonPayload = JSON.stringify(payload, null, 2);
  
  return `curl -X POST \\
  "http://localhost:8080${endpoint}" \\
  -H "Content-Type: application/json" \\
  -d '${jsonPayload}'`;
};

export const executeJobImmediately = async (jobId: string): Promise<Response> => {
  console.log(`Executing job ${jobId} immediately`);
  
  const apiUrl = `http://localhost:8080/api/jobs/${jobId}/execute`;
  
  try {
    const response = await fetch(apiUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      }
    });
    
    return response;
  } catch (error) {
    console.error('Error executing job:', error);
    throw error;
  }
};

export const getUserDetails = async (userId: number): Promise<Response> => {
  console.log(`Fetching details for user ${userId}`);
  
  const apiUrl = `http://localhost:8080/api/user_details/${userId}`;
  
  try {
    const response = await fetch(apiUrl, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    });
    
    return response;
  } catch (error) {
    console.error('Error fetching user details:', error);
    throw error;
  }
};