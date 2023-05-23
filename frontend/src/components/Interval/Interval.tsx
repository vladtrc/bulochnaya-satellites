import React from "react";
import {
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
  const startPosition = getIntervalOffset(startAt);
  const width = getIntervalWidth(startAt, endAt);

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
            <div className={styles.name}>Satellite {satelliteName}</div>
            <div className={styles.info}>
              <div className={styles.key}></div>
              <div className={styles.value}></div>
            </div>
            <div className={styles.info}>
              <div className={styles.key}></div>
              <div className={styles.value}></div>
            </div>
          </div>
        }
      >
        <div
          style={{
            width: `${width}px`,
          }}
          className={styles.inner}
        >
          {satelliteName}
        </div>
      </PopoverOverlay>
    </div>
  );
};

export default Interval;
