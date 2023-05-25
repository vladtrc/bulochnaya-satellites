import React, { useMemo } from "react";
import { useMeasure } from "../../contexts/MeasureContext";
import { ISatelliteResponse } from "../../interfaces";
import { getDaysForHeader } from "../../utils";
import Interval from "../Interval/Interval";

import styles from "./StantionRow.module.scss";

interface IProps {
  stantionData: ISatelliteResponse["results"][number];
  commonStart: string;
  commonEnd: string;
}

const StantionRow: React.FC<IProps> = ({
  stantionData,
  commonEnd,
  commonStart,
}) => {
  const { hourWidth } = useMeasure();

  const daysArr = useMemo(() => {
    return getDaysForHeader(commonStart, commonEnd);
  }, []);

  return (
    <div className={styles.row}>
      {daysArr?.map((el) => (
        <div
          key={el.date}
          className={styles.cell}
          style={{ width: `${el.hours * hourWidth}px` }}
        ></div>
      ))}
      {stantionData.usage.map((el, i) => (
        <Interval
          key={el.satelliteName + i}
          startAt={el.start}
          endAt={el.end}
          satelliteName={el.satelliteName}
        />
      ))}
    </div>
  );
};

export default StantionRow;
