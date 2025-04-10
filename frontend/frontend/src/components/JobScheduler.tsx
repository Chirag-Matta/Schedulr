import React, { useState } from 'react';
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { format } from "date-fns";
import { CalendarIcon, Clock } from "lucide-react";

import { Button } from "@/components/ui/button";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Checkbox } from "@/components/ui/checkbox";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { Calendar } from "@/components/ui/calendar";
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { cn } from "@/lib/utils";
import { toast } from "sonner";
import { formatToLocalISOString, formatDisplayDate, weekDays, monthDays, frequencies, jobTypes } from "@/lib/date-utils";
import { scheduleJob, generateCurlCommand, JobPayload } from "@/lib/api";
import { Card, CardContent } from "@/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";

const formSchema = z.object({
  userId: z.string().min(1, "User ID is required"),
  jobType: z.enum(["ONE_TIME", "RECURRING", "DELAYED"]),
  scheduledDateTime: z.date().optional(),
  startDateTime: z.date().optional(),
  frequency: z.string().optional(),
  weekDays: z.array(z.string()).optional(),
  monthDays: z.array(z.string()).optional(),
  delayMinutes: z.coerce.number().min(1).optional(),
});

type FormValues = z.infer<typeof formSchema>;

interface JobSchedulerProps {
  onJobScheduled: (job: JobPayload & { id: string }) => void;
}

