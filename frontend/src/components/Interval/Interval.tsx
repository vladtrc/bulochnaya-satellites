import dayjs from "dayjs";
import React from "react";
import { DurationLabels } from "../../constants";
import { useMeasure } from "../../contexts/MeasureContext";
import {
  getIntervalDuration,
  getIntervalOffset,
  getIntervalWidth,
  stringToColour,
} from "../../utils";
import PopoverOverlay from "../PopoverOverlay/PopoverOverlay";

import styles from "./Interval.module.scss";

interface IProps {
  startAt: string;
  endAt: string;
  satelliteName: string;
}

const Interval: React.FC<IProps> = ({ startAt, endAt, satelliteName }) => {
  const { hourWidth, durationFormat } = useMeasure();

  const startPosition = getIntervalOffset(startAt, hourWidth);
  const width = getIntervalWidth(startAt, endAt, hourWidth);

  return (
    <div
      className={styles.interval}
      style={{
        width: `${width}px`,
        left: `${startPosition}px`,
        background: stringToColour(satelliteName),
      }}
    >
      <PopoverOverlay
        openType="hover"
        placement="bottom"
        popoverBorderColor="primary"
        content={
          <div className={styles.popover}>
            <div className={styles.name}>{satelliteName}</div>
            <div className={styles.info}>
              <div className={styles.key}>Duration:</div>
              <div className={styles.value}>
                {getIntervalDuration(startAt, endAt, durationFormat)}
                &nbsp;
                {DurationLabels[durationFormat]}
              </div>
            </div>
            <div className={styles.info}>
              <div className={styles.key}>Start at:</div>
              <div className={styles.value}>
                {dayjs(startAt).format("YYYY.MM.DD HH:mm")}
              </div>
            </div>
            <div className={styles.info}>
              <div className={styles.key}>End at:</div>
              <div className={styles.value}>
                {dayjs(endAt).format("YYYY.MM.DD HH:mm")}
              </div>
            </div>
            <div className={styles.info}>
              <div className={styles.key}></div>
              <div className={styles.value}></div>
            </div>
          </div>
        }
      >
        <div className={styles.infoIcon}>i</div>
      </PopoverOverlay>
      <div
        style={{
          width: `${width}px`,
        }}
        className={styles.inner}
      >
        {satelliteName}
      </div>
    </div>
  );
};

export default Interval;
