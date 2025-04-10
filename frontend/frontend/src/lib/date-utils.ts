
import { format, addMinutes, parse } from "date-fns";

export function formatToLocalISOString(date: Date): string {
  return date.toISOString().slice(0, 19); // Removes milliseconds + 'Z'
}

export const formatDisplayDate = (date: Date): string => {
  return format(date, "MMMM d, yyyy h:mm a");
};

export const weekDays = [
  { label: "Monday", value: "MON" },
  { label: "Tuesday", value: "TUE" },
  { label: "Wednesday", value: "WED" },
  { label: "Thursday", value: "THU" },
  { label: "Friday", value: "FRI" },
  { label: "Saturday", value: "SAT" },
  { label: "Sunday", value: "SUN" },
];

export const monthDays = Array.from({ length: 31 }, (_, i) => ({
  label: `${i + 1}`,
  value: `${i + 1}`,
}));

export const frequencies = [
  { label: "Hourly", value: "HOURLY" },
  { label: "Daily", value: "DAILY" },
  { label: "Weekly", value: "WEEKLY" },
  { label: "Monthly", value: "MONTHLY" },
];

export const jobTypes = [
  { label: "One-Time", value: "ONE_TIME" },
  { label: "Recurring", value: "RECURRING" },
  { label: "Delayed Reminder", value: "DELAYED" },
];
