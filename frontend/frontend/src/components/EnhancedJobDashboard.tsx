
import React from 'react';
import { JobPayload } from '@/lib/api';
import { Card, CardContent } from '@/components/ui/card';
import { formatDisplayDate } from '@/lib/date-utils';
import { Badge } from '@/components/ui/badge';

interface Job extends JobPayload {
  id: string;
  ExecuteNowButton?: React.ReactNode;
}

interface JobDashboardProps {
  jobs: Job[];
}

const EnhancedJobDashboard: React.FC<JobDashboardProps> = ({ jobs }) => {
  if (jobs.length === 0) {
    return (
      <Card>
        <CardContent className="p-6 text-center text-muted-foreground">
          No jobs scheduled yet. Use the form to schedule a new job.
        </CardContent>
      </Card>
    );
  }

  return (
    <div className="space-y-4">
      {jobs.map((job) => (
        <Card key={job.id} className="border border-border">
          <CardContent className="p-4">
            <div className="flex items-center justify-between mb-2">
              <Badge variant={getBadgeVariant(job.jobType)}>
                {formatJobType(job.jobType)}
              </Badge>
              {job.ExecuteNowButton}
            </div>
            
            <h3 className="font-medium">User ID: {job.userId}</h3>
            
            {job.scheduledTime && (
              <p className="text-sm text-muted-foreground mt-1">
                Scheduled: {formatDisplayDate(new Date(job.scheduledTime))}
              </p>
            )}
            
            {job.startTime && (
              <p className="text-sm text-muted-foreground mt-1">
                Start Time: {formatDisplayDate(new Date(job.startTime))}
              </p>
            )}
            
            {job.frequency && (
              <p className="text-sm text-muted-foreground">
                Frequency: {formatFrequency(job.frequency)}
                {job.weekDays?.length && job.frequency === "WEEKLY" && (
                  <span> on {job.weekDays.join(", ")}</span>
                )}
                {job.monthDays?.length && job.frequency === "MONTHLY" && (
                  <span> on day {job.monthDays.join(", ")}</span>
                )}
              </p>
            )}
            
            {job.delayDuration !== undefined && (
              <p className="text-sm text-muted-foreground">
                Delay: {job.delayDuration} minutes
              </p>
            )}
            
            {job.kafkaTopic && (
              <div className="mt-2">
                <Badge variant="outline" className="text-xs">
                  {job.kafkaTopic}
                </Badge>
              </div>
            )}
            
            <div className="mt-3 text-xs bg-gray-50 p-2 rounded border">
              <div className="font-semibold mb-1">Metadata:</div>
              <pre className="whitespace-pre-wrap overflow-auto max-h-24">
                {JSON.stringify(job.metadata, null, 2)}
              </pre>
            </div>
          </CardContent>
        </Card>
      ))}
    </div>
  );
};

// Helper functions
const getBadgeVariant = (jobType: string) => {
  switch (jobType) {
    case "ONE_TIME":
      return "default";
    case "RECURRING":
      return "secondary";
    case "DELAYED":
      return "outline";
    default:
      return "default";
  }
};

const formatJobType = (jobType: string) => {
  switch (jobType) {
    case "ONE_TIME":
      return "One-Time";
    case "RECURRING":
      return "Recurring";
    case "DELAYED":
      return "Delayed";
    default:
      return jobType;
  }
};

const formatFrequency = (frequency: string) => {
  switch (frequency) {
    case "HOURLY":
      return "Hourly";
    case "DAILY":
      return "Daily";
    case "WEEKLY":
      return "Weekly";
    case "MONTHLY":
      return "Monthly";
    default:
      return frequency;
  }
};

export default EnhancedJobDashboard;
