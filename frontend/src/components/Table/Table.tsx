import React, {
  useEffect,
  useLayoutEffect,
  useMemo,
  useRef,
  useState,
} from "react";
import { DurationMeasuresFormats, ScalesEnum } from "../../constants";
import { getCommonWidth } from "../../utils";
import StantionRow from "../StantionRow/StantionRow";
import TableHeader from "../TableHeader/TableHeader";
import Select from "react-select";
import useSWR from "swr";

import styles from "./Table.module.scss";
import { useMeasure } from "../../contexts/MeasureContext";
import { fetcher } from "../../services/fetcher";
import { ISatelliteResponse } from "../../interfaces";

const scaleOptions = [
  { value: ScalesEnum["1h"], label: "1h" },
  { value: ScalesEnum["12h"], label: "12h" },
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

  const algoInput = useRef<HTMLInputElement>(null);
  const [algoName, setAlgoName] = useState("");

  const submitHandler = () => {
    setAlgoName(algoInput.current?.value!);
  };

  const { data } = useSWR<ISatelliteResponse>(
    () => (!!algoName ? `/results/${algoName}` : null),
    fetcher
  );

  useEffect(() => {
    console.log(data);
  }, [data]);

  const aaaa = useMemo<ISatelliteResponse>(() => {
    if (!data) return { results: [], start: "", end: "" };
    return {
      ...data,
      results: [data.results[0]],
    };
  }, [data]);

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
        <div>
          <label className={styles.label}>Algorithm</label>
          <div className={styles.algoField}>
            <input ref={algoInput} className={styles.input} />
            <button onClick={submitHandler} className={styles.submit}>
              Submit
            </button>
          </div>
        </div>
      </div>
      <div className={styles.table}>
        <div className={styles.names}>
          <div className={styles.name}></div>
          {aaaa.results.map((el) => (
            <div className={styles.name} key={el.base}>
              {el.base}
            </div>
          ))}
        </div>
        <div className={styles.content} ref={contentRef}>
          <TableHeader commonEnd={aaaa.end} commonStart={aaaa.start} />
          <div
            style={{
              width: `${getCommonWidth(aaaa.start, aaaa.end, hourWidth)}px`,
            }}
          >
            {aaaa.results.map((el) => (
              <StantionRow
                stantionData={el}
                key={el.base}
                commonEnd={aaaa.end}
                commonStart={aaaa.start}
              />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Table;
