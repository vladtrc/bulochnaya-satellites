import React, { useMemo } from "react";
import { useMeasure } from "../../contexts/MeasureContext";
import { tempEnd, tempStart } from "../../temp/temp";
import { getDaysForHeader } from "../../utils";

import styles from "./TableHeader.module.scss";

const TableHeader = () => {
  const { hourWidth } = useMeasure();

  const daysArr = useMemo(() => {
    return getDaysForHeader(tempStart, tempEnd);
  }, []);

  return (
    <div className={styles.header}>
      {daysArr?.map((el) => (
        <div
          key={el.date}
          className={styles.cell}
          style={{ width: `${el.hours * hourWidth}px` }}
        >
          {el.date}
        </div>
      ))}
    </div>
  );
};

export default TableHeader;
