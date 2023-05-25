import React, { useMemo } from "react";
import { useMeasure } from "../../contexts/MeasureContext";
import { getDaysForHeader } from "../../utils";

import styles from "./TableHeader.module.scss";

interface IProps {
  commonStart: string;
  commonEnd: string;
}

const TableHeader: React.FC<IProps> = ({ commonEnd, commonStart }) => {
  const { hourWidth } = useMeasure();

  const daysArr = useMemo(() => {
    return getDaysForHeader(commonStart, commonEnd);
  }, [commonStart, commonEnd]);

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
