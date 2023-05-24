import React, { useLayoutEffect, useRef, useState } from "react";
import { DurationMeasuresFormats, ScalesEnum } from "../../constants";
import { tempEnd, tempMock, tempStart } from "../../temp/temp";
import { getCommonWidth } from "../../utils";
import StantionRow from "../StantionRow/StantionRow";
import TableHeader from "../TableHeader/TableHeader";
import Select from "react-select";

import styles from "./Table.module.scss";
import { useMeasure } from "../../contexts/MeasureContext";

const scaleOptions = [
  { value: ScalesEnum.day, label: "Day" },
  { value: ScalesEnum.week, label: "Week" },
  { value: ScalesEnum.month, label: "Month" },
];

const durationMeasureOptions = [
  { value: DurationMeasuresFormats.date, label: "Full format" },
  { value: DurationMeasuresFormats.days, label: "Days" },
  { value: DurationMeasuresFormats.hours, label: "Hours" },
  { value: DurationMeasuresFormats.minutes, label: "Minutes" },
  { value: DurationMeasuresFormats.seconds, label: "Seconds" },
];

const Table = () => {
  const contentRef = useRef<HTMLDivElement>(null);

  const { hourWidth, onChangeHourWidth, onChangeDurationFormat } = useMeasure();

  const [scale, setScale] = useState(scaleOptions[0]);
  const [durationMeasure, setDurationMeasure] = useState(
    durationMeasureOptions[0]
  );

  useLayoutEffect(() => {
    if (!contentRef.current) return;
    const contentWidth = contentRef.current?.offsetWidth;
    onChangeHourWidth(contentWidth / scale.value);
  }, []);

  const changeScale = (dimension: ScalesEnum) => {
    if (!contentRef.current) return;
    const contentWidth = contentRef.current?.offsetWidth;
    onChangeHourWidth(contentWidth / dimension);
  };

  return (
    <div>
      <div className={styles.settings}>
        <div>
          <label className={styles.label}>Scale</label>
          <Select
            options={scaleOptions}
            value={scale}
            onChange={(v) => {
              setScale(v!);
              changeScale(v?.value!);
            }}
          />
        </div>
        <div>
          <label className={styles.label}>Duration measure</label>
          <Select
            options={durationMeasureOptions}
            value={durationMeasure}
            onChange={(v) => {
              setDurationMeasure(v!);
              onChangeDurationFormat(v?.value!);
            }}
          />
        </div>
      </div>
      <div className={styles.table}>
        <div className={styles.names}>
          <div className={styles.name}></div>
          {tempMock.results.map((el) => (
            <div className={styles.name} key={el.base}>
              {el.base}
            </div>
          ))}
        </div>
        <div className={styles.content} ref={contentRef}>
          <TableHeader />
          <div
            style={{
              width: `${getCommonWidth(tempStart, tempEnd, hourWidth)}px`,
            }}
          >
            {tempMock.results.map((el) => (
              <StantionRow stantionData={el} key={el.base} />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Table;
