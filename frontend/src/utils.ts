import dayjs from "dayjs";

import duration from "dayjs/plugin/duration";
import { DurationMeasuresFormats } from "./constants";

dayjs.extend(duration);

export const getIntervalOffset = (
  startAt: string,
  hourWidth: number,
  commonStart: string
) => {
  return dayjs(startAt).diff(commonStart, "hours") * hourWidth;
};

export const getIntervalWidth = (
  startAt: string,
  endAt: string,
  hourWidth: number
) => {
  return dayjs(endAt).diff(startAt, "hours") * hourWidth;
};

export const getCommonWidth = (
  startAt: string,
  endAt: string,
  hourWidth: number
) => {
  return dayjs(endAt).diff(startAt, "hours") * hourWidth;
};

export const getDaysForHeader = (startAt: string, endAt: string) => {
  const hoursDiff = dayjs(endAt).diff(startAt, "hours");

  let dateCursor = dayjs(startAt);

  const datesDict: Record<string, number> = {};

  for (let i = 0; i < hoursDiff; i++) {
    const dateString = dateCursor.format("YYYY-MM-DD");
    if (!datesDict[dateString]) {
      datesDict[dateString] = 1;
    } else {
      datesDict[dateString]++;
    }

    dateCursor = dayjs(dateCursor).add(1, "hours");
  }

  return Object.entries(datesDict).map(([date, hours]) => ({
    date: dayjs(date).format("DD.MM"),
    hours,
  }));
};

export const stringToColour = function (str: string) {
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    hash = str.charCodeAt(i) + ((hash << 5) - hash);
  }
  let colour = "#";
  for (let i = 0; i < 3; i++) {
    let value = (hash >> (i * 8)) & 0xff;
    colour += ("00" + value.toString(16)).substr(-2);
  }
  return colour;
};

export const getIntervalDuration = (
  dateStart: string,
  dateEnd: string,
  format: string
) => {
  const start = dayjs(dateStart);
  const end = dayjs(dateEnd);
  const diff = end.diff(start, "milliseconds");

  switch (format) {
    case DurationMeasuresFormats.date:
      return dayjs.duration(diff, "milliseconds").format(format);

    case DurationMeasuresFormats.days:
      return diff / (24 * 60 * 60 * 1000);

    case DurationMeasuresFormats.hours:
      return diff / (60 * 60 * 1000);

    case DurationMeasuresFormats.minutes:
      return diff / (60 * 1000);

    case DurationMeasuresFormats.seconds:
      return diff / 1000;
  }
};
