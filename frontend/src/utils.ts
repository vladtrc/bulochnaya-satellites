import { tempStart } from "./temp/temp";
import dayjs from "dayjs";
import { HOUR_WIDTH } from "./constants";

export const getIntervalOffset = (startAt: string) => {
  return dayjs(startAt).diff(tempStart, "hours") * HOUR_WIDTH;
};

export const getIntervalWidth = (startAt: string, endAt: string) => {
  return dayjs(endAt).diff(startAt, "hours") * HOUR_WIDTH;
};

export const getCommonWidth = (startAt: string, endAt: string) => {
  return dayjs(endAt).diff(startAt, "hours") * HOUR_WIDTH;
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

  return Object.entries(datesDict).map(([date, hours]) => ({ date, hours }));
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
