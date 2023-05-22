import React, { useMemo } from "react";
import { HOUR_WIDTH } from "../../constants";
import { tempEnd, tempMock, tempStart } from "../../temp/temp";
import { getDaysForHeader } from "../../utils";
import Interval from "../Interval/Interval";

import styles from "./StantionRow.module.scss";

interface IProps {
  stantionData: (typeof tempMock)["results"][number];
}

const StantionRow: React.FC<IProps> = ({ stantionData }) => {
  const daysArr = useMemo(() => {
    return getDaysForHeader(tempStart, tempEnd);
  }, []);

  return (
    <div className={styles.row}>
      {daysArr?.map((el) => (
        <div
          key={el.date}
          className={styles.cell}
          style={{ width: `${el.hours * HOUR_WIDTH}px` }}
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