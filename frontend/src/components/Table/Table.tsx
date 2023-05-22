import React from "react";
import { tempEnd, tempMock, tempStart } from "../../temp/temp";
import { getCommonWidth } from "../../utils";
import StantionRow from "../StantionRow/StantionRow";
import TableHeader from "../TableHeader/TableHeader";

import styles from "./Table.module.scss";

const Table = () => {
  return (
    <div className={styles.table}>
      <div className={styles.names}>
        <div className={styles.name}></div>
        {tempMock.results.map((el) => (
          <div className={styles.name} key={el.base}>
            {el.base}
          </div>
        ))}
      </div>
      <div className={styles.content}>
        <TableHeader />
        <div style={{ width: `${getCommonWidth(tempStart, tempEnd)}px` }}>
          {tempMock.results.map((el) => (
            <StantionRow stantionData={el} key={el.base} />
          ))}
        </div>
      </div>
    </div>
  );
};

export default Table;
