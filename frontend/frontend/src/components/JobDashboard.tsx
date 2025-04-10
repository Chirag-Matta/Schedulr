
import React from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { ScrollArea } from "@/components/ui/scroll-area";
import { JobPayload } from "@/lib/api";
import { formatDisplayDate } from "@/lib/date-utils";

interface Job extends JobPayload {
  id: string;
}

interface JobDashboardProps {
  jobs: Job[];
}

const JobDashboard: React.FC<JobDashboardProps> = ({ jobs }) => {
  if (jobs.length === 0) {
    return (
      <Card className="border border-border">
        <CardHeader>
          <CardTitle>Scheduled Jobs</CardTitle>
          <CardDescription>No jobs have been scheduled yet</CardDescription>
        </CardHeader>
        <CardContent className="text-center py-8 text-muted-foreground">
          Schedule a job to see it appear here
        </CardContent>
      </Card>
    );
  }

  const getJobTypeString = (job: Job): string => {
    switch (job.jobType) {
      case 'ONE_TIME':
        return 'One-Time';
      case 'RECURRING':
        return 'Recurring';
      case 'DELAYED':
        return 'Delayed Reminder';
      default:
        return job.jobType;
    }
  };

  const getJobScheduleDetails = (job: Job): string => {
    switch (job.jobType) {
      case 'ONE_TIME':
        return job.scheduledTime ? `Scheduled at ${formatDisplayDate(new Date(job.scheduledTime))}` : 'No time specified';
      case 'RECURRING':
        let details = job.startTime ? `Starts at ${formatDisplayDate(new Date(job.startTime))}` : '';
        if (job.frequency) {
          details += `, ${job.frequency.toLowerCase()}`;
          if (job.frequency === 'WEEKLY' && job.weekDays?.length) {
            details += ` on ${job.weekDays.join(', ')}`;
          } else if (job.frequency === 'MONTHLY' && job.monthDays?.length) {
            if (job.monthDays.length > 3) {
              details += ` on days ${job.monthDays.slice(0, 3).join(', ')} and ${job.monthDays.length - 3} more`;
            } else {
              details += ` on day${job.monthDays.length > 1 ? 's' : ''} ${job.monthDays.join(', ')}`;
            }
          }
        }
        return details || 'No schedule details';
      case 'DELAYED':
        return job.delayDuration ? `Delayed by ${job.delayDuration} minute${job.delayDuration > 1 ? 's' : ''}` : 'No delay specified';
      default:
        return 'Unknown schedule type';
    }
  };

  return (
    <Card className="border border-border">
      <CardHeader>
        <CardTitle>Scheduled Jobs</CardTitle>
        <CardDescription>{jobs.length} job{jobs.length !== 1 ? 's' : ''} scheduled</CardDescription>
      </CardHeader>
      <CardContent>
        <ScrollArea className="h-[400px] pr-4">
          <div className="space-y-4">
            {jobs.map((job, index) => (
              <Card key={index} className="overflow-hidden">
                <div className="bg-gradient-to-r from-timeweaver-blue-600 to-timeweaver-blue-500 h-1" />
                <CardContent className="p-4">
                  <div className="flex justify-between items-start">
                    <div>
                      <h3 className="font-semibold text-lg">
                        Job #{job.id.substring(0, 8)}
                      </h3>
                      <p className="text-muted-foreground text-sm mb-2">User ID: {job.userId}</p>
                    </div>
                    <Badge>{getJobTypeString(job)}</Badge>
                  </div>
                  
                  <p className="text-sm mt-2">{getJobScheduleDetails(job)}</p>
                  
                  <div className="mt-3">
                    <h4 className="text-sm font-medium mb-1">Metadata:</h4>
                    <pre className="text-xs bg-gray-50 dark:bg-gray-900 p-2 rounded overflow-x-auto">
                      {JSON.stringify(job.metadata, null, 2)}
                    </pre>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </ScrollArea>
      </CardContent>
    </Card>
  );
};

export default JobDashboard;
