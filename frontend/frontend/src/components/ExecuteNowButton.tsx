
import React from 'react';
import { Button } from '@/components/ui/button';
import { Play } from 'lucide-react';
import { executeJobImmediately } from '@/lib/api';
import { toast } from 'sonner';

interface ExecuteNowButtonProps {
  jobId: string;
}

const ExecuteNowButton: React.FC<ExecuteNowButtonProps> = ({ jobId }) => {
  const [isExecuting, setIsExecuting] = React.useState(false);
  
  const handleExecuteNow = async () => {
    try {
      setIsExecuting(true);
      await executeJobImmediately(jobId);
      toast.success('Job executed successfully');
    } catch (error) {
      console.error('Failed to execute job:', error);
      toast.error('Failed to execute job');
    } finally {
      setIsExecuting(false);
    }
  };
  
  return (
    <Button 
      variant="outline" 
      size="sm" 
      onClick={handleExecuteNow} 
      disabled={isExecuting}
      className="ml-auto"
    >
      <Play className="h-4 w-4 mr-1" /> Execute Now
    </Button>
  );
};

export default ExecuteNowButton;
