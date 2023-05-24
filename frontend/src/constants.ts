export const HOUR_WIDTH = 30;

export enum ScalesEnum {
  "12h" = 12,
  day = 24,
  week = 168,
  month = 744,
}

export enum DurationMeasuresFormats {
  date = "DD:HH:mm",
  days = "days",
  hours = "hours",
  minutes = "minutes",
  seconds = "seconds",
}

export const DurationLabels: Record<string, string> = {
  [DurationMeasuresFormats.date]: DurationMeasuresFormats.date,
  [DurationMeasuresFormats.days]: "days",
  [DurationMeasuresFormats.hours]: "hours",
  [DurationMeasuresFormats.minutes]: "minutes",
  [DurationMeasuresFormats.seconds]: "seconds",
};