const JobScheduler = ({ onJobScheduled }: JobSchedulerProps) => {
  const [previewCurl, setPreviewCurl] = useState<string | null>(null);

  const form = useForm<FormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      userId: "",
      jobType: "ONE_TIME",
      weekDays: [],
      monthDays: [],
    },
  });

  const jobType = form.watch("jobType");
  const frequency = form.watch("frequency");

  const renderJobTypeFields = () => {
    switch (jobType) {
      case "ONE_TIME":
        return (
          <FormField
            control={form.control}
            name="scheduledDateTime"
            render={({ field }) => (
              <FormItem className="flex flex-col">
                <FormLabel>Scheduled Time</FormLabel>
                <Popover>
                  <PopoverTrigger asChild>
                    <FormControl>
                      <Button
                        variant={"outline"}
                        className={cn(
                          "w-full pl-3 text-left font-normal",
                          !field.value && "text-muted-foreground"
                        )}
                      >
                        {field.value ? (
                          format(field.value, "PPP HH:mm")
                        ) : (
                          <span>Pick a date and time</span>
                        )}
                        <CalendarIcon className="ml-auto h-4 w-4 opacity-50" />
                      </Button>
                    </FormControl>
                  </PopoverTrigger>
                  <PopoverContent className="w-auto p-0" align="start">
                    <Calendar
                      mode="single"
                      selected={field.value}
                      onSelect={field.onChange}
                      initialFocus
                      className={cn("p-3 pointer-events-auto")}
                    />
                    <div className="p-3 border-t border-border">
                      <Input
                        type="time"
                        value={field.value ? format(field.value, "HH:mm") : ""}
                        onChange={(e) => {
                          if (field.value && e.target.value) {
                            const [hours, minutes] = e.target.value.split(':').map(Number);
                            const newDate = new Date(field.value);
                            newDate.setHours(hours);
                            newDate.setMinutes(minutes);
                            field.onChange(newDate);
                          }
                        }}
                      />
                    </div>
                  </PopoverContent>
                </Popover>
                <FormDescription>
                  Select when the one-time job should run
                </FormDescription>
                <FormMessage />
              </FormItem>
            )}
          />
        );
      case "RECURRING":
        return (
          <>
            <FormField
              control={form.control}
              name="startDateTime"
              render={({ field }) => (
                <FormItem className="flex flex-col">
                  <FormLabel>Start Time</FormLabel>
                  <Popover>
                    <PopoverTrigger asChild>
                      <FormControl>
                        <Button
                          variant={"outline"}
                          className={cn(
                            "w-full pl-3 text-left font-normal",
                            !field.value && "text-muted-foreground"
                          )}
                        >
                          {field.value ? (
                            format(field.value, "PPP HH:mm")
                          ) : (
                            <span>Pick a start date and time</span>
                          )}
                          <CalendarIcon className="ml-auto h-4 w-4 opacity-50" />
                        </Button>
                      </FormControl>
                    </PopoverTrigger>
                    <PopoverContent className="w-auto p-0" align="start">
                      <Calendar
                        mode="single"
                        selected={field.value}
                        onSelect={field.onChange}
                        initialFocus
                        className={cn("p-3 pointer-events-auto")}
                      />
                      <div className="p-3 border-t border-border">
                        <Input
                          type="time"
                          value={field.value ? format(field.value, "HH:mm") : ""}
                          onChange={(e) => {
                            if (field.value && e.target.value) {
                              const [hours, minutes] = e.target.value.split(':').map(Number);
                              const newDate = new Date(field.value);
                              newDate.setHours(hours);
                              newDate.setMinutes(minutes);
                              field.onChange(newDate);
                            }
                          }}
                        />
                      </div>
                    </PopoverContent>
                  </Popover>
                  <FormDescription>
                    Select when the recurring job should start
                  </FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="frequency"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Frequency</FormLabel>
                  <Select onValueChange={field.onChange} defaultValue={field.value}>
                    <FormControl>
                      <SelectTrigger>
                        <SelectValue placeholder="Select frequency" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      <SelectGroup>
                        {frequencies.map((freq) => (
                          <SelectItem key={freq.value} value={freq.value}>{freq.label}</SelectItem>
                        ))}
                      </SelectGroup>
                    </SelectContent>
                  </Select>
                  <FormDescription>How often the job should run</FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />
            {frequency === "WEEKLY" && (
              <FormField
                control={form.control}
                name="weekDays"
                render={() => (
                  <FormItem>
                    <div className="mb-4">
                      <FormLabel className="text-base">Days of week</FormLabel>
                      <FormDescription>
                        Select which days of the week to run the job
                      </FormDescription>
                    </div>
                    <div className="grid grid-cols-2 md:grid-cols-4 gap-2">
                      {weekDays.map((day) => (
                        <FormField
                          key={day.value}
                          control={form.control}
                          name="weekDays"
                          render={({ field }) => {
                            return (
                              <FormItem
                                key={day.value}
                                className="flex flex-row items-start space-x-3 space-y-0"
                              >
                                <FormControl>
                                  <Checkbox
                                    checked={field.value?.includes(day.value)}
                                    onCheckedChange={(checked) => {
                                      const currentValues = field.value || [];
                                      return checked
                                        ? field.onChange([...currentValues, day.value])
                                        : field.onChange(
                                            currentValues.filter(
                                              (value) => value !== day.value
                                            )
                                          );
                                    }}
                                  />
                                </FormControl>
                                <FormLabel className="font-normal">
                                  {day.label}
                                </FormLabel>
                              </FormItem>
                            );
                          }}
                        />
                      ))}
                    </div>
                    <FormMessage />
                  </FormItem>
                )}
              />
            )}
            {frequency === "MONTHLY" && (
              <FormField
                control={form.control}
                name="monthDays"
                render={() => (
                  <FormItem>
                    <div className="mb-4">
                      <FormLabel className="text-base">Days of month</FormLabel>
                      <FormDescription>
                        Select which days of the month to run the job
                      </FormDescription>
                    </div>
                    <div className="grid grid-cols-4 md:grid-cols-8 gap-2">
                      {monthDays.map((day) => (
                        <FormField
                          key={day.value}
                          control={form.control}
                          name="monthDays"
                          render={({ field }) => {
                            return (
                              <FormItem
                                key={day.value}
                                className="flex flex-row items-start space-x-3 space-y-0"
                              >
                                <FormControl>
                                  <Checkbox
                                    checked={field.value?.includes(day.value)}
                                    onCheckedChange={(checked) => {
                                      const currentValues = field.value || [];
                                      return checked
                                        ? field.onChange([...currentValues, day.value])
                                        : field.onChange(
                                            currentValues.filter(
                                              (value) => value !== day.value
                                            )
                                          );
                                    }}
                                  />
                                </FormControl>
                                <FormLabel className="font-normal">
                                  {day.label}
                                </FormLabel>
                              </FormItem>
                            );
                          }}
                        />
                      ))}
                    </div>
                    <FormMessage />
                  </FormItem>
                )}
              />
            )}
          </>
        );
      case "DELAYED":
        return (
          <FormField
            control={form.control}
            name="delayMinutes"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Delay (Minutes)</FormLabel>
                <FormControl>
                <Input
                  type="number"
                  min={1}
                  value={field.value ?? ''}
                  onChange={field.onChange}
                />

                </FormControl>
                <FormDescription>
                  How many minutes to delay before sending the reminder
                </FormDescription>
                <FormMessage />
              </FormItem>
            )}
          />
        );
      default:
        return null;
    }
  };

  const buildPayload = (values: FormValues): JobPayload => {
    const metadata = { event: "user_data_fetch" };
    const kafkaTopic = "user-events"; // Hardcoded kafka topic
    
    const payload: JobPayload = {
      userId: values.userId,
      jobType: values.jobType,
      metadata,
      kafkaTopic, // Always use the hardcoded topic
    };

    switch (values.jobType) {
      case "ONE_TIME":
        if (values.scheduledDateTime) {
          payload.scheduledTime = formatToLocalISOString(values.scheduledDateTime);
        }
        break;
      case "RECURRING":
        if (values.startDateTime) {
          payload.startTime = formatToLocalISOString(values.startDateTime);
        }
        payload.frequency = values.frequency;
        if (values.frequency === "WEEKLY" && values.weekDays?.length) {
          payload.weekDays = values.weekDays;
        }
        if (values.frequency === "MONTHLY" && values.monthDays?.length) {
          payload.monthDays = values.monthDays;
        }
        break;
      case "DELAYED":
        payload.delayDuration = values.delayMinutes;
        break;
    }

    return payload;
  };

  const previewApiCall = () => {
    const values = form.getValues();
    try {
      const payload = buildPayload(values);
      const curl = generateCurlCommand(payload);
      setPreviewCurl(curl);
    } catch (error) {
      console.error("Error generating preview:", error);
      toast.error("Could not generate preview due to invalid form data");
    }
  };

  const onSubmit = async (values: FormValues) => {
    try {
      const payload = buildPayload(values);
      const response = await scheduleJob(payload);
      const responseData = await response.json();
      
      toast.success("Job successfully scheduled!");
      onJobScheduled({ ...payload, id: responseData.jobId });
      setPreviewCurl(null);
    } catch (error) {
      console.error("Error scheduling job:", error);
      toast.error("Failed to schedule job");
    }
  };

  return (
    <Card className="border border-border">
      <CardContent className="p-6">
        <Tabs defaultValue="form">
          <TabsList className="w-full mb-6">
            <TabsTrigger value="form" className="w-1/2">Schedule Job</TabsTrigger>
            <TabsTrigger value="curl" className="w-1/2">API Preview</TabsTrigger>
          </TabsList>
          <TabsContent value="form">
            <Form {...form}>
              <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
                <FormField
                  control={form.control}
                  name="userId"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>User ID</FormLabel>
                      <FormControl>
                        <Input placeholder="Enter user ID" {...field} />
                      </FormControl>
                      <FormDescription>
                        The ID of the user to fetch data for
                      </FormDescription>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="jobType"
                  render={({ field }) => (
                    <FormItem className="space-y-3">
                      <FormLabel>Job Type</FormLabel>
                      <FormControl>
                        <RadioGroup
                          onValueChange={field.onChange}
                          defaultValue={field.value}
                          className="flex flex-col space-y-1"
                        >
                          {jobTypes.map((type) => (
                            <FormItem key={type.value} className="flex items-center space-x-3 space-y-0">
                              <FormControl>
                                <RadioGroupItem value={type.value} />
                              </FormControl>
                              <FormLabel className="font-normal">
                                {type.label}
                              </FormLabel>
                            </FormItem>
                          ))}
                        </RadioGroup>
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                {renderJobTypeFields()}

                <div className="flex space-x-2 pt-4">
                  <Button 
                    type="button" 
                    variant="outline" 
                    onClick={previewApiCall}
                    className="flex items-center gap-1"
                  >
                    <Clock className="h-4 w-4" />
                    Preview API Call
                  </Button>
                  <Button type="submit">Schedule Job</Button>
                </div>
              </form>
            </Form>
          </TabsContent>
          <TabsContent value="curl">
            {previewCurl ? (
              <div className="bg-gray-100 dark:bg-gray-800 p-4 rounded-md">
                <h3 className="text-sm font-medium mb-2">cURL Command:</h3>
                <pre className="text-xs overflow-x-auto p-2 bg-white dark:bg-gray-900 rounded border border-gray-200 dark:border-gray-700">
                  {previewCurl}
                </pre>
              </div>
            ) : (
              <div className="p-6 text-center">
                <p className="text-muted-foreground">
                  Fill out the form and click "Preview API Call" to see the cURL command
                </p>
              </div>
            )}
          </TabsContent>
        </Tabs>
      </CardContent>
    </Card>
  );
};

export default JobScheduler;
