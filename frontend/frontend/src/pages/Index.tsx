
import React, { useState } from 'react';
import Header from '@/components/Header';
import JobScheduler from '@/components/JobScheduler';
import EnhancedJobDashboard from '@/components/EnhancedJobDashboard';
import { JobPayload } from '@/lib/api';
import { Toaster } from 'sonner';
import ExecuteNowButton from '@/components/ExecuteNowButton';

interface Job extends JobPayload {
  id: string;
  ExecuteNowButton?: React.ReactNode;
}

const Index = () => {
  const [scheduledJobs, setScheduledJobs] = useState<Job[]>([]);

  const handleJobScheduled = (job: JobPayload & { id: string }) => {
    // Add the ExecuteNowButton component to the job
    const jobWithExecuteButton: Job = {
      ...job,
      ExecuteNowButton: <ExecuteNowButton jobId={job.id} />
    };
    setScheduledJobs(prev => [jobWithExecuteButton, ...prev]);
  };

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <Toaster position="top-right" />
      <Header />
      
      <main className="flex-1 container mx-auto py-8 px-4">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <div>
            <h2 className="text-2xl font-semibold mb-4">Schedule New Job</h2>
            <JobScheduler onJobScheduled={handleJobScheduled} />
          </div>
          <div>
            <h2 className="text-2xl font-semibold mb-4">Job Dashboard</h2>
            <EnhancedJobDashboard jobs={scheduledJobs} />
          </div>
        </div>
      </main>
      
      <footer className="bg-white border-t py-6">
        <div className="container mx-auto px-4 text-center text-sm text-gray-500">
          &copy; {new Date().getFullYear()} Time Weaver Scheduler - All Rights Reserved
        </div>
      </footer>
    </div>
  );
};

export default Index;
